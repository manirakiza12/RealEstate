package com.elite.item;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EditProperty {

    @SerializedName("post_id")
    private String post_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("type_id")
    private String type_id;

    @SerializedName("type_name")
    private String type_name;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("bedrooms")
    private String bedrooms;

    @SerializedName("bathrooms")
    private String bathrooms;

    @SerializedName("area")
    private String area;

    @SerializedName("furnishing")
    private String furnishing;

    @SerializedName("amenities")
    private String amenities;

    @SerializedName("price")
    private String price;

    @SerializedName("image")
    private String image;

    @SerializedName("verified")
    private String verified;

    @SerializedName("floor_plan_image")
    private String floor_plan_image;

    @SerializedName("gallery_images")
    private ArrayList<UploadImage> galleryEditProperties;

    public String getPost_id() {
        return post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getType_id() {
        return type_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public String getBathrooms() {
        return bathrooms;
    }

    public String getArea() {
        return area;
    }

    public String getFurnishing() {
        return furnishing;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getFloor_plan_image() {
        return floor_plan_image;
    }

    public ArrayList<UploadImage> getGalleryEditProperties() {
        return galleryEditProperties;
    }


    public String getVerified() {
        return verified;
    }

    public String getType_name() {
        return type_name;
    }
}
