package mk.finki.unip.ranggo.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mk.finki.unip.ranggo.model.Content;
import mk.finki.unip.ranggo.model.Person;
import mk.finki.unip.ranggo.repository.ContentRepository;
import mk.finki.unip.ranggo.repository.PersonRepository;
import mk.finki.unip.ranggo.service.ContentsSelector;

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
}