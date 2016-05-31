package mk.finki.ranggo.aggregator.model;

import java.util.List;

public class AlchemyAPIAnalysisResult {
	private String type;
	private String title;
	private String body;
	private String url;
	private String timestamp;
	
	private List<Person> persons;
	private List<PersonEntity> personEntities;
	private List<Concept> concepts;
	private List<Keyword> keywords;
	private List<Taxonomy> taxonomies;
	
	public AlchemyAPIAnalysisResult(){
		//does nothing
	}
	
	public AlchemyAPIAnalysisResult(String type, String title, String body, String url, String timestamp, List<Person> persons, List<PersonEntity> personEntities, List<Concept> concepts, List<Keyword> keywords, List<Taxonomy> taxonomies){
		this.type = type;
		this.title = title;
		this.body = body;
		this.url = url;
		this.timestamp = timestamp;
		
		this.persons = persons;
		this.personEntities = personEntities;
		this.concepts = concepts;
		this.keywords = keywords;
		this.taxonomies = taxonomies;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getBody(){
		return body;
	}
	
	public void setBody(String body){
		this.body = body;
	}
	
	public String getUrl(){
		return url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getTimestamp(){
		return timestamp;
	}
	
	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}
	
	public List<Person> getPersons(){
		return persons;
	}
	
	public void setPersons(List<Person> persons){		this.persons = persons;
	}
	
	public List<PersonEntity> getPersonEntities(){
		return personEntities;
	}
	
	public void setPersonEntities(List<PersonEntity> personEntities){
		this.personEntities = personEntities;
	}
	
	public List<Concept> getConcepts(){
		return concepts;
	}
	
	public void setConcepts(List<Concept> concepts){
		this.concepts = concepts;
	}
	
	public List<Keyword> getKeywords(){
		return keywords;
	}
	
	public void setKeywords(List<Keyword> keywords){
		this.keywords = keywords;
	}
	
	public List<Taxonomy> getTaxonomies(){
		return taxonomies;
	}
	
	public void setTaxonomies(List<Taxonomy> taxonomies){
		this.taxonomies = taxonomies;
	}
}