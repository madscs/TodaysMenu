package com.unwire.todaysmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
    String API = "https://unwire-menu.herokuapp.com";
    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(API)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();
    MenuApi menu = restAdapter.create(MenuApi.class);

    public static int NUM_PAGES = 0;

    public static String updatedAtDate;

    // ViewPager and PagerAdapter for swiping views
    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Retrofit getFirstMenuItem
        menu.getMenu(new Callback<List<MenuModel>>() {
            @Override
            public void success(List<MenuModel> menuModel, Response response) {
                // Get the highest ID to tell PagerAdapter how many pages we're gonna show

                NUM_PAGES = menuModel.size();

                // Get the date from retrofit
                updatedAtDate = menuModel.get(0).getUpdatedAt();

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
}