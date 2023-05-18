package com.elite.response;

import com.elite.item.Property;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LatestRP implements Serializable {

    @SerializedName("REAL_ESTATE_APP")
    @Expose
    private List<Property> latestPropertyList;

    public List<Property> getLatestPropertyList() {
        return latestPropertyList;
    }

    @SerializedName("load_more")
    public boolean loadMore=false;

    public boolean isLoadMore() {
        return loadMore;
    }
}
