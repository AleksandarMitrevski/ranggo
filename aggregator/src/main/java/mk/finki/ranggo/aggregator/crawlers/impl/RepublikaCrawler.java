package mk.finki.ranggo.aggregator.crawlers.impl;

import mk.finki.ranggo.aggregator.ContentsAggregatorImpl.AlchemyAPIAnalysisResult;
import mk.finki.ranggo.aggregator.alchemyapi.AlchemyAPIWrapper;
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
import org.xml.sax.SAXException;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Simona on 4/11/2016.
 */
public class RepublikaCrawler implements Crawler {

    private static String baseURL = "http://republika.mk/";

    private List<AlchemyAPIAnalysisResult> results;

    public RepublikaCrawler() {
    	results = new ArrayList<AlchemyAPIAnalysisResult>();
    }
    
    public  List<AlchemyAPIAnalysisResult> crawl() {
        try {
            int count = 1;
            boolean flag = true;
            while(flag){
                try {
                    String url = baseURL + "?cat=1963&paged=" + count;
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
                    NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@id,'content')]/article", doc, XPathConstants.NODESET);
                    for(int i=0;i<tableRows.getLength();i++){
                        String newsURL = (String)xpathObj.evaluate("./a/@href",tableRows.item(i), XPathConstants.STRING);
                        if(!extractDataFromPage(newsURL)){
                            flag = false;
                            break;
                        }
                    }

                } catch(SocketTimeoutException e){
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
        } finally{
            
        }
        return results;
    }

    private boolean extractDataFromPage(String url){
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
            String title = (String) xpathObj.evaluate("//div[contains(@id,'content')]/div[2]/h1/text()", doc, XPathConstants.STRING);
            String date = (String)xpathObj.evaluate("//div[contains(@id,'content')]/div[2]/div[contains(@class,'entry-meta')]/span[contains(@class,'meta-date')]/text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList)xpathObj.evaluate("//div[contains(@id,'article_text')]/p",doc, XPathConstants.NODESET);
            String text = "";
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            date = StringEscapeUtils.unescapeHtml4(date);
            if(!checkDate(date)){
                return false;
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text, "mk", "en");

            String shortText = "";
            String translatedShortText = "";

            String source = "Република";
            
            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("US"));

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
            
            System.out.println("Text:" + text);
            System.out.println("Title: " + title);
            System.out.println("Date: " + date);
            System.out.println("Output text: " + outputText);
            
            //get alchemyapi analysis result
            AlchemyAPIAnalysisResult result = AlchemyAPIWrapper.sentimentAnalysisFromTextDocument(translatedText, text, source, url, title, outputText);
            results.add(result);

            return true;
        } catch(SocketTimeoutException e){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return extractDataFromPage(url);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }

    private boolean checkDate(String date){
        try {
            String[] parts = date.split("\\.");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            String today = HelperClass.getToday();
            System.out.println("From text: " + year + "-" + month + "-" + day);
            System.out.println("Original: " + year + "-" + month + "-" + day);
            return (year + "-" + month + "-" + day).equals(today);
        } catch(Exception e){

        }
        return false;
    }
}
