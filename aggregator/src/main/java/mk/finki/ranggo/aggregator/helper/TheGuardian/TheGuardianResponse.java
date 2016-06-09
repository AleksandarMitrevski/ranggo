package mk.finki.ranggo.aggregator.helper.TheGuardian;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheGuardianResponse {

    @JsonProperty("results")
    private ArrayList<TheGuardianWebUrl> urls;

    @JsonProperty("pages")
    private int pages;

    public TheGuardianResponse() {

    }

    public TheGuardianResponse(int pages, ArrayList<TheGuardianWebUrl> urls) {
        this.pages = pages;
        this.urls = urls;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public ArrayList<TheGuardianWebUrl> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<TheGuardianWebUrl> urls) {
        this.urls = urls;
    }

}
