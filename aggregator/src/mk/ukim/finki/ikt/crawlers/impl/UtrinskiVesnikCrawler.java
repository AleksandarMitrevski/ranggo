package mk.ukim.finki.ikt.crawlers.impl;

import mk.ukim.finki.ikt.crawlers.Crawler;
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
public class UtrinskiVesnikCrawler implements Crawler {

    private static String baseURL = "http://www.utrinski.mk/";
    private List<String> categories;

    public UtrinskiVesnikCrawler(){
        categories = new ArrayList<>();
        categories.add("?ItemID=20467E30720CB241A155CA584D233EF8");
        categories.add("?ItemID=1E3705B1DBE3194B8E69407760B6865A");
        categories.add("?ItemID=102695A78201AA4CBCB142DC8B876CEB");
        categories.add("?ItemID=A45A8F343EE1774EAA0582225746A73F");
    }

    public static void main(String[] args){
        UtrinskiVesnikCrawler crawler = new UtrinskiVesnikCrawler();
        crawler.crawl();
    }

    @Override
    public void crawl() {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/utrinski.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");

            for(int i=0;i<categories.size();i++){
                try {
                    String url = baseURL + categories.get(i);
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
                    NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@class,'category-details')]/div", doc, XPathConstants.NODESET);

                    for(int j=0;j<tableRows.getLength();j++){
                        Node node = tableRows.item(j);
                        String newsURL = (String)xpathObj.evaluate("./table/tbody/tr/td[2]/a/@href", node, XPathConstants.STRING);
                        newsURL = baseURL + newsURL.trim();
                        System.out.println("\t\t" + newsURL);
                        if(!extractDataFromPage(newsURL, writer)){
                            break;
                        }
                    }

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    i--;
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (XPathExpressionException e) {
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean extractDataFromPage(String url, OutputStreamWriter writer){
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
            String date = (String) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td/div[contains(@class,'WB_UTRINSKI_DatumFullArticle')]//text()", doc, XPathConstants.STRING);
            String title = (String) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td//span[contains(@id,'ArticleTitle')]//text()", doc, XPathConstants.STRING);
            String shortText = (String) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td//span[contains(@id,'ArticleSummary')]//text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td/*[text()]|//div[contains(@class,'article-details')]/table[1]/tbody/tr/td/p/*[text()]", doc, XPathConstants.NODESET);
            String text = "";
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            System.out.println("Title: " + title);
            System.out.println("Date: " + date);
            System.out.println("Short text: " + shortText);
            System.out.println("Text: " + text);

        } catch(SocketTimeoutException e){
            e.printStackTrace();
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
        return true;
    }
}
