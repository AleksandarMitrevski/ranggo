package mk.finki.ranggo.aggregator.alchemyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_CombinedParams;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;
import com.alchemyapi.api.AlchemyAPI_Params;

import mk.finki.ranggo.aggregator.ContentsAggregatorImpl.AlchemyAPIAnalysisResult;
import mk.finki.ranggo.aggregator.model.Concept;
import mk.finki.ranggo.aggregator.model.Keyword;
import mk.finki.ranggo.aggregator.model.Person;
import mk.finki.ranggo.aggregator.model.PersonEntity;
import mk.finki.ranggo.aggregator.model.Taxonomy;

/**
 * Created by Simona on 3/31/2016.
 */
public class AlchemyAPIWrapper {

    public static String apiKey = "93d373e6a3f668c2b385057b29e3072e77ad2046";

    public static AlchemyAPIAnalysisResult sentimentAnalysisFromTextDocument(String text, String source, String url, String title, String timestamp) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException{
        AlchemyAPI alchemyAPI = AlchemyAPI.GetInstanceFromString(apiKey);
        AlchemyAPI_CombinedParams alchemyapi_params = new AlchemyAPI_CombinedParams();
		alchemyapi_params.setLinkedData(true);
		alchemyapi_params.setSentiment(true);
		alchemyapi_params.setShowSourceText(true);
		alchemyapi_params.setExtract("entity");
		alchemyapi_params.setExtract("concept");
		alchemyapi_params.setExtract("keyword");
		alchemyapi_params.setExtract("taxonomy");
        
        Document document = alchemyAPI.TextGetCombined(text, alchemyapi_params);
		
		//extracting entities
		Element entitiesElement = (Element)document.getElementsByTagName("entities").item(0);
		NodeList entities = entitiesElement.getElementsByTagName("entity");
		
		List<Person> persons = new ArrayList<Person>();
		List<PersonEntity> personEntities = new ArrayList<PersonEntity>();
		for(int i = 0; i < entities.getLength(); i++){
			Element entity = (Element)entities.item(i);
			
			if(entity.getElementsByTagName("type").item(0).getTextContent().equals("Person")){
				String name = entity.getElementsByTagName("text").item(0).getTextContent();
				
				System.out.println("Person name: " + name);
				
				String relevanceString = entity.getElementsByTagName("relevance").item(0).getTextContent();
				Double relevance = Double.parseDouble(relevanceString);
				
				Element sentiment = (Element)entity.getElementsByTagName("sentiment").item(0);
				Double score;
				Boolean mixed;
				
				String sentimentType = sentiment.getElementsByTagName("type").item(0).getTextContent();
				if(sentimentType.equals("neutral")){
					score = 0.0;
				}else{
					String scoreText = sentiment.getElementsByTagName("score").item(0).getTextContent();
					score = Double.parseDouble(scoreText);
				}
				
				NodeList mixedElements = sentiment.getElementsByTagName("mixed");
				if(mixedElements.getLength() > 0){
					String mixedText = mixedElements.item(0).getTextContent();
					mixed = mixedText.equals("1");
				}else{
					mixed = false;
				}
				
				NodeList disambiguatedElements = entity.getElementsByTagName("disambiguated");
				List<String> categories = new ArrayList<String>();
				String dbpediaUrl = null;
				if(disambiguatedElements.getLength() > 0){
					Element disambiguated = (Element)disambiguatedElements.item(0);
					
					NodeList subtypes = disambiguated.getElementsByTagName("subType");
					for(int z = 0; z < subtypes.getLength(); z++){
						categories.add(subtypes.item(z).getTextContent());
					}
					
					dbpediaUrl = disambiguated.getElementsByTagName("dbpedia").item(0).getTextContent();
				}
				
				Person person = new Person();
				
				person.setName(name);
				person.setDbpediaUrl(dbpediaUrl);
				person.setCategories(categories);
				
				//defer fetching from dbpedia until before adding to data store (a prior confirmation that the person isn't already stored reduces total traffic) 
				person.setShortBio(null);
				person.setPictureUrl(null);
				
				persons.add(person);
				
				PersonEntity personEntity = new PersonEntity();
				
				//set this after the person has been added to the data store
				personEntity.setPerson(null);
				
				personEntity.setRelevance(relevance);
				personEntity.setScore(score);
				System.out.println("Score: " + personEntity.getScore());
				if(mixed){
					personEntity.setMixed(mixed);
				}
				
				personEntities.add(personEntity);
			}
		}

		//extracting concepts
		
		LinkedList<Concept> concepts = new LinkedList<Concept>();
		
		Element conceptsElement = (Element)document.getElementsByTagName("concepts").item(0);
		NodeList conceptsList = conceptsElement.getElementsByTagName("concept");
		for(int i = 0; i < conceptsList.getLength(); i++){
			Element conceptElement = (Element)conceptsList.item(i);
			
			String name = conceptElement.getElementsByTagName("text").item(0).getTextContent();
			
			String relevanceString = conceptElement.getElementsByTagName("relevance").item(0).getTextContent();
			Double relevance = Double.parseDouble(relevanceString);
			
			String dbpediaUrl = conceptElement.getElementsByTagName("dbpedia").item(0).getTextContent();
			
			Concept concept = new Concept();
			
			concept.setText(name);
			concept.setRelevance(relevance);
			concept.setDbpediaUrl(dbpediaUrl);
			
			concepts.add(concept);
		}
		
		//extracting keywords
		
		LinkedList<Keyword> keywords = new LinkedList<Keyword>();
		
		Element keywordsElement = (Element)document.getElementsByTagName("keywords").item(0);
		NodeList keywordsList = keywordsElement.getElementsByTagName("keyword");
		for(int i = 0; i < keywordsList.getLength(); i++){
			Element keywordElement = (Element)keywordsList.item(i);
			
			String name = keywordElement.getElementsByTagName("text").item(0).getTextContent();
			
			String relevanceString = keywordElement.getElementsByTagName("relevance").item(0).getTextContent();
			Double relevance = Double.parseDouble(relevanceString);
			
			Element sentiment = (Element)keywordElement.getElementsByTagName("sentiment").item(0);
			Double score = 0.0;
			Boolean mixed;
			
			if(sentiment.getElementsByTagName("type") != null){
				String sentimentType = sentiment.getElementsByTagName("type").item(0).getTextContent();
					if(sentimentType.equals("neutral")){
						score = 0.0;
					}else{
						String scoreText = sentiment.getElementsByTagName("score").item(0).getTextContent();
						score = Double.parseDouble(scoreText);
					}
			} else {
				score = 0.0;
			}
			
			
			NodeList mixedElements = sentiment.getElementsByTagName("mixed");
			if(mixedElements.getLength() > 0){
				String mixedText = mixedElements.item(0).getTextContent();
				mixed = mixedText.equals("1");
			}else{
				mixed = false;
			}
			
			Keyword keyword = new Keyword();
			
			keyword.setText(name);
			keyword.setRelevance(relevance);
			keyword.setScore(score);
			if(mixed){
				keyword.setMixed(mixed);
			}
			
			keywords.add(keyword);
		}
		
		//extracting taxonomies
		
		LinkedList<Taxonomy> taxonomies = new LinkedList<Taxonomy>();
		
		Element taxonomiesElement = (Element)document.getElementsByTagName("taxonomy").item(0);
		NodeList taxonomiesList = taxonomiesElement.getElementsByTagName("element");
		for(int i = 0; i < taxonomiesList.getLength(); i++){
			Element taxonomyElement = (Element)taxonomiesList.item(i);
			
			String labelString = taxonomyElement.getElementsByTagName("label").item(0).getTextContent();
			if(labelString.startsWith("/")){
				labelString = labelString.substring(1);
			}
			List<String> labels = Arrays.asList(labelString.split("/"));
			
			String scoreString = taxonomyElement.getElementsByTagName("score").item(0).getTextContent();
			Double score = Double.parseDouble(scoreString);
			
			Taxonomy taxonomy = new Taxonomy();
			
			taxonomy.setLabels(labels);
			taxonomy.setScore(score);
			
			taxonomies.add(taxonomy);
		}
	
		AlchemyAPIAnalysisResult result = new AlchemyAPIAnalysisResult(null, null, text, null, null, persons, personEntities, concepts, keywords, taxonomies);
		result.setType(source);
		result.setUrl(url);
		result.setTitle(title);
		result.setTimestamp(timestamp);
		
		return result;
    }

}
