package mk.finki.ranggo.aggregator;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.io.IOException;
//import java.io.StringWriter;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_CombinedParams;

//import org.w3c.dom.Node;
import org.w3c.dom.Element;

import mk.finki.ranggo.aggregator.model.Concept;
import mk.finki.ranggo.aggregator.model.Content;
import mk.finki.ranggo.aggregator.model.Keyword;
import mk.finki.ranggo.aggregator.model.Person;
import mk.finki.ranggo.aggregator.model.PersonEntity;
import mk.finki.ranggo.aggregator.model.Taxonomy;
import mk.finki.ranggo.aggregator.repository.ContentRepository;
import mk.finki.ranggo.aggregator.repository.PersonRepository;
import mk.finki.ranggo.aggregator.ContentsAggregatorException.AggregatorMethod;

public class ContentsAggregatorImpl implements ContentsAggregator {
	
	private PersonRepository personRepository;
	
	private ContentRepository contentRepository;
	
	private AlchemyAPI alchemyapi;
	private AlchemyAPI_CombinedParams alchemyapi_params;
	
	public ContentsAggregatorImpl(String alchemyapi_key, PersonRepository personRepository, ContentRepository contentRepository) {
		alchemyapi = AlchemyAPI.GetInstanceFromString(alchemyapi_key);

		alchemyapi_params = new AlchemyAPI_CombinedParams();
		alchemyapi_params.setLinkedData(true);
		alchemyapi_params.setSentiment(true);
		alchemyapi_params.setShowSourceText(true);
		alchemyapi_params.setExtract("entity");
		alchemyapi_params.setExtract("concept");
		alchemyapi_params.setExtract("keyword");
		alchemyapi_params.setExtract("taxonomy");
		
		this.personRepository = personRepository;
		this.contentRepository = contentRepository;
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
		//currently fetching 10 articles to reduce the method runtime
		final String url = "http://news.google.com/news?&hl=en&output=rss&scoring=n&as_drrb=b&as_minm=" + month + "&as_mind=" + day + "&as_maxm=" + month + "&as_maxd=" + day + "&num=10";

		//parse the rss
		try{
			DocumentBuilderFactory documenetBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documenetBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(url);
					
			document.getDocumentElement().normalize();
			
			NodeList items = document.getElementsByTagName("item");
			
			//extract data for each article
			for(int i = 0; i < items.getLength(); i++){
				Element itemElement = (Element)items.item(i);
				
				String itemTitle = itemElement.getElementsByTagName("title").item(0).getTextContent().replaceAll("&apos;", "'").replaceAll("&quot;", "'").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
				String itemLink = itemElement.getElementsByTagName("link").item(0).getTextContent();
				String itemPubDate = itemElement.getElementsByTagName("pubDate").item(0).getTextContent();
				
				itemLink = URLDecoder.decode(itemLink.substring(itemLink.indexOf("url=") + 4), "UTF-8");	//the feed nests the url and redirects to it, extract only the nested url
				
				try{
					this.processGoogleNewsArticle(itemLink, itemTitle, itemPubDate);
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
	
	public void aggregateHuffingtonPost() throws ContentsAggregatorException {
		//fetches current index contents
		final String url = "http://www.huffingtonpost.com/feeds/index.xml";

		//parse the rss
		try{
			DocumentBuilderFactory documenetBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documenetBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(url);
					
			document.getDocumentElement().normalize();
			
			NodeList items = document.getElementsByTagName("item");
			
			//extract data for each article
			for(int i = 0; i < items.getLength(); i++){
				Element itemElement = (Element)items.item(i);
				
				String itemTitle = itemElement.getElementsByTagName("title").item(0).getTextContent().replace("<![CDATA[", "").replace("]]>", "").replaceAll("&apos;", "'").replaceAll("&quot;", "'").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").trim();
				String itemLink = itemElement.getElementsByTagName("link").item(0).getTextContent();
				String itemPubDate = itemElement.getElementsByTagName("pubDate").item(0).getTextContent();
				
				itemLink = URLDecoder.decode(itemLink, "UTF-8");
				
				try{
					this.processOther(itemLink, itemTitle, itemPubDate);
				}
				catch(ParserConfigurationException exception){} //these exceptions should be logged - only a single article fails  
				catch(SAXException exception){}
				catch(XPathExpressionException exception){}
				catch(IOException exception){}
			}
		}catch(ParserConfigurationException exception){
			throw new ContentsAggregatorException("parser configuration error", AggregatorMethod.HUFFINGTON_POST);
		}catch(SAXException exception){
			throw new ContentsAggregatorException("xml parse exception", AggregatorMethod.HUFFINGTON_POST);
		}catch(IOException exception){
			throw new ContentsAggregatorException("can not fetch resource", AggregatorMethod.HUFFINGTON_POST);
		}
	}
	
	public void aggregateTest() throws ContentsAggregatorException {
		final int CONTENT_COUNT = 20;
		
		String[] articles = new String[CONTENT_COUNT];
		String[] titles = new String[CONTENT_COUNT];
		String[] timestamps = new String[CONTENT_COUNT];
		
		int counter = 0;
		
		//populating content information
		
		articles[counter] = "http://nymag.com/daily/intelligencer/2015/09/frank-rich-in-praise-of-donald-trump.html";
		titles[counter] = "The Importance of Donald Trump";
		timestamps[counter] = "Sun, 20 Sep 2015 21:00:00 GMT";
		counter++;
		
		articles[counter] = "https://www.washingtonpost.com/opinions/what-americans-think-should-be-done-on-illegal-immigration/2015/10/11/4f6f6df6-6ed7-11e5-9bfe-e59f5e244f92_story.html";
		titles[counter] = "What Americans think should be done on illegal immigration";
		timestamps[counter] = "Sun, 11 Oct 2015 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://thinkprogress.org/politics/2015/11/22/3724879/donald-trump-black-lives-matter-protester-beating/";
		titles[counter] = "Donald Trump: My Fans Were Right To Beat Up Black Protester";
		timestamps[counter] = "Sun, 22 Nov 2015 09:30:00 GMT";
		counter++;
		
		articles[counter] = "http://edition.cnn.com/2015/12/11/politics/donald-trump-ted-cruz-iowa-ethanol/";
		titles[counter] = "Donald Trump launches first attacks against Ted Cruz";
		timestamps[counter] = "Fri, 11 Dec 2015 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://edition.cnn.com/2015/12/18/politics/donald-trump-praises-defends-vladimir-putin/";
		titles[counter] = "Donald Trump lavishes praise on 'leader' Putin";
		timestamps[counter] = "Fri, 18 Dec 2015 15:19:00 GMT";
		counter++;
		
		articles[counter] = "http://www.cbsnews.com/news/donald-trump-compares-bill-clinton-to-bill-cosby/";
		titles[counter] = "Donald Trump compares Bill Clinton to Bill Cosby";
		timestamps[counter] = "Wed, 06 Jan 2016 08:56:00 GMT";
		counter++;
		
		articles[counter] = "http://www.sbnation.com/lookit/2016/1/28/10858584/australian-open-video-roger-federer-novak-djokovic-point";
		titles[counter] = "Roger Federer wins mind-boggling point against Novak Djokovic in ridiculous rally";
		timestamps[counter] = "Thu, 28 Jan 2016 09:47:00 GMT";
		counter++;
		
		articles[counter] = "http://www.theguardian.com/sport/2016/jan/31/novak-djokovic-andy-murray-australian-open-final";
		titles[counter] = "Novak Djokovic beats Andy Murray to win the 2016 Australian Open final";
		timestamps[counter] = "Sun, 31 Jan 2016 11:37:00 GMT";
		counter++;

		articles[counter] = "https://www.washingtonpost.com/politics/a-sigh-of-relief-in-swinging-northern-virginia-when-trump-fell-short-in-iowa/2016/02/02/1f48fb36-c9d3-11e5-a7b2-5a2f824b02c9_story.html";
		titles[counter] = "A sigh of relief in Northern Virginia as Trump falls short in Iowa";
		timestamps[counter] = "Tue, 2 Feb 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://www.ibtimes.com/angelina-jolie-brad-pitt-divorce-angie-insanely-jealous-over-selena-gomez-cheating-2312126";
		titles[counter] = "Angelina Jolie And Brad Pitt To Divorce? Angie Is 'Insanely Jealous' Over Selena Gomez Cheating Rumors: Report";
		timestamps[counter] = "Wed, 17 Feb 2016 13:07:00 GMT";
		counter++;
		
		articles[counter] = "http://www.nytimes.com/2016/02/24/us/politics/nevada-caucus-gop.html";
		titles[counter] = "Donald Trump Wins Nevada Caucuses, Collecting Third Straight Victory";
		timestamps[counter] = "Tue, 23 Feb 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://www.nytimes.com/politics/first-draft/2016/03/16/donald-trump-warns-of-riots-if-party-blocks-him-at-convention/";
		titles[counter] = "Donald Trump Warns of 'Riots' if Party Blocks Him at Convention";
		timestamps[counter] = "Wed, 16 Mar 2016 15:15:00 GMT";
		counter++;
		
		articles[counter] = "https://www.washingtonpost.com/blogs/post-partisan/wp/2016/03/23/ted-cruzs-harebrained-harmful-policy-towards-american-muslims/";
		titles[counter] = "Ted Cruz's harebrained, harmful policy toward American Muslims";
		timestamps[counter] = "Wed, 23 Mar 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://espn.go.com/tennis/story/_/id/15079824/miami-open-rafael-nadal-roger-federer-host-questions-answer";
		titles[counter] = "Rafael Nadal, Roger Federer among top stars with questions to answer";
		timestamps[counter] = "Mon, 28 Mar 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://www.houstonchronicle.com/sports/article/Feliciano-Lopez-glad-he-plays-in-tennis-era-of-7235565.php";
		titles[counter] = "Feliciano Lopez glad he plays in tennis era of Novak Djokovic & Co.";
		timestamps[counter] = "Thu, 07 Apr 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://www.vanityfair.com/hollywood/2016/04/brad-pitt-and-angelina-jolie-london-mansion";
		titles[counter] = "Brad Pitt and Angelina Jolie's $21,000/Month London Mansion Is an Inspiration to All Business Travelers";
		timestamps[counter] = "Fri, 22 Apr 2016 10:38:00 GMT";
		counter++;
		
		articles[counter] = "https://www.washingtonpost.com/news/the-fix/wp/2016/04/26/how-donald-trump-is-dominating-the-i-95-primary/";
		titles[counter] = "How Donald Trump dominated Tuesday's primaries";
		timestamps[counter] = "Tue, 26 Apr 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "http://www.inquisitr.com/3042986/johnny-depp-was-under-medical-care-after-filming/";
		titles[counter] = "Johnny Depp Was Under Medical Care After Filming";
		timestamps[counter] = "Thu, 28 Apr 2016 00:00:00 GMT";
		counter++;
		
		articles[counter] = "https://www.washingtonpost.com/news/the-fix/wp/2016/04/29/the-gop-is-running-on-empty-so-why-are-people-debating-whether-to-stop-for-gas/";
		titles[counter] = "Mitt Romney's chief strategist says Donald Trump is 'uniquely unqualified' to be president";
		timestamps[counter] = "Fri, 29 Apr 2016 09:53:00 GMT";
		counter++;
		
		articles[counter] = "http://www.mirror.co.uk/3am/celebrity-news/kirk-norcross-gets-arrested-dangerous-7864035";
		titles[counter] = "Kirk Norcross 'gets ARRESTED for dangerous driving' hours after Snapchatting behind the wheel";
		timestamps[counter] = "Fri, 29 Apr 2016 17:43:00 GMT";
		counter++; //= CONTENT_COUNT
		
		//end populating content information
		
		for(int i = 0; i < CONTENT_COUNT; i++){
			try{
				this.processOther(articles[i], titles[i], timestamps[i]);
			}
			catch(ParserConfigurationException exception){} //these exceptions should be logged - only a single link fails  
			catch(SAXException exception){}
			catch(XPathExpressionException exception){}
			catch(IOException exception){}
		}
	}
	
	private void processGoogleNewsArticle(String articleURL, String title, String timestamp) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
		//do not analyze it if it exists in the data store (check by url)
		if(contentRepository.findBySourceUrl(articleURL) == null){
			AlchemyAPIAnalysisResult analysisResults = ContentsAggregatorImpl.analyzeContent(alchemyapi, alchemyapi_params, articleURL);
			
			analysisResults.setType("GOOGLE_NEWS");
			analysisResults.setUrl(articleURL);
			analysisResults.setTitle(title);
			analysisResults.setTimestamp(timestamp);
			
			ContentsAggregatorImpl.persistData(personRepository, contentRepository, analysisResults);
		}
	}
	
	private void processOther(String URL, String title, String timestamp) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
		//do not analyze it if it exists in the data store (check by url)
		if(contentRepository.findBySourceUrl(URL) == null){
			AlchemyAPIAnalysisResult analysisResults = ContentsAggregatorImpl.analyzeContent(alchemyapi, alchemyapi_params, URL);
			
			analysisResults.setType("OTHER");
			analysisResults.setUrl(URL);
			analysisResults.setTitle(title);
			analysisResults.setTimestamp(timestamp);
			
			ContentsAggregatorImpl.persistData(personRepository, contentRepository, analysisResults);
		}
	}
	
	private static AlchemyAPIAnalysisResult analyzeContent(AlchemyAPI alchemyapi, AlchemyAPI_CombinedParams alchemyapi_params, String contentURL) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
		Document document = alchemyapi.URLGetCombined(contentURL, alchemyapi_params);
		//System.out.println(getStringFromDocument(document));
		
		String text = document.getElementsByTagName("text").item(0).getTextContent();
		text = text.replaceAll("�", "'").replaceAll("�", "\"").replaceAll("�", "\"");
		
		//extracting entities
		
		Element entitiesElement = (Element)document.getElementsByTagName("entities").item(0);
		NodeList entities = entitiesElement.getElementsByTagName("entity");
		
		List<Person> persons = new ArrayList<Person>();
		List<PersonEntity> personEntities = new ArrayList<PersonEntity>();
		for(int i = 0; i < entities.getLength(); i++){
			Element entity = (Element)entities.item(i);
			
			if(entity.getElementsByTagName("type").item(0).getTextContent().equals("Person")){
				String name = entity.getElementsByTagName("text").item(0).getTextContent();
				
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
	
		return new AlchemyAPIAnalysisResult(null, null, text, null, null, persons, personEntities, concepts, keywords, taxonomies);
	}
	
	private static void persistData(PersonRepository personRepository, ContentRepository contentRepository, AlchemyAPIAnalysisResult data){
		List<Person> persons = data.getPersons();
		List<PersonEntity> personEntities = data.getPersonEntities();
		
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
					found = personRepository.save(person);
				}
				
				//insert the id in the rating
				personEntities.get(i).setPerson(found);
			}
			
			//save the article
			
			Content content = new Content();
			
			content.setType(data.getType());
			content.setSourceUrl(data.getUrl());
			content.setTitle(data.getTitle());
			content.setBody(data.getBody());
			content.setPersonEntities(personEntities);
			content.setConcepts(data.getConcepts());
			content.setKeywords(data.getKeywords());
			content.setTaxonomies(data.getTaxonomies());
			content.setTimestamp(data.getTimestamp());
			
			content = contentRepository.save(content);
			
			//fetch short biography and picture url for each person from dbpedia
			
			for(PersonEntity personEntity : personEntities){
				Person person = personEntity.getPerson();
				if(person.getDbpediaUrl() != null && (person.getPictureUrl() == null || person.getShortBio() == null)){
					//fetch the details
					ContentsAggregatorImpl.fetchPersonDetailsFromDbpedia(person);
					
					//save the updated person
					person = personRepository.save(person);
					
					//set the updated person
					personEntity.setPerson(person);
				}
			}
		}
	}
	
