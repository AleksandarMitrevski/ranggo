package mk.finki.ranggo.aggregator.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@Document(collection = "persons")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {
	@Id
	@NotNull
	private String id;	//avoid org.bson.types.ObjectId
	
	@Indexed(unique = true)
	private String dbpediaUrl;
	
	@NotNull
	private String name;
	
	private String shortBio;
	
	private List<String> categories;
	
	private String pictureUrl;

	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getDbpediaUrl(){
		return dbpediaUrl;
	}
	
	public void setDbpediaUrl(String dbpediaUrl){
		this.dbpediaUrl = dbpediaUrl;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getShortBio(){
		return shortBio;
	}
	
	public void setShortBio(String shortBio){
		this.shortBio = shortBio;
	}
	
	public List<String> getCategories(){
		return categories;
	}
	
	public void setCategories(List<String> categories){
		this.categories = categories;
	}
	
	public String getPictureUrl(){
		return pictureUrl;
	}
	
	public void setPictureUrl(String pictureUrl){
		this.pictureUrl = pictureUrl;
	}
	
	public Person(){
		this.dbpediaUrl = null;
		this.name = null;
		this.shortBio = null;
		this.categories = null;
		this.pictureUrl = null;
	}
}