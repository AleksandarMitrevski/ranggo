package mk.finki.unip.ranggo.model;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class Rating {
	
	@DBRef
	@NotNull
	private Person person;
	
	@NotNull
	private Double score;
	
	@NotNull
	private Boolean mixed;
	
	public Person getPerson(){
		return person;
	}
	
	public void setPerson(Person person){
		this.person = person;
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
	
	public Rating(){
		this.person = null;
		this.score = null;
		this.mixed = null;
	}
}