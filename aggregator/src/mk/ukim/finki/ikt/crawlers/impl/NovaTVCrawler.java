package mk.ukim.finki.ikt.crawlers.impl;

import mk.ukim.finki.ikt.crawlers.Crawler;
import mk.ukim.finki.ikt.helper.HelperClass;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simona on 4/11/2016.
 */
public class NovaTVCrawler implements Crawler {

    private static String baseURL = "http://novatv.mk";
    private List<String> categories;
    private Map<String, String> months;

    public NovaTVCrawler(){
        categories = new ArrayList<>();
        categories.add("/category/makedonija/");
        categories.add("/category/svet/");

        months = new HashMap<>();
        months.put("јануари","01");
        months.put("февруари","02");
        months.put("март","03");
        months.put("април","04");
        months.put("мај","05");
        months.put("јуни","06");
        months.put("јули","07");
        months.put("август","08");
        months.put("септември","09");
        months.put("октомври", "10");
        months.put("ноември","11");
        months.put("декември","12");

    }

    public static void main(String[] args){
        NovaTVCrawler crawler = new NovaTVCrawler();
        crawler.crawl();
    }

    @Override
    public void crawl() {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/novatv.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|originalTitle|translatedTitle|originalText|translatedText|originalShortText|translatedShortText|source|datePublished\n");

            for(int i=0;i<categories.size();i++){
                String category = categories.get(i);
                int count = 1;
                boolean flag = true;
                while(flag){
                    try {
                        String url = baseURL + category + "page/" + count + "/";
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
                        String xpathQuery = "";
                        if(count == 1){
                            String firstURL = (String) xpathObj.evaluate("//div[contains(@id,'main-content')]/article/h3/a/@href", doc, XPathConstants.STRING);
                            System.out.println("\t\tNews url: " + firstURL);
                            if(!extractDataFromPage(firstURL, writer)){
                                flag = false;
                                break;
                            }
                            xpathQuery = "//div[contains(@id,'main-content')]/div[contains(@class,'archive-grid mh-section mh-group')]/article|//div[contains(@id,'main-content')]/div[contains(@class,'archive-list mh-section mh-group')]/article";

                        } else {
                            xpathQuery = "//div[contains(@id,'main-content')]/article";
                        }

                        NodeList tableRows = (NodeList)xpathObj.evaluate(xpathQuery, doc, XPathConstants.NODESET);
                        for(int j=0;j<tableRows.getLength();j++){
                            Node node = tableRows.item(j);
                            String newsURL = (String)xpathObj.evaluate("./div[1]/a/@href",node, XPathConstants.STRING);
                            System.out.println("\t\tNews url: " + newsURL);
                            if(!extractDataFromPage(newsURL, writer)){
                                flag = false;
                                break;
                            }
                        }


                    } catch(SocketTimeoutException e){
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
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean extractDataFromPage(String url, OutputStreamWriter writer){
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
            String title = (String) xpathObj.evaluate("//div[contains(@id,'main-content')]/article/header/h1/text()", doc, XPathConstants.STRING);
            String date = (String) xpathObj.evaluate("//div[contains(@id,'main-content')]/article/p/span[contains(@class,'entry-meta-date updated')]//text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList)xpathObj.evaluate("//div[contains(@id,'main-content')]/article/div[contains(@class,'entry-content clearfix')]/p", doc, XPathConstants.NODESET);
            String text = "";
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            if(!checkDate(date)){
                return false;
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text, "mk", "en");

            date = StringEscapeUtils.unescapeHtml4(date).trim();
            String shortText = "";
            String translatedShortText = "";

            String source = "Nova TV";
            System.out.println(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");
            writer.write(url + "|" + title + "|" + translatedTitle + "|" + text + "|" + translatedText + "|" + shortText + "|" + translatedShortText + "|" + source + "|" + date + "\n");

            return true;
        } catch(SocketTimeoutException e ){
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return extractDataFromPage(url, writer);
        }catch (MalformedURLException e) {
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

    private boolean checkDate(String dateTime){
        try {
            String date = dateTime.split("-")[1].trim();
            String[] parts = date.split(" ");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            return (year + "-" + months.get(month.toLowerCase()) + "-" + day).equals(HelperClass.getToday());
        } catch (Exception e){

        }
        return false;
    }
}
