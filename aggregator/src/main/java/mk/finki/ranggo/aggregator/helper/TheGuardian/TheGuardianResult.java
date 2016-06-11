package mk.finki.ranggo.aggregator.helper.TheGuardian;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheGuardianResult {

    @SerializedName("response")
    private TheGuardianResponse response;

    public TheGuardianResult() {
    }

    public TheGuardianResult(TheGuardianResponse response) {
        this.response = response;
    }

    public TheGuardianResponse getResponse() {
        return response;
    }

    public void setResponse(TheGuardianResponse response) {
        this.response = response;
    }
}
