package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class comm_read_items {

    public comm_read_items(
                           String post_authNum,
                           String title,
                           String category,
                           String username,
                           String area,
                           String content,
                           String like_num,
                           String reply_num,
                           String hit_num,
                           String path) {
        this.post_authNum = post_authNum;
        this.title = title;
        this.category = category;
        this.username = username;
        this.area = area;
        this.content = content;
        this.like_num = like_num;
        this.reply_num = reply_num;
        this.hit_num = hit_num;
        this.path = path;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Expose
    @SerializedName("post_authNum") private String post_authNum;

    @Expose
    @SerializedName("title") private String title;

    @Expose
    @SerializedName("category") private String category;

    @Expose
    @SerializedName("username") private String username;

    @Expose
    @SerializedName("area") private String area;

    @Expose
    @SerializedName("content") private String content;

    @Expose
    @SerializedName("like_num") private String like_num;

    @Expose
    @SerializedName("reply_num") private String reply_num;

    @Expose
    @SerializedName("hit_num") private String hit_num;

    @Expose
    @SerializedName("path") private String path;

    public String getHit_num() {
        return hit_num;
    }

    public void setHit_num(String hit_num) {
        this.hit_num = hit_num;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getReply_num() {
        return reply_num;
    }

    public void setReply_num(String reply_num) {
        this.reply_num = reply_num;
    }
}
