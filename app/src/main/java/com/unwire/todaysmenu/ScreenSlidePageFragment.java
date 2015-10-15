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

    // DeviceId
    private String deviceId;

    // int value to determine the current fragment
    int currentFragment;

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
        currentFragment = getArguments() != null ? getArguments().getInt("num") : 1;
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
        testTextView.setText(String.valueOf("Fragment #" + currentFragment));

        // Retrofit getFirstMenuItem
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

                // Set main course and sides text for menu of the day
                mainCourseTextView.setText(menuModel.get(currentFragment).getMainCourse());
                sidesTextView.setText(menuModel.get(currentFragment).getSides());
            }

            @Override
            public void failure(RetrofitError error) {
                error.getMessage();
            }
        });

        getDeviceId();
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

}