package com.unwire.todaysmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.View;

import com.unwire.todaysmenu.API.MenuApi;
import com.unwire.todaysmenu.model.MenuModel;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScreenSlidePagerActivity extends FragmentActivity {

    // Retrofit
    String API = "https://todays-menu.herokuapp.com";
    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(API)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();
    MenuApi menu = restAdapter.create(MenuApi.class);

    public static int NUM_PAGES = 0, servingDate;

    public static String convertedServingDate;

    // ViewPager and PagerAdapter for swiping views
    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Retrofit getMenu
        menu.getMenu(setHttpBasicAuth(),
                new Callback<List<MenuModel>>() {
            @Override
            public void success(List<MenuModel> menuModel, Response response) {
                // Get the highest ID to tell PagerAdapter how many pages we're gonna show

                NUM_PAGES = menuModel.size();

                // Get the date from retrofit
                servingDate = menuModel.get(0).getServingDate();

                convertedServingDate = String.valueOf(new java.util.Date((long) servingDate * 1000));

                // Remove last 19 chars of String to get a simpler date
                convertedServingDate = convertedServingDate.substring(0, convertedServingDate.length() - 19);

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
        String username = "someHardcodedAndroidDeviceToken";
        String password = "0e0f90dcee78b2a8c5577ea37ecc23616515fe604ec7b3c7180e90d99aaa906d";
        String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
}