package mk.finki.ranggo.aggregator.helper.NewYorkTimes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NYTimesResponse {

    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("docs")
    private ArrayList<NYTimesWebUrl> docs;

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
}
