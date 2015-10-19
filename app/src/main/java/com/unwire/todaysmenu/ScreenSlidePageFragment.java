package com.unwire.todaysmenu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unwire.todaysmenu.API.MenuApi;
import com.unwire.todaysmenu.model.MenuModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScreenSlidePageFragment extends Fragment {

    // Liking states (to be replaced after backend update)
    // From here -->
    public static final int NON_VOTED_STATE = 0;
    public static final int LIKED_STATE = 1;
    public static final int DISLIKED_STATE = 2;
    public int state = 0;
    // <-- To here

    // Text and Image Views
    private TextView sidesTextView, mainCourseTextView, testTextView, likesTextView, dislikesTextView,
            visitFacebookTextView;
    private ImageView cakeDayImageView, thumbsDownId, thumbsUpId, forkAndKnifeImageView;

    // String values
    private String deviceId;

    // int value to determine the current fragment
    // Should never be static (fragments won't update properly)
    int currentFragment = 0;

    // Colours for like and dislike buttons
    int greyColor = Color.parseColor("#979797");
    int whiteColor = Color.parseColor("#FFFFFF");

    // Retrofit
    String API = "https://unwire-menu.herokuapp.com";
    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(API)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();
    MenuApi menu = restAdapter.create(MenuApi.class);

    // Method to get the current fragment
    static ScreenSlidePageFragment newInstance(int num) {
        ScreenSlidePageFragment f = new ScreenSlidePageFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set currentFragment according to whether or not the date is correct.
        if (isLocalDateCorrect()) {
            currentFragment = getArguments() != null ? getArguments().getInt("num") : 1;
        } else {
            currentFragment = (getArguments() != null ? getArguments().getInt("num") : 1) - 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout for this fragment
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Assign TextViews
        sidesTextView = (TextView) view.findViewById(R.id.sidesText);
        mainCourseTextView = (TextView) view.findViewById(R.id.mainCourseText);
        testTextView = (TextView) view.findViewById(R.id.testTextView);
        likesTextView = (TextView) view.findViewById(R.id.likesText);
        dislikesTextView = (TextView) view.findViewById(R.id.dislikesText);
        visitFacebookTextView = (TextView) view.findViewById(R.id.visitFacebookText);

        // Assign ImageViews
        cakeDayImageView = (ImageView) view.findViewById(R.id.cakeDayId);
        thumbsDownId = (ImageView) view.findViewById(R.id.thumbsDownId);
        thumbsUpId = (ImageView) view.findViewById(R.id.thumbsUpId);
        forkAndKnifeImageView = (ImageView) view.findViewById(R.id.forkAndKnifeId);

        testTextView = (TextView) view.findViewById(R.id.testTextView);
        testTextView.setText(ScreenSlidePagerActivity.convertedServingDate
                + String.valueOf(" Fragment #" + currentFragment));

        // If menu has not arrived, else show today's menu
        if (currentFragment == -1) {
            sidesTextView.setVisibility(View.INVISIBLE);
            visitFacebookTextView.setVisibility(View.VISIBLE);
            mainCourseTextView.setText(R.string.menu_not_ready_text);
            cakeDayImageView.setVisibility(View.GONE);
            likesTextView.setVisibility(View.INVISIBLE);
            dislikesTextView.setVisibility(View.INVISIBLE);
            thumbsDownId.setVisibility(View.INVISIBLE);
            thumbsUpId.setVisibility(View.INVISIBLE);
        } else {
            // Retrofit getMenu
            menu.getMenu(new Callback<List<MenuModel>>() {
                @Override
                public void success(List<MenuModel> menuModel, Response response) {
                    // Set like and dislike values from retrofit
                    likesTextView.setText(String.valueOf(menuModel
                            .get(currentFragment)
                            .getLikes()));
                    dislikesTextView.setText(String.valueOf(menuModel
                            .get(currentFragment)
                            .getDislikes()));

                    sidesTextView.setVisibility(View.VISIBLE);
                    visitFacebookTextView.setVisibility(View.INVISIBLE);
                    likesTextView.setVisibility(View.VISIBLE);
                    dislikesTextView.setVisibility(View.VISIBLE);
                    thumbsDownId.setVisibility(View.VISIBLE);
                    thumbsUpId.setVisibility(View.VISIBLE);

                    // Set main course and sides text for menu of the day
                    sidesTextView.setText(menuModel.get(currentFragment).getSides());
                    mainCourseTextView.setText(menuModel.get(currentFragment).getMainCourse());

                    // Should cake day sign be shown or not
                    if (menuModel.get(currentFragment).getCake()) {
                        cakeDayImageView.setVisibility(View.VISIBLE);
                    } else {
                        cakeDayImageView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sidesTextView.setText(error.getMessage());
                    mainCourseTextView.setText(error.getMessage());
                }
            });
        }

        getDeviceId();
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

        // Changes the date of the month for testing purposes
        // localDate = localDate.replaceAll("16", "17");

        String s = ScreenSlidePagerActivity.convertedServingDate;

        // Checks if the date from retrofit is equal to the local date
        if (s.equals(localDate)) {
            isDateCorrect = true;
        } else {
            isDateCorrect = false;
        }
        return isDateCorrect;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumbsUpId:
                onThumbsUp();
                break;
            case R.id.thumbsDownId:
                onThumbsDown();
                break;
        }
    }

    private void getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getView().getContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getView().getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        deviceId = deviceUuid.toString();
    }

    private void onThumbsUp() {
        menu.setVote(new LikeTask(deviceId), "like", new Callback<List<MenuModel>>() {
            @Override
            public void success(List<MenuModel> menuModel, Response response) {
                // int state should be replaced when backend is implemented
                state = LIKED_STATE;
                setDislikeAndLikeColor();
            }

            @Override
            public void failure(RetrofitError error) {
                likesTextView.setText(error.getMessage());
            }
        });
    }

    private void onThumbsDown() {
        menu.setVote(new LikeTask((deviceId)), "dislike", new Callback<List<MenuModel>>() {
            @Override
            public void success(List<MenuModel> menuModel, Response response) {
                // int state should be replaced when backend is implemented
                state = DISLIKED_STATE;
                setDislikeAndLikeColor();
            }

            @Override
            public void failure(RetrofitError error) {
                dislikesTextView.setText(error.getMessage());
            }
        });
    }

    public void setDislikeAndLikeColor() {
        if (state == NON_VOTED_STATE) {
            thumbsUpId.setColorFilter(whiteColor);
            thumbsDownId.setColorFilter(whiteColor);
        } else if (state == LIKED_STATE) {
            thumbsUpId.setColorFilter(greyColor);
            thumbsDownId.setColorFilter(whiteColor);
        } else {
            thumbsUpId.setColorFilter(whiteColor);
            thumbsDownId.setColorFilter(greyColor);
        }
    }
}