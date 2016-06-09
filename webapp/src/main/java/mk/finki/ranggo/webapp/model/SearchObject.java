package mk.finki.ranggo.webapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchObject {
	private String dateFrom;
	private String dateTo;
	private String title;
	private List<String> keywords;
	private List<String> people;
	
	public SearchObject(){
		
	}

	public SearchObject(String dateFrom, String dateTo, String title, List<String> keywords, List<String> people) {
		super();
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.title = title;
		this.keywords = keywords;
		this.people = people;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getPeople() {
		return people;
	}

	public void setPeople(List<String> people) {
		this.people = people;
	}

	@Override
	public String toString() {
		return "SearchObject [dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", title=" + title + ", keywords="
				+ keywords + ", people=" + people + "]";
	}

	
	
	
}
