package mk.finki.ranggo.webapp.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Taxonomy {
	
	//labels contains the hierarchy in the given order: "/" + labels.get(0) + "/" + labels.get(1) + "/" + ...  
	@NotNull
	private List<String> labels;
	
	@NotNull
	private Double score;
		
	public List<String> getLabels(){
		return labels;
	}
	
	public void setLabels(List<String> labels){
		this.labels = labels;
	}
	
	public Double getScore(){
		return score;
	}
	
	public void setScore(Double score){
		this.score = score;
	}
	
	public Taxonomy(){
		this.labels = null;
		this.score = null;
	}
}