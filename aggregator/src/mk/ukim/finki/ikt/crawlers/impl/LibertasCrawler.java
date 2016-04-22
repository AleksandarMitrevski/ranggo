package mk.ukim.finki.ikt.crawlers.impl;

import mk.ukim.finki.ikt.alchemyapi.AlchemyAPIWrapper;
import mk.ukim.finki.ikt.crawlers.Crawler;
import mk.ukim.finki.ikt.entities.AlchemyAPIObject;
import mk.ukim.finki.ikt.yandex.YandexTranslator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Simona on 4/3/2016.
 */
public class LibertasCrawler implements Crawler {

    private String url = "http://www.libertas.mk/category/%D0%B0%D0%BA%D1%82%D1%83%D0%B5%D0%BB%D0%BD%D0%BE/";

    public static void main(String[] args){
        LibertasCrawler crawler = new LibertasCrawler();
        crawler.crawl();
    }

    @Override
    public void crawl(){
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("files/libertas.csv", true),
                    Charset.forName("utf-8").newEncoder()
            );
            writer.write("url|text|shortText|author|source\n");

            int i = 1;
            while(true){
                String tempURL = url;
                if(i > 1){
                    tempURL = url + "/page/" + i + "/";
                }
                boolean flag = crawlPages(tempURL, writer);
                System.out.println("Flag: " + flag);
                if(!flag){
                    break;
                }
                i++;
            }

        }catch(Exception ex){

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

    private boolean crawlPages(String url, OutputStreamWriter writer){
        boolean returnValue = true;
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
            NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@class,'panel-content main-article-list')]/div", doc, XPathConstants.NODESET);

            returnValue = true;
            for (int i = 0; i < tableRows.getLength(); i++) {
                System.out.println(i + "-------------------");
                Node tr = tableRows.item(i);

                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xPath = xPathFactory.newXPath();

                String image = (String) xPath.evaluate("./div[contains(@class,'item-header')]//img/@src", tr, XPathConstants.STRING);
                String link = (String) xPath.evaluate("./div[contains(@class,'item-header')]//a/@href", tr, XPathConstants.STRING);
                String title = (String) xPath.evaluate("./div[contains(@class,'item-content')]/h3/a/text()", tr, XPathConstants.STRING);
                String shortText = (String) xPath.evaluate("./div[contains(@class,'item-content')]/p//text()", tr, XPathConstants.STRING);
                boolean flag = secondLevel(link, image, title, shortText, writer);
                if (flag == false) {
                    returnValue = flag;
                    break;
                }
            }
        }catch(Exception ex){

        }
        return returnValue;
    }

    private boolean secondLevel(String url, String image, String title, String shortText, OutputStreamWriter writer){
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
            NodeList tableRows = (NodeList) xpathObj.evaluate("//div[contains(@class,'panel-content shortocde-content')]/p", doc, XPathConstants.NODESET);
            String text = "";
            for (int i = 0; i < tableRows.getLength(); i++) {
                Node node = tableRows.item(i);
                text += node.getTextContent();
            }
            String date = (String) xpathObj.evaluate("//div[contains(@class, 'article-header-info')]//span[contains(@class,'article-header-meta-date')]//text()", doc, XPathConstants.STRING);

            shortText = StringEscapeUtils.unescapeHtml4(shortText);
            text = StringEscapeUtils.unescapeHtml4(text);
            date = StringEscapeUtils.unescapeHtml4(date);

            String[] tmp = date.split("\\s");
            String fullDate = tmp[0];
            String fullTime = tmp[2];

            if(!checkDate(fullDate, fullTime)){
                return false;
            }

            sentimentAnalysis(url, image, title, shortText, text, fullDate, fullTime, writer);

        }catch(Exception ex){
        }

        return true;
    }

    private static boolean checkDate(String date, String time){
        String[] dateParts = date.split("\\.");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt("20" + dateParts[2]);

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int todayYear = cal.get(Calendar.YEAR);
        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);

        if(day == todayDay && month == todayMonth && year == todayYear){
            return true;
        }

        return false;
    }

    private static void sentimentAnalysis(String url, String image, String title, String shortText, String text, String date, String time, OutputStreamWriter writer) throws IOException {
        text = YandexTranslator.translate(text, "mk","en");
        System.out.println("Vleze");
        AlchemyAPIObject object = AlchemyAPIWrapper.sentimentAnalysisFromText(text, title, shortText, "", "Libertas");
        writer.write(object.toString() + "\n");
        System.out.println(object.toString());
    }

}
