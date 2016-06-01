package mk.finki.ranggo.webapp.service.impl;

import java.util.ArrayList;
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

}