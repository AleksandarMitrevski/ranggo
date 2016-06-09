package mk.finki.ranggo.aggregator.helper.NewYorkTimes;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Marija on 4/5/2016.
 */
public class NYTimesResult {

    @JsonProperty("response")
    private NYTimesResponse response;

    public NYTimesResult(){

    }
    public NYTimesResult(NYTimesResponse response){
        this.response=response;
    }

    public NYTimesResponse getResponse() {
        return response;
    }

    public void setResponse(NYTimesResponse response) {
        this.response = response;
    }
}
