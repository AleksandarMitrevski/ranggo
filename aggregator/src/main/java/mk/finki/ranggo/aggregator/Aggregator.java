package mk.finki.ranggo.aggregator;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import mk.finki.ranggo.aggregator.model.Person;
import mk.finki.ranggo.aggregator.model.Content;
import mk.finki.ranggo.aggregator.repository.ContentRepository;
import mk.finki.ranggo.aggregator.repository.PersonRepository;

public class Aggregator
{	
	public static void main(String[] args){
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/application-config.xml");

		PersonRepository personRepository = context.getBean(PersonRepository.class);
		ContentRepository contentRepository = context.getBean(ContentRepository.class);
		
		//data storage example (does not generate duplicates due to 'unique' constraints)
		//see http://github.com/Schenock/ranggo/tree/master/webapp/src/main/java/mk/finki/unip/ranggo/service/impl/ContentsAggregatorImpl.java
		
		Person person = new Person();
		Content content = new Content();
		
		person.setName("Donald Trump");
		content.setType("GOOGLE_NEWS");
		
		person = personRepository.save(person);
		content = contentRepository.save(content);
		
		System.out.println("Person ID: " + person.getId());
		System.out.println("Content ID: " + content.getId());
		
		context.close();
	}
}