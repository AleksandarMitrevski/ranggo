package mk.finki.ranggo.aggregator.crawlers.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
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
import mk.finki.ranggo.aggregator.yandex.YandexTranslator;

/**
 * Created by Simona on 4/11/2016.
 */
public class DnevnikCrawler implements Crawler {

    private static String baseURL = "http://www.dnevnik.mk/";
    
    List<AlchemyAPIAnalysisResult> results;
    
    public DnevnikCrawler(){
    	results = new ArrayList<AlchemyAPIAnalysisResult>();
    }
    
    public List<AlchemyAPIAnalysisResult> crawl() {
        try {
            String url = baseURL + "#panel-1";
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
            for(int i=0;i<tableRows.getLength();i++){
                Node node = tableRows.item(i);
                String newsURL = (String)xpathObj.evaluate("./a/@href",node, XPathConstants.STRING);
                newsURL = baseURL + newsURL.trim();
                if(!extractDataFromPage(newsURL)){
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
        
        return results;

    }

    private boolean extractDataFromPage(String url) {
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

         
            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text, "mk", "en");
            
            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            String source = "Дневник";
                        
            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("US"));

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
                       
           
            AlchemyAPIAnalysisResult result = AlchemyAPIWrapper.sentimentAnalysisFromTextDocument(translatedText, text, source, url, title, outputText);
            results.add(result);
            
            return true;
        }catch(SocketTimeoutException e){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return extractDataFromPage(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (SAXException e) {
			// TODO Auto-generated catch block
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
            return (year + "-" + month + "-" + day).equals(today);
        }catch(Exception e){

        }
        return false;
    }
}
