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
		Aggregator.test(aggregator);
		
		//executes update method
		Aggregator.update(null, aggregator);
	
		context.close();
	}
	
	//a single invocation of this method costs > 1000 AlchemyAPI transactions
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
	    	
	    	try{
	    		aggregator.aggregateNYTimes(dateObj);
	    	}catch(ContentsAggregatorException exception){
	    		
	    	}
	    	try{
	    		aggregator.aggregateTheGuardian(dateObj);
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