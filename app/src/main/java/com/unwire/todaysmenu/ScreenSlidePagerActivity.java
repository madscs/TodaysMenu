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
import android.view.View;

import com.unwire.todaysmenu.API.MenuApiManager;
import com.unwire.todaysmenu.model.MenuModel;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScreenSlidePagerActivity extends FragmentActivity {
    public static final int FIRST_INDEX = 0;

    // ViewPager and PagerAdapter for swiping views
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    // Number of fragment pages / menu items and servingDate
    public static int NUM_PAGES = 0;

    public static String convertedServingDate;

    // Receiver to get intent from MyGcmRegistrationService
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MyGcmRegistrationService.class);
        startService(intent);

        registerReceiver(mReceiver, new IntentFilter("com.unwire.todaysmenu.REGISTER_DEVICE"));

        MenuApiManager.getService().getMenu(new Callback<List<MenuModel>>() {
            @Override
            public void success(List<MenuModel> menuModels, Response response) {

                // Get the size of the menuModels list to tell PagerAdapter
                // how many pages we're gonna show
                NUM_PAGES = menuModels.size();

                // Set date in DateCache
                DateCache.getInstance().setDate(menuModels.get(FIRST_INDEX).getServingDate());

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
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
    }

    // Save instance state if user opens a link
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Restore instance state if user returns from a link
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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