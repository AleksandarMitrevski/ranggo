package mk.finki.unip.ranggo.service;

import java.util.List;

import mk.finki.unip.ranggo.model.Content;
import mk.finki.unip.ranggo.model.Person;

public interface ContentsSelector {
	
	public List<Person> getAllPersons();
	
	public List<Content> getAllContents();
}