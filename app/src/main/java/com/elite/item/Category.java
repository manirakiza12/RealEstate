package com.elite.item;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("post_id")
    private String categoryId;

    @SerializedName("post_title")
    private String categoryName;

    @SerializedName("post_image")
    private String categoryImage;

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
