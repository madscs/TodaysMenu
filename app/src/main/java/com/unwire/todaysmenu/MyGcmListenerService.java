package com.unwire.todaysmenu;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        final String message = data.getString("message");
        Log.v(TAG, "onMessageReceived = " + message);
    }
}