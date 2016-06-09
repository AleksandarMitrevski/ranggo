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
 * Created by Simona on 4/10/2016.
 */
public class VecherCrawler implements Crawler{

    private static String baseURL = "http://vecer.mk";
    private List<String> categories;
    List<AlchemyAPIAnalysisResult> results;
    
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
        
        results = new ArrayList<AlchemyAPIAnalysisResult>();
    }

    public  List<AlchemyAPIAnalysisResult> crawl() {
        try {
           
            for(int i=0;i<categories.size();i++){
                try {
                    String url = baseURL + "/" +  categories.get(i);
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
                        extractDataFromPage(newsURL);
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
            
        }
        return results;
    }

    private void extractDataFromPage(String url){
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

            date = StringEscapeUtils.unescapeHtml4(date).trim();

            String source = "Вечер";
            
            String today = HelperClass.getToday();
            
            //get alchemyapi analysis result
            
            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("US"));

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
            
          
            AlchemyAPIAnalysisResult result = AlchemyAPIWrapper.sentimentAnalysisFromTextDocument(translatedText,text, source, url, title, outputText);
            results.add(result);
            
        } catch(SocketTimeoutException e){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            extractDataFromPage(url);
        }catch (MalformedURLException e) {
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
    }
}
