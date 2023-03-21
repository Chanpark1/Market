package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class communityItems {
    @Expose
    @SerializedName("title") private String title;

    @Expose
    @SerializedName("content") private String content;

    @Expose
    @SerializedName("category") private String category;

    @Expose
    @SerializedName("authNum") private String authNum;

    @Expose
    @SerializedName("post_authNum") private String post_authNum;

    @Expose
    @SerializedName("area") private String area;

    @Expose
    @SerializedName("longitude") private String longitude;

    @Expose
    @SerializedName("latitude") private String latitude;

    @Expose
    @SerializedName("hit_num") private String hit;

    @Expose
    @SerializedName("like_num") private String like;

    @Expose
    @SerializedName("reply_num") private String rep_num;

    @Expose
    @SerializedName("created") private String created;

    @Expose
    @SerializedName("image_path") private String path;

    public communityItems(
            String title,
            String content,
            String category,
            String authNum,
            String post_authNum,
            String area,
            String longitude,
            String latitude,
            String hit,
            String like,
            String rep_num,
            String created,
            String path) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.authNum = authNum;
        this.post_authNum = post_authNum;
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;
        this.hit = hit;
        this.like = like;
        this.rep_num = rep_num;
        this.created = created;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getPost_authNum() {
        return post_authNum;
    }

    public void setPost_authNum(String post_authNum) {
        this.post_authNum = post_authNum;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getRep_num() {
        return rep_num;
    }

    public void setRep_num(String rep_num) {
        this.rep_num = rep_num;
    }

    @Override
    public String toString() {
        return "communityItems{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", authNum='" + authNum + '\'' +
                ", post_authNum='" + post_authNum + '\'' +
                ", area='" + area + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", hit='" + hit + '\'' +
                ", like='" + like + '\'' +
                ", rep_num='" + rep_num + '\'' +
                '}';
    }
}
