package com.elite.item;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Detail {

    @SerializedName("post_id")
    private String post_id;

    @SerializedName("type_id")
    private String type_id;

    @SerializedName("type_name")
    private String type_name;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_image")
    private String user_image;

    @SerializedName("post_title")
    private String post_title;

    @SerializedName("post_description")
    private String post_description;

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
    private Integer price;

    @SerializedName("verified")
    private String verified;

    @SerializedName("post_image")
    private String post_image;

    @SerializedName("floor_plan_image")
    private String floor_plan_image;

    @SerializedName("total_views")
    private Integer total_views;

    @SerializedName("favourite")
    private Boolean favourite;

    @SerializedName("gallery_list")
    private List<Gallery> galleryList;

    @SerializedName("related_property")
    private List<Property> relatedProperty;

    public String getPost_id() {
        return post_id;
    }

    public String getType_id() {
        return type_id;
    }

    public String getType_name() {
        return type_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getPost_title() {
        return post_title;
    }

    public String getPost_description() {
        return post_description;
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

    public Integer getPrice() {
        return price;
    }

    public String getVerified() {
        return verified;
    }

    public String getPost_image() {
        return post_image;
    }

    public String getFloor_plan_image() {
        return floor_plan_image;
    }

    public Integer getTotal_views() {
        return total_views;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public List<Gallery> getGalleryList() {
        return galleryList;
    }

    public List<Property> getRelatedProperty() {
        return relatedProperty;
    }
}
