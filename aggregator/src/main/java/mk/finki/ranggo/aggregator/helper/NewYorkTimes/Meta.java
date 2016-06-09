package mk.finki.ranggo.aggregator.helper.NewYorkTimes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {

    @JsonProperty("hits")
    private int hits;

    public Meta(){

    }
    public Meta(int hits){

    }
    public int getHits(){
         return this.hits;
    }
    public void  setHits(int hits){
        this.hits=hits;
    }

}
