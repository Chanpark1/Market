package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatList {

    public ChatList(String post_authNum, String path, String username, String area, String content, String to_auth, String post_path, String room_auth) {
        this.post_authNum = post_authNum;
        this.path = path;
        this.username = username;
        this.area = area;
        this.content = content;
        this.to_auth = to_auth;
        this.post_path = post_path;
        this.room_auth = room_auth;
    }

    @Expose
    @SerializedName("post_authNum") String post_authNum;

    @Expose
    @SerializedName("profile_image") String path;

    @Expose
    @SerializedName("username") String username;

    @Expose
    @SerializedName("area") String area;

    @Expose
    @SerializedName("content") String content;

    @Expose
    @SerializedName("to_auth") String to_auth;

    @Expose
    @SerializedName("post_image") String post_path;

    @Expose
    @SerializedName("room_auth") String room_auth;

    public String getRoom_auth() {
        return room_auth;
    }

    public void setRoom_auth(String room_auth) {
        this.room_auth = room_auth;
    }

    public String getPost_authNum() {
        return post_authNum;
    }

    public void setPost_authNum(String post_authNum) {
        this.post_authNum = post_authNum;
    }

    public String getPost_path() {
        return post_path;
    }

    public void setPost_path(String post_path) {
        this.post_path = post_path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTo_auth() {
        return to_auth;
    }

    public void setTo_auth(String to_auth) {
        this.to_auth = to_auth;
    }
}
