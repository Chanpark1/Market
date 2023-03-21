package com.chanyoung.market;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class read_postItem {
    @Expose
    @SerializedName("userImage") private String profile_image;

    @Expose
    @SerializedName("username") private String username;

    @Expose
    @SerializedName("created") private String created;

    @Expose
    @SerializedName("title") private String title;

    @Expose
    @SerializedName("price") private String price;

    @Expose
    @SerializedName("description") private String description;

    @Expose
    @SerializedName("Area") private String area;

    @Expose
    @SerializedName("like_num") private String like_num;

    @Expose
    @SerializedName("chat_num") private String chat_num;

    @Expose
    @SerializedName("hit_num") private String hit_num;

    @Expose
    @SerializedName("trade_lat") private String tr_lat;

    @Expose
    @SerializedName("trade_lng") private String tr_lng;

    @Expose
    @SerializedName("category") private String category;

    @Expose
    @SerializedName("status") private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHit_num() {
        return hit_num;
    }

    public void setHit_num(String hit_num) {
        this.hit_num = hit_num;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTr_lat() {
        return tr_lat;
    }

    public void setTr_lat(String tr_lat) {
        this.tr_lat = tr_lat;
    }

    public String getTr_lng() {
        return tr_lng;
    }

    public void setTr_lng(String tr_lng) {
        this.tr_lng = tr_lng;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public read_postItem(String profile_image, String username, String created, String title, String price, String description, String area, String like_num, String chat_num, String hit_num, String status) {
        this.created = created;
        this.profile_image = profile_image;
        this.username = username;
        this.title = title;
        this.price = price;
        this.description = description;
        this.area = area;
        this.like_num = like_num;
        this.chat_num = chat_num;
        this.hit_num = hit_num;
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getChat_num() {
        return chat_num;
    }

    public void setChat_num(String chat_num) {
        this.chat_num = chat_num;
    }

}
