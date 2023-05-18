package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RazorPayTokenRP implements Serializable {

    @SerializedName("status_code")
    private String status_code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("success")
    private String success;

    @SerializedName("REAL_ESTATE_APP")
    private ItemPaymentCheckOut itemPaymentCheckOuts;

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }


    public ItemPaymentCheckOut getItemPaymentCheckOuts() {
        return itemPaymentCheckOuts;
    }

    public void setItemPaymentCheckOuts(ItemPaymentCheckOut itemPaymentCheckOuts) {
        this.itemPaymentCheckOuts = itemPaymentCheckOuts;
    }


    public static class ItemPaymentCheckOut implements Serializable {

        @SerializedName("order_id")
        private String razorpay_order_id;


        public String getRazorpay_order_id() {
            return razorpay_order_id;
        }

        public void setRazorpay_order_id(String razorpay_order_id) {
            this.razorpay_order_id = razorpay_order_id;
        }

    }


}
