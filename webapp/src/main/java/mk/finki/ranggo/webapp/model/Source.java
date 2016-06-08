package mk.finki.ranggo.webapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Source {
	
	private String country;
	private String name;
	private String url;
	
	public Source(){
		
	}

	public Source(String country, String name, String url) {
		super();
		this.country = country;
		this.name = name;
		this.url = url;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
