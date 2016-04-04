package mk.finki.unip.ranggo.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
	private List<Rating> ratings;
	
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
	
	public List<Rating> getRatings(){
		return ratings;
	}
	
	public void setRatings(List<Rating> ratings){
		this.ratings = ratings;
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
		this.ratings = new ArrayList<Rating>();
		this.timestamp = null;
	}
}