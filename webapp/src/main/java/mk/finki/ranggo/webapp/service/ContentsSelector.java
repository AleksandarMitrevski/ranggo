package mk.finki.ranggo.webapp.service;

import java.util.List;

import mk.finki.ranggo.webapp.model.Content;
import mk.finki.ranggo.webapp.model.Person;
import mk.finki.ranggo.webapp.model.SearchObject;

public interface ContentsSelector {
	
	public List<Person> getAllPersons();
	
	public List<Person> getTop5People();
	
	public List<Content> getAllContents();
	
	public List<Content> getRatingsForPerson(String id);
	
	public List<Content> getLatestNews();
	
	public List<Double> getAverageRatingForPerson(String id);
	
	public List<String> getCategories();
	
	public Person findByID(String id);
	
	public List<Content> getContentsByDateAndDate(String date, List<String> preferences);
	
	public Content getContentByID(String id);
	
	public List<Content> getSimilarContents(String id);
	
	public List<Content> getFilteredContents(SearchObject searchObject);
	
}