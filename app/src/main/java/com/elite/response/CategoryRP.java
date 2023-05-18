package com.elite.response;

import com.elite.item.Category;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryRP implements Serializable {

    @SerializedName("REAL_ESTATE_APP")
    @Expose
    private List<Category> categoryList;

    @SerializedName("load_more")
    private boolean loadMore;

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public boolean isLoadMore() {
        return loadMore;
    }
}
