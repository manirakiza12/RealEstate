package com.elite.item;

import com.google.gson.annotations.SerializedName;

public class Property {

    @SerializedName("post_id")
    private String propertyId;

    @SerializedName("post_title")
    private String propertyTitle;

    @SerializedName("address")
    private String propertyAddress;

    @SerializedName("purpose")
    private String propertyPurpose;

    @SerializedName("price")
    private Integer propertyPrice;

    @SerializedName("post_image")
    private String propertyImage;

    @SerializedName("total_views")
    private Integer propertyViews;

    @SerializedName("favourite")
    private Boolean propertyFavorite;

    public String getPropertyId() {
        return propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public String getPropertyPurpose() {
        return propertyPurpose;
    }

    public Integer getPropertyPrice() {
        return propertyPrice;
    }

    public String getPropertyImage() {
        return propertyImage;
    }

    public Integer getPropertyViews() {
        return propertyViews;
    }

    public Boolean getPropertyFavorite() {
        return propertyFavorite;
    }


    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public void setPropertyPrice(Integer propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }

    public void setPropertyFavorite(Boolean propertyFavorite) {
        this.propertyFavorite = propertyFavorite;
    }
}
