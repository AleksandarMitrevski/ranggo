package mk.ukim.finki.ikt.alchemyapi;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import mk.ukim.finki.ikt.entities.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Text;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simona on 3/31/2016.
 */
public class AlchemyAPIWrapper {

    public static String apiKey = "93d373e6a3f668c2b385057b29e3072e77ad2046";

    public static void main(String[] args) throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        sentimentAnalysisFromURL("http://edition.cnn.com/2016/03/31/politics/trump-view-from-south-korea-japan/index.html", "CNN");
    }

    public static void sentimentAnalysisFromURL(String url, String source) throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        String html = getHTML(url);

        AlchemyAPI alchemyAPI = AlchemyAPI.GetInstanceFromString(apiKey);

        String text = getText(alchemyAPI, html);
        String author = getAuthor(alchemyAPI, html);

        sentimentAnalysis(text, "", "", author, source, alchemyAPI);
    }

    public static AlchemyAPIObject sentimentAnalysisFromText(String text, String title, String shortText, String author, String source){
        AlchemyAPI alchemyAPI = AlchemyAPI.GetInstanceFromString(apiKey);
        return sentimentAnalysis(text, title, shortText, author, source, alchemyAPI);
    }

    public static AlchemyAPIObject sentimentAnalysis(String text, String title, String shortText, String author, String source, AlchemyAPI alchemyAPI){
        Category category = getCategory(alchemyAPI, text);
        List<Concept> concepts = getConcepts(alchemyAPI, text);
        List<Entity> entities = getEntities(alchemyAPI, text);
        List<Keyword> keywords = getKeywords(alchemyAPI, text);
        TextSentiment textSentiment = getTextSentiment(alchemyAPI, text);
        List<ElementTaxonomy> taxonomy = getTaxonomy(alchemyAPI, text);

//        System.out.println("Text: " + text);
//        System.out.println("Author: " + author);
//        System.out.println("Short text: " + shortText);
//        System.out.println("Source: " + source);
//        System.out.println("Category: " + category);
//        System.out.println("Concepts: " + concepts);
//        System.out.println("Entities: " + entities);
//        System.out.println("Keywor
// ds: " + keywords);
//        System.out.println("Text sentiment: " + textSentiment);
//        System.out.println("Taxonomy: " + taxonomy);

        AlchemyAPIObject object = new AlchemyAPIObject(author, text, category, concepts, entities, keywords, textSentiment, taxonomy);
        return object;
    }

    private static String getText(AlchemyAPI alchemyAPI, String html){
        Document doc = null;
        try {
            doc = alchemyAPI.HTMLGetText(html, "http://www.test.com");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            String text = (String)xpathObj.evaluate("//text/text()", doc, XPathConstants.STRING);
            return text;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static List<ElementTaxonomy> getTaxonomy(AlchemyAPI alchemyAPI, String text){
        List<ElementTaxonomy> taxonomyList = new ArrayList<>();
        Document doc = null;
        try {
            doc = alchemyAPI.TextGetTaxonomy(text);
        } catch (Exception e) {
            e.printStackTrace();
            return taxonomyList;
        }

        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            NodeList elements = (NodeList)xpathObj.evaluate("//taxonomy/element", doc, XPathConstants.NODESET);
            for(int i=0;i<elements.getLength();i++){
                Node node = elements.item(i);
                String confident = (String)xpathObj.evaluate("./confident/text()", node, XPathConstants.STRING);
                String label = (String)xpathObj.evaluate("./label/text()", node, XPathConstants.STRING);
                String scoreS = (String)xpathObj.evaluate("./score/text()", node, XPathConstants.STRING);
                double score;
                if(scoreS.equals("")){
                    scoreS = "0";
                }
                score = Double.parseDouble(scoreS);

                ElementTaxonomy taxonomy = new ElementTaxonomy(confident, label, score);
                taxonomyList.add(taxonomy);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return taxonomyList;
    }

    private static TextSentiment getTextSentiment(AlchemyAPI alchemyAPI, String text){
        TextSentiment textSentiment = null;
        Document doc = null;
        try {
            doc = alchemyAPI.TextGetTextSentiment(text);
        } catch (Exception e) {
            e.printStackTrace();
            return textSentiment;
        }
        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            String mixedS = (String)xpathObj.evaluate("//docSentiment/mixed", doc, XPathConstants.STRING);
            String scoreS = (String)xpathObj.evaluate("//docSentiment/score", doc, XPathConstants.STRING);
            String type = (String)xpathObj.evaluate("//docSentiment/type", doc, XPathConstants.STRING);

            if(mixedS.equals("")){
                mixedS = "0";
            }
            if(scoreS.equals("")){
                scoreS = "0";
            }

            int mixed;
            double score;
            mixed = Integer.parseInt(mixedS);
            score = Double.parseDouble(scoreS);

            textSentiment = new TextSentiment(mixed, score, type);

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return textSentiment;
    }

    private static List<Keyword> getKeywords(AlchemyAPI alchemyAPI, String text){
        List<Keyword> keywordList = new ArrayList<>();
        AlchemyAPI_KeywordParams keywordParams = new AlchemyAPI_KeywordParams();
        keywordParams.setSentiment(true);
        Document doc = null;
        try {
            doc = alchemyAPI.TextGetRankedKeywords(text, keywordParams);
        } catch (Exception e) {
            e.printStackTrace();
            return keywordList;
        }

        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            NodeList concepts = (NodeList)xpathObj.evaluate("//keywords/keyword", doc, XPathConstants.NODESET);

            for(int i=0;i<concepts.getLength();i++){
                Node node = concepts.item(i);
                XPathFactory xFactory = XPathFactory.newInstance();
                XPath xPathObj = xFactory.newXPath();
                double relevance = Double.parseDouble((String)xPathObj.evaluate("./relevance/text()", node, XPathConstants.STRING));
                String tmp = (String)xPathObj.evaluate("./sentiment/score/text()", node, XPathConstants.STRING);
                if(tmp.equals("")){
                    tmp = "0";
                }
                double sentimentScore = Double.parseDouble(tmp);
                String sentimentType = (String)xPathObj.evaluate("./sentiment/type/text()", node, XPathConstants.STRING);
                String text1 = (String)xPathObj.evaluate("./text/text()", node, XPathConstants.STRING);

                Keyword keyword = new Keyword(relevance, sentimentScore, sentimentType, text1);
                keywordList.add(keyword);
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return keywordList;
    }

    private static List<Entity> getEntities(AlchemyAPI alchemyAPI, String text){
        List<Entity> entitiesList = new ArrayList<>();
        Document doc = null;
        AlchemyAPI_NamedEntityParams entityParams = new AlchemyAPI_NamedEntityParams();
        entityParams.setSentiment(true);
        try {
            doc = alchemyAPI.TextGetRankedNamedEntities(text, entityParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            NodeList concepts = (NodeList)xpathObj.evaluate("//entities/entity", doc, XPathConstants.NODESET);

            for(int i=0;i<concepts.getLength();i++){
                Node node = concepts.item(i);
                XPathFactory xFactory = XPathFactory.newInstance();
                XPath xPathObj = xFactory.newXPath();
                String type = (String)xPathObj.evaluate("./type/text()", node, XPathConstants.STRING);
                double relevance = Double.parseDouble((String)xPathObj.evaluate("./relevance/text()", node, XPathConstants.STRING));
                String sentiment = (String)xPathObj.evaluate("./sentiment/score/text()", node, XPathConstants.STRING);
                if (sentiment.equals("")) {
                    sentiment = "0";
                }
                int count = Integer.parseInt((String) xPathObj.evaluate("./count/text()", node, XPathConstants.STRING));
                String text1 = (String)xPathObj.evaluate("./text/text()", node, XPathConstants.STRING);
                String disName = (String)xPathObj.evaluate("./disambiguated/name/text()", node, XPathConstants.STRING);
                String disGeo = (String)xPathObj.evaluate("./disambiguated/geo/text()", node, XPathConstants.STRING);
                String disWebsite = (String)xPathObj.evaluate("./disambiguated/website/text()", node, XPathConstants.STRING);
                String disDbpedia = (String)xPathObj.evaluate("./disambiguated/dbpedia/text()", node, XPathConstants.STRING);
                String disFreebase = (String)xPathObj.evaluate("./disambiguated/freebase/text()", node, XPathConstants.STRING);
                String disYago = (String)xPathObj.evaluate("./disambiguated/freebase/text()", node, XPathConstants.STRING);
                String disGeonames = (String)xPathObj.evaluate("./disambiguated/freebase/text()", node, XPathConstants.STRING);
                NodeList subTypes = (NodeList)xPathObj.evaluate("./disambiguated/subtype", node, XPathConstants.NODESET);
                ArrayList<String> subs = new ArrayList<>();
                for(int j=0;j<subTypes.getLength();j++){
                    Node sub = subTypes.item(j);
                    subs.add((String)xPathObj.evaluate("./text()", sub, XPathConstants.STRING));
                }

                Disambiguated disambiguated = new Disambiguated(disName, disGeo, disWebsite, disDbpedia, disFreebase, disYago, disGeonames, subs);
                Entity entity = new Entity(type, relevance, sentiment, count, text1, disambiguated);

                entitiesList.add(entity);
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return entitiesList;
    }

    private static List<Concept> getConcepts(AlchemyAPI alchemyAPI, String text){
        List<Concept> conceptsList = new ArrayList<>();
        Document doc = null;
        try {
            doc = alchemyAPI.TextGetRankedConcepts(text);
        } catch (Exception e) {
            e.printStackTrace();
            return conceptsList;
        }

        try {
           XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            NodeList concepts = (NodeList)xpathObj.evaluate("//concepts/concept", doc, XPathConstants.NODESET);

            for(int i=0;i<concepts.getLength();i++){
                Node node = concepts.item(i);
                String text1 = (String)xpathObj.evaluate("./text/text()", node, XPathConstants.STRING);
                double relevance = Double.parseDouble((String)xpathObj.evaluate("./relevance/text()", node, XPathConstants.STRING));
                String geo = (String)xpathObj.evaluate("./geo/text()", node, XPathConstants.STRING);
                String website = (String)xpathObj.evaluate("./website/text()",node, XPathConstants.STRING);
                String dbpedia = (String)xpathObj.evaluate("./dbpedia/text()", node, XPathConstants.STRING);
                String ciaFacebook = (String)xpathObj.evaluate("./ciaFacebook/text()", node, XPathConstants.STRING);
                String freebase = (String)xpathObj.evaluate("./freebase/text()", node, XPathConstants.STRING);
                String opencyc = (String)xpathObj.evaluate("./opencyc/text()", node, XPathConstants.STRING);
                String yago = (String)xpathObj.evaluate("./yago/text()", node, XPathConstants.STRING);
                String geoNames = (String)xpathObj.evaluate("./geoNames/text()", node, XPathConstants.STRING);

                Concept concept = new Concept(text1, relevance, geo, website, dbpedia, ciaFacebook, freebase, opencyc, yago, geoNames);


                conceptsList.add(concept);
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return conceptsList;
    }


    private static Category getCategory(AlchemyAPI alchemyAPI, String text){
        Document doc = null;
        try {
            doc = alchemyAPI.TextGetCategory(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            String category = (String)xpathObj.evaluate("//category/text()", doc, XPathConstants.STRING);
            double score = Double.parseDouble((String)xpathObj.evaluate("//score/text()", doc, XPathConstants.STRING));

            Category cat = new Category(category, score);

            return cat;

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getAuthor(AlchemyAPI alchemyAPI, String html){
        Document doc = null;
        try {
            doc = alchemyAPI.HTMLGetAuthor(html, "http://www.test.com/");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            String author = (String)xpathObj.evaluate("//author/text()", doc, XPathConstants.STRING);
            return author;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String getHTML(String url){
        URLConnection conn = null;
        try {
            conn = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't open connection");
            return "";
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't open stream");
            return "";
        }
        String inputLine;
        StringBuilder sb = new StringBuilder();
        try {
            while((inputLine = in.readLine())!=null){
                sb.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading");
            return "";
        }

        return sb.toString();
    }
}
