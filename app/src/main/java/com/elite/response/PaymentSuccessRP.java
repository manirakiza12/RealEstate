package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaymentSuccessRP implements Serializable {

    @SerializedName("status_code")
    private String status_code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("success")
    private String success;


    public String getStatus_code() {
        return status_code;
    }

    public String getMsg() {
        return msg;
    }

    public String getSuccess() {
        return success;
    }



}
