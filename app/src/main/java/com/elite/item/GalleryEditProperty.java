package com.elite.item;

import com.google.gson.annotations.SerializedName;

public class GalleryEditProperty {

    @SerializedName("gallery_img_id")
    private String gallery_img_id;

    @SerializedName("post_id")
    private String post_id;

    @SerializedName("gallery_image")
    private String gallery_image;


    public String getGallery_img_id() {
        return gallery_img_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getGallery_image() {
        return gallery_image;
    }

}
