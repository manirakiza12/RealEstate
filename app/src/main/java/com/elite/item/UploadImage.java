package com.elite.item;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class UploadImage {

    @SerializedName("gallery_image")
    public String imageUrl = "";

    @SerializedName("gallery_img_id")
    public String gallery_img_id = "";

    public File imageFile;

    public UploadImage() {

    }

    public UploadImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UploadImage(File imageFile) {
        this.imageFile = imageFile;

    }
    public String getGallery_img_id() {
        return gallery_img_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}
