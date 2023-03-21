package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class sortedItems {

    public sortedItems(
            String post_authNum,
            String title,
            String price,
            String description,
            String area,
            String like,
            String chat_num,
            String hit_num,
            String image_uri,
            String tr_lat,
            String tr_lng,
            String created,
            String status,
            String category,
            String authNum) {
        this.post_authNum = post_authNum;
        this.title = title;
        this.price = price;
        this.description = description;
        this.area = area;
        this.like = like;
        this.chat_num = chat_num;
        this.hit_num = hit_num;
        this.image_uri = image_uri;
        this.tr_lat = tr_lat;
        this.tr_lng = tr_lng;
        this.created = created;
        this.status = status;
        this.category = category;
        this.authNum = authNum;
    }

    @Expose
    @SerializedName("post_authNum") private String post_authNum;

    @Expose
    @SerializedName("title") private String title;

    @Expose
    @SerializedName("price") private String price;

    @Expose
    @SerializedName("description") private String description;

    @Expose
    @SerializedName("Area") private String area;

    @Expose
    @SerializedName("like") private String like;

    @Expose
    @SerializedName("chat_num") private String chat_num;

    @Expose
    @SerializedName("hit_num") private String hit_num;

    @Expose
    @SerializedName("profile_image") private String image_uri;

    @Expose
    @SerializedName("trade_lat") private String tr_lat;

    @Expose
    @SerializedName("trade_lng") private String tr_lng;

    @Expose
    @SerializedName("created") private String created;

    @Expose
    @SerializedName("status") private String status;

    @Expose
    @SerializedName("category") private String category;

    @Expose
    @SerializedName("authNum") private String authNum;


    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPost_authNum() {
        return post_authNum;
    }

    public void setPost_authNum(String post_authNum) {
        this.post_authNum = post_authNum;
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

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getChat_num() {
        return chat_num;
    }

    public void setChat_num(String chat_num) {
        this.chat_num = chat_num;
    }

    public String getHit_num() {
        return hit_num;
    }

    public void setHit_num(String hit_num) {
        this.hit_num = hit_num;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
