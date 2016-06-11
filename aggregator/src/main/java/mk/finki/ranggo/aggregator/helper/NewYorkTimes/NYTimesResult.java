package mk.finki.ranggo.aggregator.helper.NewYorkTimes;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Marija on 4/5/2016.
 */
public class NYTimesResult {

	@SerializedName("response")
	
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
