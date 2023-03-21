package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class post_info {

    public post_info(String postImage, String status, String title, String price) {
        this.postImage = postImage;
        this.status = status;
        this.title = title;
        this.price = price;
    }

    @Expose
    @SerializedName("postImage") private String postImage;

    @Expose
    @SerializedName("status") private String status;

    @Expose
    @SerializedName("title") private String title;

    @Expose
    @SerializedName("price") private String price;

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
