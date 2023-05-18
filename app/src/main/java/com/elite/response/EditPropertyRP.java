package com.elite.response;

import com.elite.item.EditProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EditPropertyRP implements Serializable {

    @SerializedName("REAL_ESTATE_APP")
    @Expose
    private EditProperty editProperty;
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;


    public EditProperty getEditProperty() {
        return editProperty;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
