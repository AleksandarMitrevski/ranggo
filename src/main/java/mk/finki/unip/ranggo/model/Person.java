package mk.finki.unip.ranggo.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "persons")
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
		this.categories = new ArrayList<String>();
		this.pictureUrl = null;
	}
}