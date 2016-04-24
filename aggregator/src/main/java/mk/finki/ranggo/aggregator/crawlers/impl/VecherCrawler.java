package mk.finki.ranggo.aggregator.crawlers.impl;

import mk.finki.ranggo.aggregator.crawlers.Crawler;
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
 * Created by Simona on 4/10/2016.
 */
public class VecherCrawler implements Crawler{

    private static String baseURL = "http://vecer.mk";
    private List<String> categories;

    public VecherCrawler(){
        categories = new ArrayList<String>();
        categories.add("makedonija");
        categories.add("balkan");
        categories.add("svet");
        categories.add("skopska");
        categories.add("ekonomija");
        categories.add("kultura");
        categories.add("zabavna");
        categories.add("sport");
    }

    public static void main(String[] args){
        VecherCrawler crawler = new VecherCrawler();
        crawler.crawl();
    }

    public void crawl() {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/vecher.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");

            for(int i=0;i<categories.size();i++){
                try {
                    String url = baseURL + "/" +  categories.get(i);
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
                    NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@class,'view-content')]/div[contains(@class,'views-group')][1]/div", doc, XPathConstants.NODESET);
                    for(int j=0;j<tableRows.getLength();j++){
                        Node node = tableRows.item(j);
                        String newsURL = (String)xpathObj.evaluate("./div/h3/a/@href", node, XPathConstants.STRING);
                        newsURL = baseURL + newsURL;
                        System.out.println("\t\tNews url: " + newsURL);
                        extractDataFromPage(newsURL, writer);
                    }

                } catch(SocketTimeoutException ex){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //go back
                    i--;
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
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

    private static void extractDataFromPage(String url, OutputStreamWriter writer){
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
            String shortText = (String) xpathObj.evaluate("//div[contains(@id,'block-system-main')]/div/div/div[1]/div/div[1]//text()", doc, XPathConstants.STRING);
            String title = (String) xpathObj.evaluate("//div[contains(@id,'block-system-main')]/div/div/div[1]/div/h1//text()", doc, XPathConstants.STRING);
            String date = (String) xpathObj.evaluate("//div[contains(@id,'block-system-main')]/div/div/div[1]/div/div[2]/div[2]//text()", doc, XPathConstants.STRING);
            String text = (String)xpathObj.evaluate("//div[contains(@id,'block-system-main')]/div/div/div[2]/article/div[1]/div[1]/p/text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList)xpathObj.evaluate("//div[contains(@id,'block-system-main')]/div/div/div[2]/article/div[1]/div[3]/p", doc, XPathConstants.NODESET);
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text,"mk","en");

            shortText = StringEscapeUtils.unescapeHtml4(shortText).trim();
            String translatedShortText = YandexTranslator.translate(shortText,"mk","en");

            date = StringEscapeUtils.unescapeHtml4(date).trim();

            String source = "Vecher";
            System.out.println(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            writer.write(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
        } catch(SocketTimeoutException e){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            extractDataFromPage(url, writer);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
