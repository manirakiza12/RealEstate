package com.elite.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginRP implements Serializable {


    @SerializedName("status_code")
    private String status_code;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("REAL_ESTATE_APP")
    private ItemUser itemUserList;

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

    public ItemUser getItemUserList() {
        return itemUserList;
    }

    public void setItemUserList(ItemUser itemUserList) {
        this.itemUserList = itemUserList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public static class ItemUser implements Serializable {

        @SerializedName("user_id")
        String user_id;

        @SerializedName("name")
        String name;

        @SerializedName("email")
        String email;

        @SerializedName("phone")
        String phone;

        @SerializedName("user_image")
        String user_image;


        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }


    }
    }
