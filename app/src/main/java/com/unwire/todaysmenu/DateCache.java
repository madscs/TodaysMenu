package com.unwire.todaysmenu;

import android.util.Log;

public class DateCache {

    private static final String TAG = "DateCache";

    private Integer date;

    private static DateCache dateCache;

    public static DateCache getInstance() {
        if (dateCache == null) {
            dateCache = new DateCache();
        }
        return dateCache;
    }

    public Integer getDate() {
        Log.v(TAG, "Date = " + date);
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
        // for persisted data, use eg. DB or SharedPreferences
    }
}
