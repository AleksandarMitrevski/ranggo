package mk.finki.ranggo.webapp.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mk.finki.ranggo.webapp.model.Content;
import mk.finki.ranggo.webapp.model.Person;
import mk.finki.ranggo.webapp.model.PersonEntity;
import mk.finki.ranggo.webapp.repository.ContentRepository;
import mk.finki.ranggo.webapp.repository.PersonRepository;
import mk.finki.ranggo.webapp.service.ContentsSelector;

@Service
public class ContentsSelectorImpl implements ContentsSelector {
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private ContentRepository contentRepository;
	
	public List<Person> getAllPersons(){
		Iterable<Person> personsIter = personRepository.findAll();
		
		List<Person> persons = new LinkedList<Person>();
		for(Person person : personsIter){
			persons.add(person);
		}
		
		return persons;
	}
	
	public List<Content> getAllContents(){
		Iterable<Content> contentsIter = contentRepository.findAll();
		
		List<Content> contents = new LinkedList<Content>();
		for(Content content : contentsIter){
			contents.add(content);
		}
		
		return contents;
	}

	public List<Content> getContentsByDateAndDate(String date, List<String> type){
		List<Content> contents = getAllContents();
		List<Content> result = new ArrayList<Content>();
		
        System.out.println("type:" + type);
		System.out.println("type.length: " + type.size());
        
		for(Content content : contents){
			try{
				DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
		        DateFormat inputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");

		        Date dateObj = inputFormat.parse(content.getTimestamp());
		        String outputText = outputFormat.format(dateObj);
		        
		        if(outputText.equals(date)){
		        	if(type.size() == 0){
		        		result.add(content);
		        	} else {
			        	for(String str : type){
			        		if(str.equals(content.getType())){
			        			result.add(content);
			        			break;
			        		}
			        	}
		        	}
		        }
		        
			} catch(Exception e){
				
			}
		}
		
		return result;
	}
	
	public List<Content> getRatingsForPerson(String id) {
		return contentRepository.getContentsForPerson(id);
	}
	
	public List<Content> getLatestNews() {
		return contentRepository.getNewestContents();
	}

	public List<Person> getTop5People() {
		List<Person> people = getAllPersons();
		List<Person> result = new ArrayList<Person>();
		int end = people.size() > 5 ? 5 : people.size();
		for(int i=0;i<end;i++){
			result.add(people.get(i));
		}
		return result;
	}
	
	public List<Double> getAverageRatingForPerson(String id){
		List<Content> contents = getAllContents();
		
		double score = 0;
		int count = 0;
		for(int i=0;i<contents.size();i++){
			Content currContent = contents.get(i);
			System.out.println("Person entities:" + currContent.getPersonEntities());
			if(currContent.getPersonEntities()!= null){
				for(PersonEntity pEntity : currContent.getPersonEntities()){
					if(pEntity.getPerson() != null){
						if(pEntity.getPerson().getId().equals(id)){
							score += pEntity.getScore();
							count ++;
						}
					}
				}
			}
		}
		
		List<Double> res = new ArrayList<Double>();
		res.add(score/count);
		return res;
	}
	
	public List<String> getCategories(){
		List<Person> people = getAllPersons();
		List<String> result = new ArrayList<String>();
		
		for(Person p : people){
			if(p.getCategories() == null){
				continue;
			}
			for(String category : p.getCategories()){
				if(!result.contains(category)){
					result.add(category);
				}
			}
		}
		
		return result;
	}

	public Person findByID(String id){
		return personRepository.findByID(id);
	}
	
}