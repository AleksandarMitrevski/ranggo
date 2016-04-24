package mk.finki.ranggo.aggregator.crawlers.impl;

import mk.finki.ranggo.aggregator.crawlers.Crawler;
import mk.finki.ranggo.aggregator.helper.HelperClass;
import mk.finki.ranggo.aggregator.yandex.YandexTranslator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Simona on 4/11/2016.
 */
public class DnevnikCrawler implements Crawler {

    private static String baseURL = "http://www.dnevnik.mk/";

    public static void main(String[] args){
        DnevnikCrawler crawler = new DnevnikCrawler();
        crawler.crawl();
    }

    public void crawl() {
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/dnevnik.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");

            String url = baseURL + "#panel-1";
            System.out.println("URL: " + url);
            URLConnection conn = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            String html = sb.toString();

            TagNode tagNode = new HtmlCleaner().clean(html);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@class,'anythingSlider')]/div/ul/li[not(contains(@class,'cloned'))]/div", doc, XPathConstants.NODESET);
            System.out.println("news length: " + tableRows.getLength());
            for(int i=0;i<tableRows.getLength();i++){
                Node node = tableRows.item(i);
                String newsURL = (String)xpathObj.evaluate("./a/@href",node, XPathConstants.STRING);
                newsURL = baseURL + newsURL.trim();
                System.out.println("\t\t" + newsURL);
                if(!extractDataFromPage(newsURL, writer)){
                    break;
                }
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            crawl();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean extractDataFromPage(String url, OutputStreamWriter writer) {
        try {
            URLConnection conn = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            String html = sb.toString();

            TagNode tagNode = new HtmlCleaner().clean(html);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            String date = (String) xpathObj.evaluate("//*[contains(@class,'WB_DNEVNIK_Datum2')]//text()", doc, XPathConstants.STRING);
            String title = (String) xpathObj.evaluate("//*[contains(@id,'ArticleTitle')]//text()", doc, XPathConstants.STRING);
            String shortText = (String)xpathObj.evaluate("//*[contains(@id,'ArticleSummary')]//text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList)xpathObj.evaluate("//body/div[2]/div[2]/table/tbody/tr/td/div/div/table/tbody/tr/td/table[4]/tbody/tr/td[1]/table/tbody/tr[2]/td/div[9]/table/tbody/tr/td/p", doc, XPathConstants.NODESET);
            int length = textNodes.getLength();
            int count = 0;
            for(int i=0;i<textNodes.getLength();i++){
                Node node = textNodes.item(i);
                String img = (String)xpathObj.evaluate("./img/@src",node, XPathConstants.STRING);
                if(!img.equals("")){
                    count = i;
                    break;
                }
            }

            String text = "";
            for(int i=count;i<textNodes.getLength();i++){
                Node node = textNodes.item(i);
                text += node.getTextContent();
            }

            date = StringEscapeUtils.unescapeHtml4(date).trim();
            if(!checkDate(date)){
                return false;
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text, "mk", "en");

            shortText = StringEscapeUtils.unescapeHtml4(shortText).trim();
            String translatedShortText = YandexTranslator.translate(shortText, "mk", "en");

            String source = "Dnevnik";
            System.out.println(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            writer.write(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            return true;
        }catch(SocketTimeoutException e){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return extractDataFromPage(url, writer);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkDate(String fullDate){
        try {
            String[] partTime = fullDate.split(":");
            String dateTime = partTime[1].trim();
            String date = dateTime.split(",")[0];
            String[] parts = date.split("\\.");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            String today = HelperClass.getToday();
            System.out.println("From text: " + year + "-" + month + "-" + day);
            System.out.println("Original: " + year + "-" + month + "-" + day);
            return (year + "-" + month + "-" + day).equals(today);
        }catch(Exception e){

        }
        return false;
    }
}
