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

/**
 * Created by Simona on 4/10/2016.
 */
public class KurirCrawler implements Crawler {

    private static String baseURL = "http://kurir.mk";

    public static void main(String[] args){
        KurirCrawler crawler = new KurirCrawler();
        crawler.crawl();
    }

    public void crawl() {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/kurir.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");

            int count = 1;
            while(true) {
                try {
                    String url = baseURL + "/" + HelperClass.getToday().replaceAll("-", "/") + "/page/" + count + "/";

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
                    NodeList tableRows = (NodeList) xpathObj.evaluate("//main[contains(@id,'main')]/ul/li", doc, XPathConstants.NODESET);
                    if (tableRows.getLength() == 0) {
                        break;
                    }
                    for (int i = 0; i < tableRows.getLength(); i++) {
                        Node node = tableRows.item(i);
                        String newsURL = (String) xpathObj.evaluate("./article/a/@href", node, XPathConstants.STRING);
                        System.out.println("\t\t" + newsURL);
                        extractDataFromPage(newsURL, writer);
                    }
                }catch(SocketTimeoutException e){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    count--;
                }catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    break;
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally{
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void extractDataFromPage(String url, OutputStreamWriter writer){
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
            String title = (String) xpathObj.evaluate("//main[contains(@id,'main')]/article/header/h1//text()", doc, XPathConstants.STRING);
            String shortText = (String)xpathObj.evaluate("//main[contains(@id,'main')]/article/header/h2/p//text()", doc, XPathConstants.STRING);
            String date = (String) xpathObj.evaluate("//main[contains(@id,'main')]/article/header/div[contains(@class,'entry-meta')]/time/@datetime", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList) xpathObj.evaluate("//main[contains(@id,'main')]/article/div[contains(@class,'entry-content')]/p", doc, XPathConstants.NODESET);
            String text = "";
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title,"mk","en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text,"mk","en");

            shortText = StringEscapeUtils.unescapeHtml4(shortText).trim();
            String translatedShortText = YandexTranslator.translate(shortText,"mk","en");

            date = StringEscapeUtils.unescapeHtml4(date).trim();

            String source = "Kurir";
            System.out.println(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            writer.write(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");


        } catch(SocketTimeoutException ex){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            extractDataFromPage(url, writer);
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
}
