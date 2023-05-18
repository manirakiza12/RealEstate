package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FavoriteRP implements Serializable {

    @SerializedName("status_code")
    private String status_code;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    public String getStatus_code() {
        return status_code;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

}
