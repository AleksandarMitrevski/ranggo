package mk.finki.ranggo.aggregator.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Concept {
	
	@NotNull
	private String text;
	
	@NotNull
	private Double relevance;
	
	private String dbpediaUrl;
	
	public String getText(){
		return text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public Double getRelevance(){
		return relevance;
	}
	
	public void setRelevance(Double relevance){
		this.relevance = relevance;
	}
	
	public String getDbpediaUrl(){
		return dbpediaUrl;
	}
	
	public void setDbpediaUrl(String dbpediaUrl){
		this.dbpediaUrl = dbpediaUrl;
	}
	
	public Concept(){
		this.text = null;
		this.relevance = null;
		this.dbpediaUrl = null;
	}
}