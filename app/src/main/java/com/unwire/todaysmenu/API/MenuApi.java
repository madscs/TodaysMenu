package com.unwire.todaysmenu.API;
import com.unwire.todaysmenu.LikeTask;
import com.unwire.todaysmenu.model.MenuModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

// API Interface for Retrofit

public interface MenuApi {
    @GET("/menus")
    void getFeed(Callback<List<MenuModel>> response);

    @POST("/votes/{like}")
    void setVote(@Body LikeTask likeTask, @Path("like") String like,
                 Callback<List<MenuModel>> response);
}