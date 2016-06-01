package mk.finki.ranggo.webapp.service;

import java.util.List;

import mk.finki.ranggo.webapp.model.Content;
import mk.finki.ranggo.webapp.model.Person;

public interface ContentsSelector {
	
	public List<Person> getAllPersons();
	
	public List<Person> getTop5People();
	
	public List<Content> getAllContents();
	
	public List<Content> getRatingsForPerson(String id);
	
	public List<Content> getLatestNews();
	
	public List<Double> getAverageRatingForPerson(String id);
	
}