package com.elite.response;

import com.elite.item.Detail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailRP implements Serializable {

    @SerializedName("REAL_ESTATE_APP")
    @Expose
    private Detail detailList;
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;

    public Integer getStatusCode() {
        return statusCode;
    }

    public Detail getDetailList() {
        return detailList;
    }
}
