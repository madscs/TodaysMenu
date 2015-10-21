package com.unwire.todaysmenu.API;

import com.unwire.todaysmenu.VoteTask;
import com.unwire.todaysmenu.model.MenuModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

// API Interface for Retrofit
public interface MenuApi {
    @GET("/api/v1/menus?limit=10")
    void getMenu(@Header("Authorization") String authId,
                 Callback<List<MenuModel>> response);

    @POST("/api/v1/menus/{menuId}/votes")
    void setVote(@Body VoteTask voteTask,
                 @Header("Authorization")  String authId,
                 @Path("menuId") Integer menuId,
                 Callback<List<MenuModel>> response);
}