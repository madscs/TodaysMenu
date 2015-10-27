package com.unwire.todaysmenu;

import android.util.Log;

public class TokenCache {

    private static final String TAG = "TokenCache";

    private String token;

    private static TokenCache tokenCache;

    public static TokenCache getInstance() {
        if (tokenCache == null) {
            tokenCache = new TokenCache();
        }
        return tokenCache;
    }

    public String getToken() {
        Log.v(TAG, "Token = " + token);
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        // for persisted data, use eg. DB or SharedPreferences
    }
}