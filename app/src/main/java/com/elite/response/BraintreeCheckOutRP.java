package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BraintreeCheckOutRP implements Serializable {

    @SerializedName("status_code")
    private String status_code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("success")
    private String success;

    @SerializedName("REAL_ESTATE_APP")
    private ItemBrainTreeCheckOut itemBrainTreeCheckOuts;

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

    public ItemBrainTreeCheckOut getItemBrainTreeCheckOuts() {
        return itemBrainTreeCheckOuts;
    }

    public void setItemBrainTreeCheckOuts(ItemBrainTreeCheckOut itemBrainTreeCheckOuts) {
        this.itemBrainTreeCheckOuts = itemBrainTreeCheckOuts;
    }


    public static class ItemBrainTreeCheckOut implements Serializable {

        @SerializedName("paypal_payment_id")
        private String paypal_payment_id;

        public String getPaypal_payment_id() {
            return paypal_payment_id;
        }

        public void setPaypal_payment_id(String paypal_payment_id) {
            this.paypal_payment_id = paypal_payment_id;
        }
    }
}
