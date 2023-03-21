package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicItems {

    @Expose
    @SerializedName("area") String area;

    @Expose
    @SerializedName("longitude") String longitude;

    @Expose
    @SerializedName("latitude") String latitude;

    public BasicItems(String area, String longitude, String latitude) {
        this.area = area;
        this.longitude = longitude;
        this.latitude = latitude;
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
}
