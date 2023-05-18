package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EditProfileRP implements Serializable {

    @SerializedName("status_code")
    private String status_code;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("REAL_ESTATE_APP")
    private ItemUserEdit itemUserEdits;


    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ItemUserEdit getItemUserEdits() {
        return itemUserEdits;
    }

    public void setItemUserEdits(ItemUserEdit itemUserEdits) {
        this.itemUserEdits = itemUserEdits;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public static class ItemUserEdit implements Serializable {


        @SerializedName("user_image")
        String user_image;

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }


    }

}
