package mk.finki.ranggo.aggregator.crawlers.impl;

import mk.finki.ranggo.aggregator.ContentsAggregatorImpl.AlchemyAPIAnalysisResult;
import mk.finki.ranggo.aggregator.alchemyapi.AlchemyAPIWrapper;
import mk.finki.ranggo.aggregator.crawlers.Crawler;
import mk.finki.ranggo.aggregator.helper.HelperClass;
import mk.finki.ranggo.aggregator.repository.ContentRepository;
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
public class KurirCrawler implements Crawler {

    private static String baseURL = "http://kurir.mk";
    	
    List<AlchemyAPIAnalysisResult> results;
    private ContentRepository contentRepository;
    
    public KurirCrawler(ContentRepository contentRepository){
    	results = new ArrayList<AlchemyAPIAnalysisResult>();
    	this.contentRepository = contentRepository;
    }

    public List<AlchemyAPIAnalysisResult> crawl() {
        try {
            int count = 1;
            while(true) {
                try {
                    String url = baseURL + "/" + HelperClass.getToday().replaceAll("-", "/") + "/page/" + count + "/";
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
                        if(contentRepository.findBySourceUrl(newsURL) == null){
                        	 extractDataFromPage(newsURL);
                        }
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
            
        }
        return results;
    }

    public void extractDataFromPage(String url){
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

            
            date = StringEscapeUtils.unescapeHtml4(date).trim();

            String source = "Курир";

            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", new Locale("US"));

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
            
            //get alchemyapi analysis result
           
            AlchemyAPIAnalysisResult result = AlchemyAPIWrapper.sentimentAnalysisFromTextDocument(translatedText, text, source, url, title, outputText);
            results.add(result);

        } catch(SocketTimeoutException ex){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            extractDataFromPage(url);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
