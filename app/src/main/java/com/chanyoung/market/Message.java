package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    public Message(String idx, String from_auth, String to_auth, String created, String content, String userImage, int viewType, String path) {
        this.idx = idx;
        this.from_auth = from_auth;
        this.to_auth = to_auth;
        this.created = created;
        this.content = content;
        this.userImage = userImage;
        this.ViewType = viewType;
        this.path = path;

    }

    @Expose
    @SerializedName("idx") private String idx;

    @Expose
    @SerializedName("from_auth") private String from_auth;

    @Expose
    @SerializedName("to_auth") private String to_auth;

    @Expose
    @SerializedName("created") private String created;

    @Expose
    @SerializedName("content") private String content;

    @Expose
    @SerializedName("userImage") private String userImage;

    @Expose
    @SerializedName("ViewType") private int ViewType;

    @Expose
    @SerializedName("path") private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getFrom_auth() {
        return from_auth;
    }

    public void setFrom_auth(String from_auth) {
        this.from_auth = from_auth;
    }

    public String getTo_auth() {
        return to_auth;
    }

    public void setTo_auth(String to_auth) {
        this.to_auth = to_auth;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getViewType() {
        return ViewType;
    }

    public void setViewType(int viewType) {
        ViewType = viewType;
    }
}
