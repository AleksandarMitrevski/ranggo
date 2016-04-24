package mk.finki.ranggo.aggregator.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "contents")
public class Content {
	
	@Id
	@NotNull
	private String id;	//avoid org.bson.types.ObjectId
	
	@NotNull
	@Indexed(unique = true)
	private String sourceUrl;
	
	@NotNull
	private String type;
	
	private String title;
	
	@NotNull
	private String body;
	
	@NotNull
	private List<PersonEntity> personEntities;
	
	private List<Concept> concepts;
	
	private List<Keyword> keywords;
	
	private List<Taxonomy> taxonomies;
	
	@NotNull
	private String timestamp;

	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getSourceUrl(){
		return sourceUrl;
	}
	
	public void setSourceUrl(String sourceUrl){
		this.sourceUrl = sourceUrl;
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
	
	public String getTimestamp(){
		return timestamp;
	}
	
	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}
	
	public Content(){
		this.sourceUrl = null;
		this.type = null;
		this.title = null;
		this.body = null;
		this.personEntities = null;
		this.concepts = null;
		this.keywords = null;
		this.taxonomies = null;
		this.timestamp = null;
	}
}