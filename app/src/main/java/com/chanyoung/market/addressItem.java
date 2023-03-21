package com.chanyoung.market;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class addressItem {

    @Expose
    @SerializedName("idx") private String idx;
    @Expose
    @SerializedName("Area") private String area;
    @Expose
    @SerializedName("Longitude") private String longitude;
    @Expose
    @SerializedName("Latitude") private String latitude;


    public addressItem(String idx,String area, String longitude, String latitude) {
        this.idx = idx;
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;

    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
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

    @Override
    public String toString() {
        return "addressItem{" +
                "idx='" + idx + '\'' +
                ", area='" + area + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
