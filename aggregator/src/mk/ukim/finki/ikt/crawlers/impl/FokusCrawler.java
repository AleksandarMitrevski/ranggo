package mk.ukim.finki.ikt.crawlers.impl;

import mk.ukim.finki.ikt.crawlers.Crawler;
import mk.ukim.finki.ikt.yandex.YandexTranslator;
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

/**
 * Created by Simona on 4/10/2016.
 */
public class FokusCrawler implements Crawler{

    private static String baseURL = "http://fokus.mk/kategorija/aktuelno-2/page/";

    public static void main(String[] args){
        FokusCrawler crawler = new FokusCrawler();
        crawler.crawl();
    }

    @Override
    public void crawl() {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/fokus.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");
            int count = 1;
            boolean flag = true;
            while(flag){
                try {
                    String url = baseURL + count;
                    System.out.println("Base url: " + url);
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
                    NodeList tableRowsLeft = (NodeList) xpathObj.evaluate("//div[contains(@id,'content')]/div[1]/div", doc, XPathConstants.NODESET);
                    NodeList tableRowsRight = (NodeList) xpathObj.evaluate("//div[contains(@id,'content')]/div[2]/div", doc, XPathConstants.NODESET);
                    for(int i=0;i<tableRowsLeft.getLength();i++){
                        Node nodeLeft = tableRowsLeft.item(i);
                        Node nodeRight = tableRowsRight.item(i);

                        String timeLeft = (String)xpathObj.evaluate("./div/div/div[contains(@class,'objaveno-pred')]//text()", nodeLeft, XPathConstants.STRING);
                        String urlLeft = (String)xpathObj.evaluate("./div/h2/a/@href", nodeLeft, XPathConstants.STRING);
                        System.out.println("\t\t" + urlLeft);
                        System.out.println("\t\tPublished: " + timeLeft);
                        if(timeLeft.contains("ден")){
                            flag = false;
                            break;
                        }
                        extractDataFromPage(urlLeft, writer);

                        String timeRight = (String)xpathObj.evaluate("./div/div/div[contains(@class,'objaveno-pred')]//text()", nodeLeft, XPathConstants.STRING);
                        String urlRight = (String)xpathObj.evaluate("./div/h2/a/@href", nodeRight, XPathConstants.STRING);
                        System.out.println("\t\t" + urlRight);
                        System.out.println("\t\tPublished: " + timeRight);
                        if(timeRight.contains("ден")){
                            flag = false;
                            break;
                        }
                        extractDataFromPage(urlRight, writer);

                    }
                }catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    count--;
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
                }
                count++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractDataFromPage(String url, OutputStreamWriter writer) {
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
            String title = (String) xpathObj.evaluate("//div[contains(@id,'content')]/div/div[1]/h2/text()", doc, XPathConstants.STRING);
            String shortText = (String) xpathObj.evaluate("//div[contains(@id,'content')]/div/div[1]/div[contains(@class,'excerpt')]/p/text()", doc, XPathConstants.STRING);
            String date = (String) xpathObj.evaluate("//div[contains(@id,'content')]/div/div[1]/div[contains(@class,'meta')]/div/text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList) xpathObj.evaluate("//div[contains(@id,'content')]/div/div[2]/p", doc, XPathConstants.NODESET);
            String text = "";
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

            String source = "Fokus";
            System.out.println(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            writer.write(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");

        } catch (SocketTimeoutException e) {
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
