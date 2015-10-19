package com.unwire.todaysmenu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuModel {

    @SerializedName("cake")
    @Expose
    private Boolean cake;

    @SerializedName("dislikes")
    @Expose
    private Integer dislikes;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("likes")
    @Expose
    private Integer likes;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("main_course")
    @Expose
    private String mainCourse;

    @SerializedName("serving_date")
    @Expose
    private Integer servingDate;

    @SerializedName("sides")
    @Expose
    private String sides;

    @SerializedName("user_vote")
    @Expose
    private Integer userVote;

    /**
     * @return The cake
     */
    public Boolean getCake() {
        return cake;
    }

    /**
     * @param cake The cake
     */
    public void setCake(Boolean cake) {
        this.cake = cake;
    }

    /**
     * @return The dislikes
     */
    public Integer getDislikes() {
        return dislikes;
    }

    /**
     * @param dislikes The dislikes
     */
    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The likes
     */
    public Integer getLikes() {
        return likes;
    }

    /**
     * @param likes The likes
     */
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The mainCourse
     */
    public String getMainCourse() {
        return mainCourse;
    }

    /**
     * @param mainCourse The main_course
     */
    public void setMainCourse(String mainCourse) {
        this.mainCourse = mainCourse;
    }

    /**
     * @return The servingDate
     */
    public Integer getServingDate() {
        return servingDate;
    }

    /**
     * @param servingDate The serving_date
     */
    public void setServingDate(Integer servingDate) {
        this.servingDate = servingDate;
    }

    /**
     * @return The sides
     */
    public String getSides() {
        return sides;
    }

    /**
     * @param sides The sides
     */
    public void setSides(String sides) {
        this.sides = sides;
    }

    /**
     * @return The userVote
     */
    public Integer getUserVote() {
        return userVote;
    }

    /**
     * @param userVote The user_vote
     */
    public void setUserVote(Integer userVote) {
        this.userVote = userVote;
    }


}