package mk.finki.unip.ranggo.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Keyword {
	
	@NotNull
	private String text;
	
	@NotNull
	private Double relevance;
	
	@NotNull
	private Double score;
	
	//only set if true
	private Boolean mixed;
	
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
	
	public Double getScore(){
		return score;
	}
	
	public void setScore(Double score){
		this.score = score;
	}
	
	public Boolean getMixed(){
		return mixed;
	}
	
	public void setMixed(Boolean mixed){
		this.mixed = mixed;
	}
	
	public Keyword(){
		this.text = null;
		this.relevance = null;
		this.score = null;
		this.mixed = null;
	}
}