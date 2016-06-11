package mk.finki.ranggo.aggregator.crawlers.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import mk.finki.ranggo.aggregator.ContentsAggregatorImpl.AlchemyAPIAnalysisResult;
import mk.finki.ranggo.aggregator.alchemyapi.AlchemyAPIWrapper;
import mk.finki.ranggo.aggregator.crawlers.Crawler;
import mk.finki.ranggo.aggregator.helper.HelperClass;
import mk.finki.ranggo.aggregator.repository.ContentRepository;
import mk.finki.ranggo.aggregator.yandex.YandexTranslator;

/**
 * Created by Simona on 4/10/2016.
 */
public class FokusCrawler implements Crawler{

    private static String baseURL = "http://fokus.mk/kategorija/aktuelno-2/page/";
    private List<AlchemyAPIAnalysisResult> results;
    private ContentRepository contentRepository;
    
    public FokusCrawler(ContentRepository contentRepository){
    	results = new ArrayList<AlchemyAPIAnalysisResult>();
    	this.contentRepository = contentRepository;
    }
    
    public List<AlchemyAPIAnalysisResult> crawl() {
            int count = 1;
            boolean flag = true;
            while(flag){
                try {
                    String url = baseURL + count;
                    System.out.println("url:" + url);
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
                      
                        if(timeLeft.contains("ден")){
                            flag = false;
                            break;
                        }
                        
                        if(contentRepository.findBySourceUrl(urlLeft) == null){
                            extractDataFromPage(urlLeft);
                        }

                        String timeRight = (String)xpathObj.evaluate("./div/div/div[contains(@class,'objaveno-pred')]//text()", nodeLeft, XPathConstants.STRING);
                        String urlRight = (String)xpathObj.evaluate("./div/h2/a/@href", nodeRight, XPathConstants.STRING);
                        
                        if(timeRight.contains("ден")){
                            flag = false;
                            break;
                        }
                        
                        if(contentRepository.findBySourceUrl(urlRight) == null){
                        	extractDataFromPage(urlRight);
                        }
                        
                      
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
                } catch(Exception e){
                	
                }
                count++;
            }
 
        return results;
    }

    private void extractDataFromPage(String url) {
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

            date = StringEscapeUtils.unescapeHtml4(date).trim();

            String source = "Фокус";
            
            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("US"));

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
            
            System.out.println("Text:" + text);
            System.out.println("Title: " + title);
            System.out.println("Date: " + date);
            System.out.println("Output text: " + outputText);
            
            AlchemyAPIAnalysisResult result = AlchemyAPIWrapper.sentimentAnalysisFromTextDocument(translatedText, text, source, url, title, outputText);
            results.add(result);
            
        } catch (SocketTimeoutException e) {
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