	private static void fetchPersonDetailsFromDbpedia(Person person){
		String url = person.getDbpediaUrl();
		url = url.replace("http://dbpedia.org/resource/", "http://dbpedia.org/data/").replace("http://dbpedia.org/page/", "http://dbpedia.org/data/");
		url += ".xml";
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(url);
			//System.out.println(getStringFromDocument(document));
			
			//extract abstract (short biography)
			NodeList abstracts = document.getElementsByTagName("dbo:abstract");
			for(int i = 0; i < abstracts.getLength(); i++){
				Element entity = (Element)abstracts.item(i);
				if(entity.hasAttribute("xml:lang") && entity.getAttribute("xml:lang").equals("en")){
					String dbAbstract = entity.getTextContent().replaceAll("&#39;", "'").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("�", "-");
					person.setShortBio(dbAbstract);
				}
			}
		
			//extract thumbnail (picture url)
			NodeList thumbnails = document.getElementsByTagName("dbo:thumbnail");
			for(int i = 0; i < thumbnails.getLength(); i++){
				Element entity = (Element)thumbnails.item(i);
				if(entity.hasAttribute("rdf:resource")){
					String dbThumbnail = entity.getAttribute("rdf:resource");
					person.setPictureUrl(dbThumbnail);
				}
			}
		}
		catch(ParserConfigurationException exception){}
		catch(SAXException exception){}
		catch(IOException exception){}
	}
	
	/*
	private static String getStringFromDocument(Document doc){
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
	
	//declared public because otherwise Spring failes to autowire it
	public static class AlchemyAPIAnalysisResult {
		private String type;
		private String title;
		private String body;
		private String url;
		private String timestamp;
		
		private List<Person> persons;
		private List<PersonEntity> personEntities;
		private List<Concept> concepts;
		private List<Keyword> keywords;
		private List<Taxonomy> taxonomies;
		
		public AlchemyAPIAnalysisResult(){
			//does nothing
		}
		
		public AlchemyAPIAnalysisResult(String type, String title, String body, String url, String timestamp, List<Person> persons, List<PersonEntity> personEntities, List<Concept> concepts, List<Keyword> keywords, List<Taxonomy> taxonomies){
			this.type = type;
			this.title = title;
			this.body = body;
			this.url = url;
			this.timestamp = timestamp;
			
			this.persons = persons;
			this.personEntities = personEntities;
			this.concepts = concepts;
			this.keywords = keywords;
			this.taxonomies = taxonomies;
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
		
		public List<PersonEntity> getPersonEntities(){
			return personEntities;
		}
		
		public void setPersonEntities(List<PersonEntity> personEntities){
			this.personEntities = personEntities;
		}
		
		public List<Concept> getConcepts(){
			return concepts;
		}
		
		public void setConcepts(List<Concept> concepts){
			this.concepts = concepts;
		}
		
		public List<Keyword> getKeywords(){
			return keywords;
		}
		
		public void setKeywords(List<Keyword> keywords){
			this.keywords = keywords;
		}
		
		public List<Taxonomy> getTaxonomies(){
			return taxonomies;
		}
		
		public void setTaxonomies(List<Taxonomy> taxonomies){
			this.taxonomies = taxonomies;
		}
	}
}