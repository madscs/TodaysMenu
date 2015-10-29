package com.unwire.todaysmenu.API;

import android.util.Base64;

import com.squareup.okhttp.OkHttpClient;
import com.unwire.todaysmenu.TokenCache;
import com.unwire.todaysmenu.TokenTask;
import com.unwire.todaysmenu.VoteTask;
import com.unwire.todaysmenu.model.AuthenticationModel;
import com.unwire.todaysmenu.model.MenuModel;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public class MenuApiManager {

    // API Interface for Retrofit
    public interface ApiService {
        @GET("/api/v1/menus?limit=10")
        void getMenu(Callback<List<MenuModel>> response);

        @POST("/api/v1/menus/{menuId}/votes")
        void setVote(@Body VoteTask voteTask,
                     @Path("menuId") Integer menuId,
                     Callback<List<MenuModel>> response);

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
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", setHttpBasicAuth());
                }
            })
            .build();

    public static final ApiService API_SERVICE = REST_ADAPTER.create(ApiService.class);

    public static ApiService getService() {
        return API_SERVICE;
    }

    public static String setHttpBasicAuth() {
        String username = TokenCache.getInstance().getToken();
        String password = "0e0f90dcee78b2a8c5577ea37ecc23616515fe604ec7b3c7180e90d99aaa906d";
        String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
}