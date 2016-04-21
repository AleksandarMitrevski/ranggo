package mk.finki.unip.ranggo.model;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonEntity {
	
	@DBRef
	@NotNull
	private Person person;
	
	@NotNull
	private Double relevance;
	
	@NotNull
	private Double score;
	
	//only set if true
	private Boolean mixed;
	
	public Person getPerson(){
		return person;
	}
	
	public void setPerson(Person person){
		this.person = person;
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
	
	public PersonEntity(){
		this.person = null;
		this.relevance = null;
		this.score = null;
		this.mixed = null;
	}
}