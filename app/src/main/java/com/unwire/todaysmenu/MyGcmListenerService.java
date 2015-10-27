package com.unwire.todaysmenu;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        final String message = data.getString("message");
    }
}