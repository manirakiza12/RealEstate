package com.elite.item;

import com.google.gson.annotations.SerializedName;

public class Gallery {

    @SerializedName("gallery_id")
    private String gallery_id;

    @SerializedName("gallery_image")
    private String gallery_image;


    public String getGallery_id() {
        return gallery_id;
    }

    public String getGallery_image() {
        return gallery_image;
    }
}
