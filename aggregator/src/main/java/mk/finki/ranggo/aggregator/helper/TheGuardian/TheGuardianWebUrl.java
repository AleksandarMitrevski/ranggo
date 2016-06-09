package mk.finki.ranggo.aggregator.helper.TheGuardian;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheGuardianWebUrl {

    @JsonProperty("webUrl")
    private String webUrl;
    @JsonProperty("webTitle")
    private String webTitle;
    @JsonProperty("webPublicationDate")
    private String webPublicationDate;

    public TheGuardianWebUrl(){

    }
    
    public TheGuardianWebUrl(String webUrl, String webTitle, String webPublicationDate) {
		super();
		this.webUrl = webUrl;
		this.webTitle = webTitle;
		this.webPublicationDate = webPublicationDate;
	}

	public  TheGuardianWebUrl(String webUrl){
        this.webUrl=webUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String toString() {
        return webUrl;
    }
	public String getWebTitle() {
		return webTitle;
	}
	public void setWebTitle(String webTitle) {
		this.webTitle = webTitle;
	}
	public String getWebPublicationDate() {
		return webPublicationDate;
	}
	public void setWebPublicationDate(String webPublicationDate) {
		this.webPublicationDate = webPublicationDate;
	}
    
}