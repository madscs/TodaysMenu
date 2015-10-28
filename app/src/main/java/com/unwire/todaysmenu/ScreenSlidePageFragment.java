package com.unwire.todaysmenu;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unwire.todaysmenu.API.MenuApiManager;
import com.unwire.todaysmenu.model.MenuModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScreenSlidePageFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ScreenSlidePageFragment";

    // Constants for liking and disliking
    public static final int VOTE_ID_LIKE = 1;
    public static final int VOTE_ID_DISLIKE = -1;
    int USER_VOTE = 0, LIKES, DISLIKES;

    // Text and Image Views
    private TextView sidesTextView, mainCourseTextView, testTextView, likesTextView, dislikesTextView,
            visitFacebookTextView;
    private ImageView cakeDayImageView, thumbsDownId, thumbsUpId;

    // int value to determine the current fragment
    // Should never be static (fragments won't update properly)
    int CURRENT_FRAGMENT = 0;

    // Colours for like and dislike buttons
    int GREY_COLOR = Color.parseColor("#979797");
    int WHITE_COLOR = Color.parseColor("#FFFFFF");

    // Boolean for checking date
    boolean isDateCorrect;

    Integer menuId = 0;
    private Integer servingDate;
    private String convertedServingDate;
    private String backendDate;
    private SimpleDateFormat dateFormat;

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
        // Set current fragment according to whether or not the date is correct.

        if(isLocalDateCorrect()){
            CURRENT_FRAGMENT = getArguments() != null ? getArguments().getInt("num") : 1;
        } else {
            CURRENT_FRAGMENT = (getArguments() != null ? getArguments().getInt("num") : 1) - 1;
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
        assignViews(view);

        thumbsDownId.setOnClickListener(this);
        thumbsUpId.setOnClickListener(this);

        showMenu();
    }

    private void showMenu() {
        // If menu has not arrived, else show today's menu
        MenuApiManager.getService().getMenu(setHttpBasicAuth(), new Callback<List<MenuModel>>() {
            @SuppressLint("LongLogTag")
            @Override
            public void success(List<MenuModel> menuModels, Response response) {

//                isLocalDateCorrect(menuModels);

//                    testTextView.setText(DateCache.getInstance().getDate());

                if (CURRENT_FRAGMENT == -1) {
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
                    // Set current userVote
                    USER_VOTE = menuModels.get(CURRENT_FRAGMENT).getUserVote();

                    LIKES = menuModels.get(CURRENT_FRAGMENT).getLikes();
                    DISLIKES = menuModels.get(CURRENT_FRAGMENT).getDislikes();

                    // Set like and dislike values from retrofit
                    likesTextView.setText(String.valueOf(LIKES));
                    dislikesTextView.setText(String.valueOf(DISLIKES));

                    sidesTextView.setVisibility(View.VISIBLE);
                    visitFacebookTextView.setVisibility(View.INVISIBLE);
                    likesTextView.setVisibility(View.VISIBLE);
                    dislikesTextView.setVisibility(View.VISIBLE);
                    thumbsDownId.setVisibility(View.VISIBLE);
                    thumbsUpId.setVisibility(View.VISIBLE);

                    testTextView.setText(dateFormat.format((long) menuModels.get(CURRENT_FRAGMENT).getServingDate() * 1000));

                    setDislikeAndLikeColor();

                    // Set main course and sides text for menu of the day
                    sidesTextView.setText(menuModels.get(CURRENT_FRAGMENT).getSides());
                    mainCourseTextView.setText(menuModels.get(CURRENT_FRAGMENT).getMainCourse());

                    // Should cake day sign be shown or not
                    if (menuModels.get(CURRENT_FRAGMENT).getCake()) {
                        cakeDayImageView.setVisibility(View.VISIBLE);
                    } else {
                        cakeDayImageView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.getMessage();
                sidesTextView.setText(error.getMessage());
                mainCourseTextView.setText(error.getMessage());
            }
        });
    }

    private void assignViews(View view) {
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
    }

    private boolean isLocalDateCorrect() {
        // Set format of date
        dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Calendar cal = Calendar.getInstance();
        String localDate = dateFormat.format(cal.getTime());

        backendDate = dateFormat.format((long) DateCache.getInstance().getDate() * 1000);

        Log.v(TAG, "MenuModels unix backend = " + backendDate);
        Log.v(TAG, "MenuModels unix now = " + localDate);
        Log.v(TAG, "MenuModels unix timestamp = " + DateCache.getInstance().getDate());

        if (localDate.equals(backendDate)) {
            isDateCorrect = true;
        } else {
            isDateCorrect = false;
        }

        Log.v(TAG, "MenuModels unix boolean = " + isDateCorrect);
        return isDateCorrect;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumbsUpId:
                if (USER_VOTE != VOTE_ID_LIKE) {
                    LIKES++;
                    if (USER_VOTE != 0) {
                        DISLIKES--;
                    }
                    USER_VOTE = VOTE_ID_LIKE;
                    onVote(VOTE_ID_LIKE);
                }
                break;
            case R.id.thumbsDownId:
                if (USER_VOTE != VOTE_ID_DISLIKE) {
                    DISLIKES++;
                    if (USER_VOTE != 0) {
                        LIKES--;
                    }
                    USER_VOTE = VOTE_ID_DISLIKE;
                    onVote(VOTE_ID_DISLIKE);
                }
                break;
        }
        setDislikeAndLikeColor();
        likesTextView.setText(String.valueOf(LIKES));
        dislikesTextView.setText(String.valueOf(DISLIKES));
    }

    private void onVote(int voteId) {
        // Retrofit setVote
        MenuApiManager.getService().setVote(setHttpBasicAuth(), new VoteTask(voteId), menuId,
                new Callback<List<MenuModel>>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void success(List<MenuModel> menuModels, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.getMessage();
                        likesTextView.setText(error.getMessage());
                        dislikesTextView.setText(error.getMessage());
                    }
                });
    }

    public void setDislikeAndLikeColor() {
        // Determine if current menu is liked or disliked already
        if (USER_VOTE == 0) {
            thumbsUpId.setColorFilter(WHITE_COLOR);
            thumbsDownId.setColorFilter(WHITE_COLOR);
        } else if (USER_VOTE == 1) {
            thumbsUpId.setColorFilter(GREY_COLOR);
            thumbsDownId.setColorFilter(WHITE_COLOR);
        } else {
            thumbsUpId.setColorFilter(WHITE_COLOR);
            thumbsDownId.setColorFilter(GREY_COLOR);
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