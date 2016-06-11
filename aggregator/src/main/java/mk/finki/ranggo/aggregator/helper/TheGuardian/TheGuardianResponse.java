package mk.finki.ranggo.aggregator.helper.TheGuardian;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Marija on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheGuardianResponse {	
	
	@SerializedName("results")
    private ArrayList<TheGuardianWebUrl> urls;
    private transient String status;
    private transient String userTier;
    private transient int total;
    private transient int startIndex;
    private transient int pageSize;
    private transient int currentPage;
    private transient String orderBy;
    @SerializedName("pages")
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserTier() {
		return userTier;
	}

	public void setUserTier(String userTier) {
		this.userTier = userTier;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
    

}
