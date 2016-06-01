package mk.finki.ranggo.aggregator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import mk.finki.ranggo.aggregator.model.Content;
import mk.finki.ranggo.aggregator.model.Person;
import mk.finki.ranggo.aggregator.repository.ContentRepository;
import mk.finki.ranggo.aggregator.repository.PersonRepository;

public class Aggregator
{	
	public static void main(String[] args){
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/application-config.xml");

		//make sure alchemyapi.key hold your license key
		final String alchemyapi_key = context.getBeanFactory().resolveEmbeddedValue("${alchemyapi.key}");
		
		PersonRepository personRepository = context.getBean(PersonRepository.class);
		ContentRepository contentRepository = context.getBean(ContentRepository.class);
		
		ContentsAggregator aggregator = new ContentsAggregatorImpl(alchemyapi_key, personRepository, contentRepository);
		
		//populates the data store with the test dataset
		//Aggregator.test(aggregator);
		
		
		Aggregator.update(null, aggregator);
		
		
		//data storage example (does not generate duplicates due to 'unique' constraints)
		//see http://github.com/Schenock/ranggo/tree/master/webapp/src/main/java/mk/finki/unip/ranggo/service/impl/ContentsAggregatorImpl.java
//		
//		Person person = new Person();
//		Content content = new Content();
//		
//		person.setName("Donald Trump");
//		content.setType("GOOGLE_NEWS");
//		
//		person = personRepository.save(person);
//		content = contentRepository.save(content);
//		
//		System.out.println("Person ID: " + person.getId());
//		System.out.println("Content ID: " + content.getId());
		
		
		context.close();
	}
	
	//each invocation currently costs 120 AlchemyAPI transactions 
	private static void update(String date, ContentsAggregator aggregator){
		//if 'date' is null, the method aggregates contents published on the current date
		//if it is, it aggregates the data published on that date
		//parameter format is dd.mm.yyyy, assumed UTC+0
		
		//parse or generate date object 
		Date dateObj = null;
		if(date != null){
			try{
				final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				dateObj = simpleDateFormat.parse(date);
				
			}catch(ParseException e){}
		}else{
			//no date parameter, default case is today
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			dateObj = calendar.getTime();
		}
		
	    if(dateObj != null){
			try{
				aggregator.aggregateGoogleNewsRSSFeed(dateObj);
			}catch(ContentsAggregatorException exception){
				//log this
			}
			
			try{
				aggregator.aggregateHuffingtonPost();
			}catch(ContentsAggregatorException exception){
				//log this
			}
	    	
	    	try{
	    		aggregator.aggregateDnevnik();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateFokus();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateKurir();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateLibertas();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateNovaTV();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateRepublika();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateTelma();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateUtrinskiVesnik();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateVecher();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateVest();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	
	    	try{
	    		aggregator.aggregateVesti24();
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    }
	}
	
	//20 static articles - 120 AlchemyAPI transactions 
	private static void test(ContentsAggregator aggregator){				
		try{
			aggregator.aggregateTest();
		}catch(ContentsAggregatorException exception){
			//log this
		}
	}
}