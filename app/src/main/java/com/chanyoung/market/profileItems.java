package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class profileItems {

    @Expose
    @SerializedName("username") private String username;

    @Expose
    @SerializedName("profile_image") private String path;

    public profileItems(String username, String path) {
        this.username = username;
        this.path  = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
