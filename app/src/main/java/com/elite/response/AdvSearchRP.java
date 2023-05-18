package com.elite.response;

import com.elite.item.Property;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AdvSearchRP implements Serializable {

    @SerializedName("REAL_ESTATE_APP")
    @Expose
    private List<Property> propertyList;

    @SerializedName("load_more")
    private boolean loadMore;

    public boolean isLoadMore() {
        return loadMore;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }
}
