package mk.finki.unip.ranggo.service;

import java.io.IOException;
//import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_CombinedParams;

//import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mk.finki.unip.ranggo.model.Content;
import mk.finki.unip.ranggo.model.Person;
import mk.finki.unip.ranggo.model.Rating;
import mk.finki.unip.ranggo.repository.ContentRepository;
import mk.finki.unip.ranggo.repository.PersonRepository;
import mk.finki.unip.ranggo.service.ContentsAggregatorException.AggregatorMethod;
//import mk.finki.unip.ranggo.utility.HttpHelper;

@Component
public class ContentsAggregator {
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private ContentRepository contentRepository;
	
	@Value("${alchemyapi.key}")
	private String alchemyapi_key;
	
	private AlchemyAPI alchemyapi;
	private AlchemyAPI_CombinedParams alchemyapi_params;
	
	@PostConstruct
	public void init() {
		alchemyapi = AlchemyAPI.GetInstanceFromString(alchemyapi_key);

		alchemyapi_params = new AlchemyAPI_CombinedParams();
		alchemyapi_params.setLinkedData(true);
		alchemyapi_params.setSentiment(true);
		alchemyapi_params.setShowSourceText(true);
		alchemyapi_params.setExtract("entity");
	}
	
	public void aggregateGoogleNewsRSSFeed(Date date) throws ContentsAggregatorException {
		//only the category 'top stories' is extracted (due to the daily limit of 1000 transactions of a free alchemyapi license) 
		//url: http://news.google.com/news?&hl=en&output=rss&scoring=n&as_drrb=b&as_minm=%minm&as_mind=%mind&as_maxm=%maxm&as_maxd=%maxd
		//%minm, %mind, %maxm, %maxd - minimum/maximum month/day
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		
		//pagination parameter? 'start' does not work. max size per page is 30
		final String url = "http://news.google.com/news?&hl=en&output=rss&scoring=n&as_drrb=b&as_minm=" + month + "&as_mind=" + day + "&as_maxm=" + month + "&as_maxd=" + day + "&num=30";

		//parse the rss
		try{
			DocumentBuilderFactory documenetBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documenetBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(url);
					
			document.getDocumentElement().normalize();
			
			NodeList items = document.getElementsByTagName("item");
			
			//extract data for each article
			for(int j = 0; j < items.getLength(); j++){
				Element itemElement = (Element)items.item(j);
				
				String itemTitle = itemElement.getElementsByTagName("title").item(0).getTextContent().replaceAll("&apos;", "'").replaceAll("&quot;", "'").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
				String itemLink = itemElement.getElementsByTagName("link").item(0).getTextContent();
				String itemPubDate = itemElement.getElementsByTagName("pubDate").item(0).getTextContent();
				
				itemLink = URLDecoder.decode(itemLink.substring(itemLink.indexOf("url=") + 4), "UTF-8");	//the feed nests the url and redirects to it, extract only the nested url
				
				try{
					this.processArticle(itemLink, itemTitle, itemPubDate);
				}
				catch(ParserConfigurationException exception){} //these exceptions should be logged - only a single article fails  
				catch(SAXException exception){}
				catch(XPathExpressionException exception){}
				catch(IOException exception){}
			}
		}catch(ParserConfigurationException exception){
			throw new ContentsAggregatorException("parser configuration error", AggregatorMethod.GOOGLE_NEWS_RSS_FEED);
		}catch(SAXException exception){
			throw new ContentsAggregatorException("xml parse exception", AggregatorMethod.GOOGLE_NEWS_RSS_FEED);
		}catch(IOException exception){
			throw new ContentsAggregatorException("can not fetch resource", AggregatorMethod.GOOGLE_NEWS_RSS_FEED);
		}
	}
	
	public void aggregateFacebook(Date date) throws ContentsAggregatorException {
		//TODO
	}
	
	public void aggregateTwitter(Date date) throws ContentsAggregatorException {
		//TODO
	}
	
	public void aggregateOther(Date date) throws ContentsAggregatorException {
		//TODO
	}
	
