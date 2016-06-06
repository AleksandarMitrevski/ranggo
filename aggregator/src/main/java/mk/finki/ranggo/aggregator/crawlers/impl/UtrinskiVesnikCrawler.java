package mk.finki.ranggo.aggregator.crawlers.impl;

import java.io.BufferedReader;
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
 * Created by Simona on 4/10/2016.
 */
public class UtrinskiVesnikCrawler implements Crawler {

    private static String baseURL = "http://www.utrinski.mk/";
    private List<String> categories;

    List<AlchemyAPIAnalysisResult> results;
    
    public UtrinskiVesnikCrawler(){
        categories = new ArrayList<String>();
        categories.add("?ItemID=20467E30720CB241A155CA584D233EF8");
        categories.add("?ItemID=1E3705B1DBE3194B8E69407760B6865A");
        categories.add("?ItemID=102695A78201AA4CBCB142DC8B876CEB");
        categories.add("?ItemID=A45A8F343EE1774EAA0582225746A73F");
        
        results = new ArrayList<AlchemyAPIAnalysisResult>();
    }

    public  List<AlchemyAPIAnalysisResult> crawl() {
        try {
           
            for(int i=0;i<categories.size();i++){
                try {
                    String url = baseURL + categories.get(i);
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
                        if(!extractDataFromPage(newsURL)){
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

        } catch (Exception e) {
            e.printStackTrace();
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
            String date = (String) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td/div[contains(@class,'WB_UTRINSKI_DatumFullArticle')]//text()", doc, XPathConstants.STRING);
            String title = (String) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td//span[contains(@id,'ArticleTitle')]//text()", doc, XPathConstants.STRING);
            String shortText = (String) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td//span[contains(@id,'ArticleSummary')]//text()", doc, XPathConstants.STRING);
            NodeList textNodes = (NodeList) xpathObj.evaluate("//div[contains(@class,'article-details')]/table[1]/tbody/tr/td/*[text()]|//div[contains(@class,'article-details')]/table[1]/tbody/tr/td/p/*[text()]", doc, XPathConstants.NODESET);
            String text = "";
            for(int i=0;i<textNodes.getLength();i++){
                text += textNodes.item(i).getTextContent();
            }

            title = StringEscapeUtils.unescapeHtml4(title).trim();
            String translatedTitle = YandexTranslator.translate(title, "mk", "en");
            
            text = StringEscapeUtils.unescapeHtml4(text).trim();
            String translatedText = YandexTranslator.translate(text, "mk", "en");
            
            String today = HelperClass.getToday();
            String source = "Утрински весник";
            
            DateFormat outputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
            DateFormat inputFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");

            String inputText = new Date().toString();
            Date dateObj = inputFormat.parse(inputText);
            String outputText = outputFormat.format(dateObj);
            
            //get alchemyapi analysis result
            AlchemyAPIAnalysisResult result = AlchemyAPIWrapper.sentimentAnalysisFromTextDocument(translatedText, text, source, url, title, outputText);
            results.add(result);

        } catch(SocketTimeoutException e){
            e.printStackTrace();
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
        return true;
    }
}
