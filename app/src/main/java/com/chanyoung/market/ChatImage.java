package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatImage {

    public ChatImage(String image) {
        this.image = image;
    }

    @Expose
    @SerializedName("image") private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
