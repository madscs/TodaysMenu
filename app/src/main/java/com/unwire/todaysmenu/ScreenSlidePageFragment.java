package com.unwire.todaysmenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
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

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener {

    public static final int VOTE_ID_LIKE = 1;
    public static final int VOTE_ID_DISLIKE = -1;

    // Text and Image Views
    private TextView sidesTextView, mainCourseTextView, testTextView, likesTextView, dislikesTextView,
            visitFacebookTextView;
    private ImageView cakeDayImageView, thumbsDownId, thumbsUpId;

    // int value to determine the current fragment
    // Should never be static (fragments won't update properly)
    int currentFragment = 0;

    int userVote = 0;

    int likes;

    int dislikes;

    // Colours for like and dislike buttons
    int greyColor = Color.parseColor("#979797");
    int whiteColor = Color.parseColor("#FFFFFF");

    // Retrofit
    String API = "https://todays-menu.herokuapp.com";
    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(API)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();
    MenuApi menu = restAdapter.create(MenuApi.class);

    Integer menuId = 0;

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

        testTextView = (TextView) view.findViewById(R.id.testTextView);
        testTextView.setText(ScreenSlidePagerActivity.convertedServingDate);

        thumbsDownId.setOnClickListener(this);
        thumbsUpId.setOnClickListener(this);

        // If menu has not arrived, else show today's menu
        if (currentFragment == -1) {
            sidesTextView.setVisibility(View.INVISIBLE);
            visitFacebookTextView.setVisibility(View.VISIBLE);
            visitFacebookTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mainCourseTextView.setText(R.string.menu_not_ready_text);
            cakeDayImageView.setVisibility(View.GONE);
            likesTextView.setVisibility(View.INVISIBLE);
            dislikesTextView.setVisibility(View.INVISIBLE);
            thumbsDownId.setVisibility(View.INVISIBLE);
            thumbsUpId.setVisibility(View.INVISIBLE);
        } else {
            // Retrofit getMenu
            menu.getMenu(setHttpBasicAuth(),
                    new Callback<List<MenuModel>>() {
                        @Override
                        public void success(List<MenuModel> menuModel, Response response) {
                            // Set current menuId
                            menuId = menuModel.get(currentFragment).getId();

                            // Set current userVote
                            userVote = menuModel.get(currentFragment).getUserVote();

                            likes = menuModel.get(currentFragment).getLikes();
                            dislikes = menuModel.get(currentFragment).getDislikes();

                            // Set like and dislike values from retrofit
                            likesTextView.setText(String.valueOf(likes));
                            dislikesTextView.setText(String.valueOf(dislikes));

                            sidesTextView.setVisibility(View.VISIBLE);
                            visitFacebookTextView.setVisibility(View.INVISIBLE);
                            likesTextView.setVisibility(View.VISIBLE);
                            dislikesTextView.setVisibility(View.VISIBLE);
                            thumbsDownId.setVisibility(View.VISIBLE);
                            thumbsUpId.setVisibility(View.VISIBLE);

                            setDislikeAndLikeColor();

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
        if (ScreenSlidePagerActivity.convertedServingDate.equals(localDate)) {
            isDateCorrect = true;
        } else {
            isDateCorrect = false;
        }
        return isDateCorrect;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumbsUpId:
                if (userVote != VOTE_ID_LIKE) {
                    likes++;
                    dislikes--;
                    userVote = VOTE_ID_LIKE;
                    onVote(VOTE_ID_LIKE);
                }
                break;
            case R.id.thumbsDownId:
                if (userVote != VOTE_ID_DISLIKE) {
                    dislikes++;
                    likes--;
                    userVote = VOTE_ID_DISLIKE;
                    onVote(VOTE_ID_DISLIKE);
                }
                break;
        }
        setDislikeAndLikeColor();
        likesTextView.setText(String.valueOf(likes));
        dislikesTextView.setText(String.valueOf(dislikes));
    }

    private void onVote(int voteId) {
        // Retrofit setVote
        menu.setVote(new VoteTask((voteId)),
                setHttpBasicAuth(),
                menuId,
                new Callback<List<MenuModel>>() {
                    @Override
                    public void success(List<MenuModel> menuModel, Response response) {
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        likesTextView.setText(error.getMessage());
                        dislikesTextView.setText(error.getMessage());
                    }
                });
    }

    public void setDislikeAndLikeColor() {
        // Determine if current menu is liked or disliked already
        if (userVote == 0) {
            thumbsUpId.setColorFilter(whiteColor);
            thumbsDownId.setColorFilter(whiteColor);
        } else if (userVote == 1) {
            thumbsUpId.setColorFilter(greyColor);
            thumbsDownId.setColorFilter(whiteColor);
        } else {
            thumbsUpId.setColorFilter(whiteColor);
            thumbsDownId.setColorFilter(greyColor);
        }
    }

    public String setHttpBasicAuth() {
        String username = "someHardcodedAndroidDeviceToken";
        String password = "0e0f90dcee78b2a8c5577ea37ecc23616515fe604ec7b3c7180e90d99aaa906d";
        String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
}