	private void processArticle(String articleURL, String title, String timestamp) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
		//do not analyze it if it exists (check by url)
		if(contentRepository.findBySourceUrl(articleURL) == null){
			AlchemyAPIAnalysisResult analysisResults = ContentsAggregator.analyzeContent(alchemyapi, alchemyapi_params, articleURL);
			
			analysisResults.setType("GOOGLE_NEWS");
			analysisResults.setUrl(articleURL);
			analysisResults.setTitle(title);
			analysisResults.setTimestamp(timestamp);
			
			ContentsAggregator.persistData(personRepository, contentRepository, analysisResults);
		}
	}
	
	private void processFacebookPost(String postURL){
		//TODO
	}
	
	private void processTweet(String tweetURL){
		//TODO
	}
	
	private void processOther(String URL){
		//TODO
	}
	
	private static AlchemyAPIAnalysisResult analyzeContent(AlchemyAPI alchemyapi, AlchemyAPI_CombinedParams alchemyapi_params, String contentURL) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
		Document document = alchemyapi.URLGetCombined(contentURL, alchemyapi_params);
		//System.out.println(getStringFromDocument(document));
		
		String text = document.getElementsByTagName("text").item(0).getTextContent();
		
		NodeList entities = document.getElementsByTagName("entity");
		
		List<Person> persons = new ArrayList<Person>();
		List<Rating> ratings = new ArrayList<Rating>();
		for(int k = 0; k < entities.getLength(); k++){
			Element entity = (Element)entities.item(k);
			
			//minimal relevance is 0.01
			if(entity.getElementsByTagName("type").item(0).getTextContent().equals("Person") && entity.getElementsByTagName("relevance").item(0).getTextContent().compareTo("0.01") > 0){
				String name = entity.getElementsByTagName("text").item(0).getTextContent();
				
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
				
				//defer fetching from dbpedia until before adding to data store (a prior confirmation that the person does not exist in the data store reduces the total traffic) 
				person.setShortBio(null);
				person.setPictureUrl(null);
				
				persons.add(person);
				
				Rating rating = new Rating();
				
				//set this after the person has been added to the data store
				rating.setPerson(null);
				
				rating.setScore(score);
				rating.setMixed(mixed);
				
				ratings.add(rating);
			}
		}
		
		return new AlchemyAPIAnalysisResult(null, null, text, null, null, persons, ratings);
	}
	
	private static void persistData(PersonRepository personRepository, ContentRepository contentRepository, AlchemyAPIAnalysisResult data){
		List<Person> persons = data.getPersons();
		List<Rating> ratings = data.getRatings();
		
		if(persons.size() > 0){
			for(int i = 0; i < persons.size(); i++){
				Person person = persons.get(i);
				Person found = null;
				
				if(person.getDbpediaUrl() != null){
					found = personRepository.findByDbpediaUrl(person.getDbpediaUrl());
				}else{
					found = personRepository.findByName(person.getName());
				}
				
				if(found == null){
					if(person.getDbpediaUrl() != null){
						//TODO: fetch short biography and picture url from dbpedia
					}
					found = personRepository.save(person);
					
					if(found == null){
						//persistance failed, throw exception
						return;
					}
				}
				
				//insert the id in the rating
				ratings.get(i).setPerson(found);
			}
			
			//save the article
			
			Content content = new Content();
			
			content.setType(data.getType());
			content.setSourceUrl(data.getUrl());
			content.setTitle(data.getTitle());
			content.setBody(data.getBody());
			content.setRatings(ratings);
			content.setTimestamp(data.getTimestamp());
			
			content = contentRepository.save(content);
			if(content == null){
				//persistance failed, throw exception
				return;
			}
		}
	}
	
	/*
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
	*/
	
	//this is only public to circumvent a Spring 'no class def found' exception
	public static class AlchemyAPIAnalysisResult {
		private String type;
		private String title;
		private String body;
		private String url;
		private String timestamp;
		
		private List<Person> persons;
		private List<Rating> ratings;
		
		public AlchemyAPIAnalysisResult(){
			this.type = null;
			this.title = null;
			this.body = null;
			this.url = null;
			this.timestamp = null;
			
			this.persons = new ArrayList<Person>();
			this.ratings = new ArrayList<Rating>();
		}
		
		public AlchemyAPIAnalysisResult(String type, String title, String body, String url, String timestamp, List<Person> persons, List<Rating> ratings){
			this.type = type;
			this.title = title;
			this.body = body;
			this.url = url;
			this.timestamp = timestamp;
			
			this.persons = persons;
			this.ratings = ratings;
		}
		
		public String getType(){
			return type;
		}
		
		public void setType(String type){
			this.type = type;
		}
		
		public String getTitle(){
			return title;
		}
		
		public void setTitle(String title){
			this.title = title;
		}
		
		public String getBody(){
			return body;
		}
		
		public void setBody(String body){
			this.body = body;
		}
		
		public String getUrl(){
			return url;
		}
		
		public void setUrl(String url){
			this.url = url;
		}
		
		public String getTimestamp(){
			return timestamp;
		}
		
		public void setTimestamp(String timestamp){
			this.timestamp = timestamp;
		}
		
		public List<Person> getPersons(){
			return persons;
		}
		
		public void setPersons(List<Person> persons){
			this.persons = persons;
		}
		
		public List<Rating> getRatings(){
			return ratings;
		}
		
		public void setRatings(List<Rating> ratings){
			this.ratings = ratings;
		}
	}
}