package com.unwire.todaysmenu.API;

import com.squareup.okhttp.OkHttpClient;
import com.unwire.todaysmenu.TokenTask;
import com.unwire.todaysmenu.VoteTask;
import com.unwire.todaysmenu.model.AuthenticationModel;
import com.unwire.todaysmenu.model.MenuModel;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

public class MenuApiManager {
    // API Interface for Retrofit
    public interface ApiService {
        @GET("/api/v1/menus?limit=10")
        void getMenu(@Header("Authorization") String authId,
                     Callback<List<MenuModel>> response);

        @POST("/api/v1/menus/{menuId}/votes")
        void setVote(@Header("Authorization") String authId,
                     @Body VoteTask voteTask,
                     @Path("menuId") Integer menuId,
                     Callback<List<MenuModel>> response); // Er der noget response??

        @POST("/api/v1/devices")
        void postToken(@Body TokenTask tokenTask,
                       Callback<AuthenticationModel> response);
    }

    // Retrofit
    private static final String API = "https://todays-menu.herokuapp.com";
    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(API)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(new OkHttpClient()))
            .build();

    public static final ApiService API_SERVICE = REST_ADAPTER.create(ApiService.class);

    public static ApiService getService() {
        return API_SERVICE;
    }
}