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
public class VestCrawler implements Crawler {

    private static String baseURL = "http://www.vest.mk";
    private List<String> categories;
    List<AlchemyAPIAnalysisResult> results;
 
    public VestCrawler(){
        categories = new ArrayList<String>();
        categories.add("?ItemID=248611FB8F724A4D89E75953D9321EF0");
        categories.add("?ItemID=2C2B6DE119DADB4BA9CDF0C2B7918E11");
        categories.add("?ItemID=E90B1FCD71872F4CA984406C420D7778");
        categories.add("?ItemID=A7E98DB3DACF3240AB114E0CDC20384A");
        categories.add("?ItemID=86800C325C886149A708CBDE4D0CCA18");
        categories.add("?ItemID=FD8578D49B477643AE86230043ED205A");
        categories.add("?ItemID=5C4AEF021FFB6642B91F9FC3C0A7E4B9");
    	results = new ArrayList<AlchemyAPIAnalysisResult>();

    }

    public  List<AlchemyAPIAnalysisResult> crawl() {
        try {
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
                    NodeList tableRows = (NodeList) xpathObj.evaluate("//body/div/table/tbody/tr[6]/td/div/div/table/tbody/tr[2]/td[3]/table", doc, XPathConstants.NODESET);
                    for(int j=0;j<tableRows.getLength();j++){
                        Node node = tableRows.item(j);
                        String newsURL = (String)xpathObj.evaluate("./tbody/tr/td[2]/a/@href", node, XPathConstants.STRING);
                        if(!newsURL.equals("")) {
                            newsURL = baseURL + newsURL.trim();
                            System.out.println("\t\tNews url: " + newsURL);
                            if(!extractDataFromPage(newsURL)){
                                break;
                            }
                        }
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
        } finally {
            
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
            String date = (String) xpathObj.evaluate("//body/div[2]/table/tbody/tr[6]/td/div/div/table/tbody/tr[2]/td[3]/div[2]/table/tbody/tr/td/div[1]/table/tbody/tr/td/div/text()", doc, XPathConstants.STRING);
            String title = (String) xpathObj.evaluate("//body/div[2]/table/tbody/tr[6]/td/div/div/table/tbody/tr[2]/td[3]/div[2]/table/tbody/tr/td/div[1]/table/tbody/tr/td//*[contains(@id,'ArticleTitle')]/text()", doc, XPathConstants.STRING);
            String shortText = (String)xpathObj.evaluate("//body/div[2]/table/tbody/tr[6]/td/div/div/table/tbody/tr[2]/td[3]/div[2]/table/tbody/tr/td/div[1]/table/tbody/tr/td//*[contains(@id,'ArticleSummary')]/text()", doc, XPathConstants.STRING);
            Node node = (Node)xpathObj.evaluate("//body/div[2]/table/tbody/tr[6]/td/div/div/table/tbody/tr[2]/td[3]/div[2]/table/tbody/tr/td/div[1]/table/tbody/tr/td/p[last()]", doc, XPathConstants.NODE);
            String text = "";
            if(node != null){
                text = node.getTextContent();
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");

            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text,"mk","en");

            date = StringEscapeUtils.unescapeHtml4(date).trim();

            if(!checkDate(date)){
                return false;
            }
            if(text.length()<20){
                return false;
            }
            String source = "Вест";
            
            String today = HelperClass.getToday();
            
            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("US"));

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
            
           
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
        return false;
    }

    private boolean checkDate(String dateTime){
        try {
            String date = dateTime.split(",")[0];
            String[] parts = date.split("\\.");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            String today = HelperClass.getToday();
            System.out.println("From text: " + year + "-" + month + "-" + day);
            System.out.println("Original: " + year + "-" + month + "-" + day);
            return (year + "-" + month + "-" + day).equals(today);
        } catch(Exception e){
            return false;
        }
    }
}
