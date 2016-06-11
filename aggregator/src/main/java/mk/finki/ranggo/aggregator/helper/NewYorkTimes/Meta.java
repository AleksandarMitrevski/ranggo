package mk.finki.ranggo.aggregator.helper.NewYorkTimes;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {

	@SerializedName("hits")
    private int hits;
    private transient int time;
    private transient int offset;
    

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
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
    

}
