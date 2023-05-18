package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscriptionRP implements Serializable {


    @SerializedName("status_code")
    private String status_code;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("REAL_ESTATE_APP")
    private ItemSubscription itemSubscription;

    public String getStatus_code() {
        return status_code;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public ItemSubscription getItemSubscription() {
        return itemSubscription;
    }


    public static class ItemSubscription implements Serializable {

        @SerializedName("current_plan")
        String current_plan;

        @SerializedName("expired_on")
        String expired_on;

        @SerializedName("property_limit_reached")
        String property_limit_reached;

        @SerializedName("user_total_property")
        String user_total_property;

        @SerializedName("plan_property_limit")
        String plan_property_limit;

        public String getCurrent_plan() {
            return current_plan;
        }

        public String getExpired_on() {
            return expired_on;
        }

        public String getProperty_limit_reached() {
            return property_limit_reached;
        }

        public String getUser_total_property() {
            return user_total_property;
        }

        public String getPlan_property_limit() {
            return plan_property_limit;
        }

    }
    }
