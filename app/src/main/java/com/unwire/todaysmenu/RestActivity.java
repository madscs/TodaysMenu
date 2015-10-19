//package com.unwire.todaysmenu;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.text.method.LinkMovementMethod;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.unwire.todaysmenu.API.MenuApi;
//import com.unwire.todaysmenu.model.MenuModel;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import java.util.List;
//import java.util.UUID;
//
//import retrofit.Callback;
//import retrofit.RestAdapter;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//
//public class RestActivity extends Activity {
//
//    public static final int NON_VOTED_STATE = 0;
//    public static final int LIKED_STATE = 1;
//    public static final int DISLIKED_STATE = 2;
//    // int state should be replaced when backend is implemented
//    public int state = 0;
//    ArrayList<Opinion> opinions = new ArrayList<>();
//
//    public static int menuDayChooser = 0;
//    public boolean isTodaysMenuAvailable = true;
//    TextView sidesTextView, mainCourseTextView, testTextView, likesTextView, dislikesTextView,
//            visitFacebookTextView;
//    ImageView cakeDayImageView, thumbsDownId, thumbsUpId, forkAndKnifeImageView;
//
//    int greyColor = Color.parseColor("#979797");
//    int whiteColor = Color.parseColor("#FFFFFF");
//    private float x1, x2;
//    static final int MIN_DISTANCE = 150;
//    private String updatedAtDate, deviceId;
//
//    // Retrofit
//    String API = "https://unwire-menu.herokuapp.com";
//    RestAdapter restAdapter = new RestAdapter.Builder()
//            .setEndpoint(API)
//            .setLogLevel(RestAdapter.LogLevel.FULL)
//            .build();
//    MenuApi menu = restAdapter.create(MenuApi.class);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        setupTextViews();
//
//        setupImageViews();
//
//        getDeviceId();
//
//        updateMenu();
//    }
//
//
////
////    // Realm method
////    public void loadData() {
////        Realm realm = Realm.getInstance(this);
////        realm.beginTransaction();
////        Opinion opinion = realm.createObject(Opinion.class);
////        RealmResults<Opinion> query = realm.where(Opinion.class)
////                .findAll();
////        for (Opinion o : query) {
////            opinions.add(o);
////        }
////
////        setDislikeAndLikeColor(opinion.getState());
////    }
////
////    // Realm method
////    public void add(final int ticketState) {
////        menu.getMenu(new Callback<List<MenuModel>>() {
////            @Override
////            public void success(List<MenuModel> menuModel, Response response) {
////                Realm realm = Realm.getInstance(getApplicationContext());
////                realm.beginTransaction();
////                Opinion o = realm.createObject(Opinion.class);
////                o.setState(ticketState);
////                o.setId(menuModel.get(menuDayChooser).getIdentifier());
////                realm.commitTransaction();
////                opinions.add(o);
////
////                setDislikeAndLikeColor(o.getState());
////            }
////
////            @Override
////            public void failure(RetrofitError error) {
////                // Failure
////            }
////        });
////    }
//
//    public void setDislikeAndLikeColor() {
//        if (state == NON_VOTED_STATE) {
//            thumbsUpId.setColorFilter(whiteColor);
//            thumbsDownId.setColorFilter(whiteColor);
//        } else if (state == LIKED_STATE) {
//            thumbsUpId.setColorFilter(greyColor);
//            thumbsDownId.setColorFilter(whiteColor);
//        } else {
//            thumbsUpId.setColorFilter(whiteColor);
//            thumbsDownId.setColorFilter(greyColor);
//        }
//    }
//
//    private void setupImageViews() {
//        cakeDayImageView = (ImageView) findViewById(R.id.cakeDayId);
//        thumbsDownId = (ImageView) findViewById(R.id.thumbsDownId);
//        thumbsUpId = (ImageView) findViewById(R.id.thumbsUpId);
//
//        forkAndKnifeImageView = (ImageView) findViewById(R.id.forkAndKnifeId);
//    }
//
//    private void setupTextViews() {
//        mainCourseTextView = (TextView) findViewById(R.id.mainCourseText);
//        sidesTextView = (TextView) findViewById(R.id.sidesText);
//        likesTextView = (TextView) findViewById(R.id.likesText);
//        dislikesTextView = (TextView) findViewById(R.id.dislikesText);
//        visitFacebookTextView = (TextView) findViewById(R.id.visitFacebookText);
//        Typeface font = Typeface.createFromAsset(getAssets(), "Avenir.ttc");
//        mainCourseTextView.setTypeface(font);
//        sidesTextView.setTypeface(font);
//        visitFacebookTextView.setTypeface(font);
//
//        visitFacebookTextView.setMovementMethod(LinkMovementMethod.getInstance());
//
//        testTextView = (TextView) findViewById(R.id.testTextView);
//    }
//
//    private void getDeviceId() {
//        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//
//        final String tmDevice, tmSerial, androidId;
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        deviceId = deviceUuid.toString();
//    }
//
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.thumbsUpId:
//                onThumbsUp();
//                break;
//            case R.id.thumbsDownId:
//                onThumbsDown();
//                break;
//        }
//    }
//
//    private void onThumbsUp() {
//
//        menu.setVote(new LikeTask(deviceId), "like", new Callback<List<MenuModel>>() {
//            @Override
//            public void success(List<MenuModel> menuModel, Response response) {
//                // int state should be replaced when backend is implemented
//                state = LIKED_STATE;
//                setDislikeAndLikeColor();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                likesTextView.setText(error.getMessage());
//            }
//        });
//    }
//
//    private void onThumbsDown() {
//
//        menu.setVote(new LikeTask((deviceId)), "dislike", new Callback<List<MenuModel>>() {
//            @Override
//            public void success(List<MenuModel> menuModel, Response response) {
//                // int state should be replaced when backend is implemented
//                state = DISLIKED_STATE;
//                setDislikeAndLikeColor();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                dislikesTextView.setText(error.getMessage());
//            }
//        });
//    }
//
//    private void updateMenu() {
//        // This should read from server instead
//        state = NON_VOTED_STATE;
//        setDislikeAndLikeColor();
//
//        // Set the standard texts
//        mainCourseTextView.setText(R.string.loading_main_course_text);
//        sidesTextView.setText(R.string.loading_sides_text);
//        visitFacebookTextView.setVisibility(View.INVISIBLE);
//
//        // Retrofit getMenu
//        menu.getMenu(new Callback<List<MenuModel>>() {
//            @Override
//            public void success(List<MenuModel> menuModel, Response response) {
//                // Set like and dislike values from retrofit
//                likesTextView.setText(String.valueOf(menuModel
//                        .get(menuDayChooser)
//                        .getLikes()));
//                dislikesTextView.setText(String.valueOf(menuModel
//                        .get(menuDayChooser)
//                        .getDislikes()));
//
//                // Get the date from retrofit
//                updatedAtDate = menuModel.get(menuDayChooser).getUpdatedAt();
//
//                // If menu has not arrived, else show today's menu
//                if (menuDayChooser == 0 && !isTodaysMenuAvailable && !isLocalDateCorrect()) {
//                    sidesTextView.setVisibility(View.INVISIBLE);
//                    visitFacebookTextView.setVisibility(View.VISIBLE);
//                    mainCourseTextView.setText(R.string.menu_not_ready_text);
//                    cakeDayImageView.setVisibility(View.GONE);
//                    likesTextView.setVisibility(View.INVISIBLE);
//                    dislikesTextView.setVisibility(View.INVISIBLE);
//                    thumbsDownId.setVisibility(View.INVISIBLE);
//                    thumbsUpId.setVisibility(View.INVISIBLE);
//                } else {
//                    sidesTextView.setVisibility(View.VISIBLE);
//                    visitFacebookTextView.setVisibility(View.INVISIBLE);
//                    likesTextView.setVisibility(View.VISIBLE);
//                    dislikesTextView.setVisibility(View.VISIBLE);
//                    thumbsDownId.setVisibility(View.VISIBLE);
//                    thumbsUpId.setVisibility(View.VISIBLE);
//                    showMenuOfTheDay(menuModel);
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                sidesTextView.setText(error.getMessage());
//                mainCourseTextView.setText(error.getMessage());
//            }
//        });
//    }
//
//    private void showMenuOfTheDay(List<MenuModel> menuModel) {
//        // Set main course and sides text for menu of the day
//        sidesTextView.setText(menuModel.get(menuDayChooser).getSides());
//        mainCourseTextView.setText(menuModel.get(menuDayChooser).getMainCourse());
//
//        // Should cake day sign be shown or not
//        if (menuModel.get(menuDayChooser).getCake()) {
//            cakeDayImageView.setVisibility(View.VISIBLE);
//        } else {
//            cakeDayImageView.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private boolean isLocalDateCorrect() {
//        boolean isDateCorrect;
//
//        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("GMT+1");
//
//        // Set format of date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//        Calendar cal = Calendar.getInstance(tz);
//        String localDate = dateFormat.format(cal.getTime());
//
//        // Format date to be with '-' instead of '/'
//        localDate = localDate.replaceAll("/", "-");
//
//        // Remove weekday from DateFormat (if EEE is added before yyyy/MM/dd
//        // localDate = localDate.substring(4);
//
//        // Changes the date of the month for testing purposes
//        // localDate = localDate.replaceAll("13", "14");
//
//        // Make a substring of the first part of the string (until "T") and delete the rest
//        int indexOfT = updatedAtDate.indexOf('T');
//        String substringOfUpdatedAtDate = updatedAtDate.substring(0, indexOfT);
//
//        // Checks if the date from retrofit is equal to the local date
//        if (substringOfUpdatedAtDate.equals(localDate)) {
//            isDateCorrect = true;
//        } else {
//            isDateCorrect = false;
//        }
//        return isDateCorrect;
//    }
//
//    // onTouchEvent that checks the x value and swipes accordingly
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                float deltaX = x2 - x1;
//
//                if (Math.abs(deltaX) > MIN_DISTANCE) {
//                    if (x2 > x1) {
//                        // Left to right swipe action
//                        if (menuDayChooser == 0 && !isLocalDateCorrect() && !isTodaysMenuAvailable) {
//                            isTodaysMenuAvailable = true;
//                            updateMenu();
//                        } else if (menuDayChooser == 0 && isTodaysMenuAvailable) {
//                            isTodaysMenuAvailable = false;
//                            menuDayChooser++;
//                            updateMenu();
//                        } else {
//                            menuDayChooser++;
//                            updateMenu();
//                        }
//                    } else {
//                        // Right to left swipe action
//                        if (menuDayChooser == 0 && !isLocalDateCorrect()) {
//                            isTodaysMenuAvailable = false;
//                            updateMenu();
//                        } else if (menuDayChooser == 0) {
//                            isTodaysMenuAvailable = false;
//                        } else {
//                            isTodaysMenuAvailable = true;
//                            menuDayChooser--;
//                            updateMenu();
//                        }
//                    }
//                } else {
//                    // consider as something else - a screen tap for example
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//}