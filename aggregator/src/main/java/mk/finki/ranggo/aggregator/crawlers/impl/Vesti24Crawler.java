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
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simona on 4/11/2016.
 */
public class Vesti24Crawler implements Crawler {
    private static String baseURL = "http://24vesti.mk";
    private List<String> categories;

    public Vesti24Crawler(){
        categories = new ArrayList<String>();
        categories.add("makedonija");
        categories.add("sloboda-na-mediumi");
        categories.add("ekonomija");
        categories.add("vesti/region");
        categories.add("svet");
        categories.add("shoubiz");
        categories.add("zanimlivosti");
        categories.add("kultura");
        categories.add("sport");
        categories.add("tehnologija");
    }

    public static void main(String[] args){
        Vesti24Crawler crawler = new Vesti24Crawler();
        crawler.crawl();
    }

    public void crawl() {
        OutputStreamWriter writer =  null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/24vesti.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");

            for(int i=0;i<categories.size();i++){
                String category = categories.get(i);
                int count = 0;
                boolean flag = true;
                while(flag){
                    try {
                        String url = baseURL + "/" +  category + "?page=" + count;
                        System.out.println("BASE URL: " + url);
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
                        NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@class,'view-content')]/table/tbody/tr", doc, XPathConstants.NODESET);
                        for(int j=0;j<tableRows.getLength();j++){
                            Node node = tableRows.item(j);
                            String date = (String)xpathObj.evaluate("./td/div[2]/span/text()", node, XPathConstants.STRING);
                            if(!checkDate(date)){
                                flag = false;
                                break;
                            }
                            String newsURL = (String)xpathObj.evaluate("./td/div[1]/span/a/@href", node, XPathConstants.STRING);
                            newsURL = baseURL + newsURL;
                            System.out.println("\t\t" + newsURL);
                            extractDataFromPage(newsURL, date, writer);
                        }

                    } catch(SocketTimeoutException e){
                        e.printStackTrace();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        count--;
                    } catch(ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (XPathExpressionException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void extractDataFromPage(String url, String date, OutputStreamWriter writer){
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
            String title = (String) xpathObj.evaluate("//div[contains(@class,'main-content')]/div/h1[contains(@class,'article-title')]/text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList)xpathObj.evaluate("//div[contains(@class,'main-content')]/div/div[contains(@class,'content')]/div", doc, XPathConstants.NODESET);
            String text = "";
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text, "mk", "en");

            date = StringEscapeUtils.unescapeHtml4(date).trim();
            String shortText = "";
            String translatedShortText = "";

            String source = "24 VESTI";
            System.out.println(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            writer.write(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");

        } catch(SocketTimeoutException e){
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            extractDataFromPage(url, date, writer);
            return;
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDate(String dateTime){
        try {
            String[] partTime = dateTime.split("-");
            String date = partTime[0].trim();
            String[] parts = date.split("\\.");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            String today = HelperClass.getToday();
            return (year + "-" + month + "-" + day).equals(today);
        }catch (Exception e){
            return false;
        }
    }
}
