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

import mk.finki.ranggo.webapp.model.Concept;
import mk.finki.ranggo.webapp.model.Content;
import mk.finki.ranggo.webapp.model.Person;
import mk.finki.ranggo.webapp.model.PersonEntity;
import mk.finki.ranggo.webapp.model.SearchObject;
import mk.finki.ranggo.webapp.model.Taxonomy;
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
	
	public Content getContentByID(String id){
		return contentRepository.getContentByID(id);
	}
	
	public List<Content> getSimilarContents(String id){
		Content content = getContentByID(id);
		List<Content> contents = getAllContents();
		
		List<Content> result = new ArrayList<Content>();
		
		List<String> people = new ArrayList<String>();
		for(PersonEntity entity : content.getPersonEntities()){
			if(entity.getPerson()!=null){
				if(!people.contains(entity.getPerson().getId())){
					people.add(entity.getPerson().getId());
				}
			}
		}
		
	
		List<String> concepts = new ArrayList<String>();
		for(Concept concept : content.getConcepts()){
			if(!concepts.contains(concept.getText())){
				concepts.add(concept.getText());
			}
		}
		
		for(Content c : contents){
			if(!c.getId().equals(content.getId()) && !c.getType().equals("STATIC")){
				int countPeople = 0;
				int countConcepts = 0;
				
				for(PersonEntity entity : c.getPersonEntities()){
					if(entity.getPerson()!=null){
						for(String person : people){
							if(person.equals(entity.getPerson().getId())){
								countPeople++;
								break;
							}
						}
					}
				}
				
				for(Concept concept : c.getConcepts()){
					for(String con : concepts){
						if(con.equals(concept.getText())){
							countConcepts++;
							break;
						}
					}
				}
				
				if(countPeople > people.size()*(1/3.0) || countConcepts > concepts.size()*(1/3.0)){
					result.add(c);
				}
			}
		}	
		
		return result;
	}
	
	public List<Content> getFilteredContents(SearchObject searchObject){
		List<Content> contents = getAllContents();
		List<Content> result = new ArrayList<Content>();
		
		if(!searchObject.getDateFrom().equals("") && !searchObject.getDateTo().equals("")){
			if(compareDates(searchObject.getDateFrom(), searchObject.getDateTo())>0){
				String date = searchObject.getDateFrom();
				searchObject.setDateFrom(searchObject.getDateTo());
				searchObject.setDateTo(date);
			}
		}
		
	
		for(Content content : contents){
			try{
				DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
		        DateFormat inputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
	
		        Date dateObj = inputFormat.parse(content.getTimestamp());
		        String outputText = outputFormat.format(dateObj);
		        
		        if(!searchObject.getDateFrom().equals("")){
		        	if(compareDates(outputText, searchObject.getDateFrom())<0){
		        		continue;
		        	}
		        }
		        
		        if(!searchObject.getDateTo().equals("")){
		        	if(compareDates(outputText, searchObject.getDateTo())>0){
		        		continue;
		        	} 
		        } 
		        
		        
		        if(!searchObject.getTitle().equals("")){
			        if(!compareTitles(content.getTitle(), searchObject.getTitle())){
			        	continue;
			        }
		        }
		        
		        if(searchObject.getKeywords().size() > 0){
		        	if(!compareKeywords(content.getConcepts(), searchObject.getKeywords())){
			        	continue;
			        }
		        }
		        
		        if(searchObject.getPeople().size() > 0){
		        	if(!comparePeople(content.getPersonEntities(), searchObject.getPeople())){
			        	continue;
			        }
		        }
		        
		
		        result.add(content);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public boolean comparePeople(List<PersonEntity> personEntities, List<String> people){
		int count = 0;
		
		for(String person : people){
			for(PersonEntity entity : personEntities){
				if(entity.getPerson()!=null && entity.getPerson().getId()!=null){
					if(person.equals(entity.getPerson().getId())){
						count++;
						break;
					}
				}
			}
		}
		
		return count == people.size();
	}
	
	public boolean compareKeywords(List<Concept> originalConcepts, List<String> concepts){
		int count = 0;
		
		for(String con : concepts){
			for(Concept concept : originalConcepts){
				if(getLetters(concept.getText()).toLowerCase().equals(getLetters(con).toLowerCase())){
					count++;
					break;
				}
			}
		}
		
	    return count == concepts.size();
	}
	
	public boolean compareTitles(String originalTitle, String toCompareWith){
		if(toCompareWith.equals("")){
			return true;
		}
		
		String[] originalParts = originalTitle.split(" ");
		String[] parts = toCompareWith.split(" ");
		
		int count = 0;
		
		for(int i=0;i<parts.length;i++){
			for(int j=0;j<originalParts.length;j++){
				if(getLetters(parts[i]).toLowerCase().equals(getLetters(originalParts[j]).toLowerCase())){
					count++;
					break;
				}
			}
		}
		
		return count == parts.length;
	}
	
	public int compareDates(String date1, String date2){
		String[] first = date1.split("\\.");
		String[] second = date2.split("\\.");
		 
		int firstDay = Integer.parseInt(first[0]);
		int firstMonth = Integer.parseInt(first[1]);
		int firstYear = Integer.parseInt(first[2]);
		
		int secondDay = Integer.parseInt(second[0]);
		int secondMonth = Integer.parseInt(second[1]);
		int secondYear = Integer.parseInt(second[2]);
		
		if(firstYear < secondYear){
			return -1;
		} else if(firstYear > secondYear){
			return 1;
		} else {
			if(firstMonth < secondMonth){
				return -1;
			} else if(firstMonth > secondMonth){
				return 1;
			} else {
				if(firstDay < secondDay){
					return -1;
				} else if(firstDay > secondDay){
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
	
	public String getLetters(String word){
		return word.replaceAll("\\P{L}+", "");
	}

	
}