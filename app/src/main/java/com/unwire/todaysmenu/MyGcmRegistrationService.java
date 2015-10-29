package com.unwire.todaysmenu;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.unwire.todaysmenu.API.MenuApiManager;
import com.unwire.todaysmenu.model.AuthenticationModel;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyGcmRegistrationService extends IntentService {

    private static final String TAG = "MyRegistrationService";
    private static final String GCM_SENDER_ID = "455208910805";
    private static final String[] TOPICS = {"global"};

    public MyGcmRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(GCM_SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                sendRegistrationToServer(token);

                subscribeTopics(token);

                TokenCache.getInstance().setToken(token);

                Log.v(TAG, "Token GCM Reg = " + token);

                Intent in = new Intent(new Intent("com.unwire.todaysmenu.REGISTER_DEVICE").putExtra("GCM_REG_ID", token));
                sendBroadcast(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // Retrofit getMenu
        MenuApiManager.getService().postToken(new TokenTask(token, "android"), new Callback<AuthenticationModel>() {
            @Override
            public void success(AuthenticationModel authenticationModel, Response response) {
                TokenCache.getInstance().setToken(String.valueOf(authenticationModel.getAuthToken()));
            }

            @Override
            public void failure(RetrofitError error) {
                error.getMessage();
            }
        });
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}