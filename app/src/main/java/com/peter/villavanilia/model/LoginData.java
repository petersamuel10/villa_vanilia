package com.peter.villavanilia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginData {

    @SerializedName("message")
    @Expose
    String message;

    @SerializedName("jwt")
    @Expose
    String jwt;

    @SerializedName("user")
    @Expose
    User user;

    public LoginData(String message, String jwt, User user) {
        this.message = message;
        this.jwt = jwt;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
