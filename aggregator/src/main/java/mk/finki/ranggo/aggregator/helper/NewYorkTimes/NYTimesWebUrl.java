package mk.finki.ranggo.aggregator.helper.NewYorkTimes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NYTimesWebUrl {

    @JsonProperty("web_url")
    private String web_url;
    @JsonProperty("lead_paragraph")
    private String title;
    @JsonProperty("pub_date")
    private String pub_date;
    
    public NYTimesWebUrl(){

    }
    public NYTimesWebUrl(String web_url,String title) {

        this.web_url = web_url;
        this.title=title;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPub_date() {
		return pub_date;
	}
	public void setPub_date(String pub_date) {
		this.pub_date = pub_date;
	}
	
    
}