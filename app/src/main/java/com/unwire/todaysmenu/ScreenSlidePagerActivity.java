package com.unwire.todaysmenu;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.unwire.todaysmenu.API.MenuApiManager;
import com.unwire.todaysmenu.model.MenuModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScreenSlidePagerActivity extends FragmentActivity {
    private static final String TAG = "ScreenSlidePagerActivity";
    public static final int FIRST_INDEX = 0;

    // ViewPager and PagerAdapter for swiping views
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    // Number of fragment pages / menu items and servingDate
    public static int NUM_PAGES = 0, servingDate;

    public static String convertedServingDate;

        private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Post registration id to server

            // Replace hardcoded token with TokenCache.getInstance().getToken() later
            // Retrofit getMenu
//            MenuApiManager.getService().postToken(new TokenTask(TokenCache.getInstance().getToken(), "android"), new Callback<AuthenticationModel>() {
//                @SuppressLint("LongLogTag")
//                @Override
//                public void success(AuthenticationModel authenticationModel, Response response) {
//                    Log.v(TAG, "Token onReceive 1 = " + String.valueOf(authenticationModel.getAuthToken()));
//                    TokenCache.getInstance().setToken(String.valueOf(authenticationModel.getAuthToken()));
//                    Log.v(TAG, "Token onReceive 2 = " + String.valueOf(authenticationModel.getAuthToken()));
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    error.getMessage();
//                }
//            });


        }
    };

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        Log.v(TAG, "Token onCreate = " + TokenCache.getInstance().getToken());

        Intent intent = new Intent(this, MyGcmRegistrationService.class);
        startService(intent);

        registerReceiver(mReceiver, new IntentFilter("com.unwire.todaysmenu.REGISTER_DEVICE"));

        setHttpBasicAuth();

//        MenuApiManager.getService().postToken(new TokenTask(TokenCache.getInstance().getToken(), "android"), new Callback<AuthenticationModel>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void success(AuthenticationModel authenticationModel, Response response) {
//                Log.v(TAG, "Token post 1 = " + String.valueOf(authenticationModel.getAuthToken()));
//                TokenCache.getInstance().setToken(authenticationModel.getAuthToken());
//                Log.v(TAG, "Token post 2 = " + String.valueOf(authenticationModel.getAuthToken()));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                error.getMessage();
//            }
//        });

//        Log.v(TAG, "Token onCreate = " + TokenCache.getInstance().getToken());

        MenuApiManager.getService().getMenu(setHttpBasicAuth(), new Callback<List<MenuModel>>() {
            @SuppressLint("LongLogTag")
            @Override
            public void success(List<MenuModel> menuModels, Response response) {
                // Get the size of the list to tell PagerAdapter how many pages we're gonna show
                NUM_PAGES = menuModels.size();

                // Get the date from retrofit
//                servingDate = menuModels.get(FIRST_INDEX).getServingDate();
//
//                convertedServingDate = String.valueOf(new java.util.Date((long) servingDate * 1000));
//
//                // Remove last 19 chars of String to get a simpler date
//                convertedServingDate = convertedServingDate.substring(0, convertedServingDate.length() - 19);
//
//                Log.v(TAG, "MenuModels convertedServingDate = " + convertedServingDate);
//
                DateCache.getInstance().setDate(menuModels.get(0).getServingDate());

                // Notify PagerAdapter that we're changing the value of NUM_PAGES
                mPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                error.getMessage();
            }
        });

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isLocalDateCorrect() {
        boolean isDateCorrect;

        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("GMT+1");

        // Set format of date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
        Calendar cal = Calendar.getInstance(tz);
        String localDate = dateFormat.format(cal.getTime());

        // Format date to be with '-' instead of '/'
        localDate = localDate.replaceAll("/", "-");

        // Checks if the date from retrofit is equal to the local date
        if (DateCache.getInstance().getDate().equals(localDate)) {
            isDateCorrect = true;
        } else {
            isDateCorrect = false;
        }
        return isDateCorrect;
    }

    public void onClick(View view) {
        new ScreenSlidePageFragment().onClick(view);
    }

    // Method for handling back button being pressed
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Get the Fragment at the specified position
        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public String setHttpBasicAuth() {
        // Outcommented code for later as it's hardcoded for now
        String username = TokenCache.getInstance().getToken();
//        String username = "someHardcodedAndroidDeviceToken";
        String password = "0e0f90dcee78b2a8c5577ea37ecc23616515fe604ec7b3c7180e90d99aaa906d";
        String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
}