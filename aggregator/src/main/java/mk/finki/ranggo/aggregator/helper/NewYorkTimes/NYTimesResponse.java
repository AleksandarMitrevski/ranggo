package mk.finki.ranggo.aggregator.helper.NewYorkTimes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NYTimesResponse {

	@SerializedName("meta")
    private Meta meta;
	@SerializedName("docs")
    private ArrayList<NYTimesWebUrl> docs;
    private transient String status;
    private transient String copyright;

    public NYTimesResponse() {

    }

    public NYTimesResponse(Meta meta, ArrayList<NYTimesWebUrl> docs) {
        this.meta = meta;
        this.docs = docs;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ArrayList<NYTimesWebUrl> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<NYTimesWebUrl> docs) {
        this.docs = docs;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
    
}
