package com.elite.response;

import com.elite.item.Category;
import com.elite.item.Property;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeRP implements Serializable {

    @SerializedName("REAL_ESTATE_APP")
    @Expose
    private RealEstate realEstate;
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;

    public RealEstate getRealEstate() {
        return realEstate;
    }

    public Integer getStatusCode() {
        return statusCode;
    }


    public static class RealEstate implements Serializable {

        @SerializedName("types_list")
        private List<Category> homeCatList;

        @SerializedName("latest_property")
        private List<Property> homeLatestList;

        @SerializedName("trending_property")
        private List<Property> homePopularList;

        public List<Category> getHomeCatList() {
            return homeCatList;
        }

        public List<Property> getHomeLatestList() {
            return homeLatestList;
        }

        public List<Property> getHomePopularList() {
            return homePopularList;
        }
    }
 }
