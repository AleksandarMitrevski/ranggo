package mk.finki.ranggo.aggregator.helper.TheGuardian;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheGuardianWebUrl {

    @SerializedName("webUrl")
    private String webUrl;
    @SerializedName("webTitle")
    private String webTitle;
    @SerializedName("webPublicationDate")
    private String webPublicationDate;
    private transient String type;
    private transient String sectionId;
    private transient String apiUrl;
    private transient String sectionName;
    
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	
	
    
}