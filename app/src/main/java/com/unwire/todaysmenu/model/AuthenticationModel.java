package com.unwire.todaysmenu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationModel {

    @SerializedName("auth_token")
    @Expose
    private String authToken;

    /**
     * @return The authentication
     */
    public String getAuthToken() {
        return authToken;
    }
}