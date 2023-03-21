package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class multipleImage {

    @Expose
    @SerializedName("path") private String path;

    public multipleImage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "multipleImage{" +
                "path='" + path + '\'' +
                '}';
    }
}
