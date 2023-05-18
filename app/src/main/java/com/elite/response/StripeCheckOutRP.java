package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StripeCheckOutRP implements Serializable {

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

        @SerializedName("id")
        private String stripe_id;

        @SerializedName("stripe_payment_token")
        private String stripe_payment_token;

        @SerializedName("ephemeralKey")
        private String stripe_ephemeralKey;

        @SerializedName("customer")
        private String stripe_customer;

        public String getStripe_id() {
            return stripe_id;
        }

        public void setStripe_id(String stripe_id) {
            this.stripe_id = stripe_id;
        }

        public String getStripe_payment_token() {
            return stripe_payment_token;
        }

        public void setStripe_payment_token(String stripe_payment_token) {
            this.stripe_payment_token = stripe_payment_token;
        }

        public String getStripe_ephemeralKey() {
            return stripe_ephemeralKey;
        }

        public void setStripe_ephemeralKey(String stripe_ephemeralKey) {
            this.stripe_ephemeralKey = stripe_ephemeralKey;
        }

        public String getStripe_customer() {
            return stripe_customer;
        }

        public void setStripe_customer(String stripe_customer) {
            this.stripe_customer = stripe_customer;
        }

    }


}
