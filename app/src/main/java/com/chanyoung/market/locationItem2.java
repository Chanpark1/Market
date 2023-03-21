package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class locationItem2 {

    @Expose
    @SerializedName("result") private String result;
    @Expose
    @SerializedName("Longitude") private String longitude;
    @Expose
    @SerializedName("Latitude") private String latitude;
    @Expose
    @SerializedName("Area3") private String area;
    @Expose
    @SerializedName("Area4") private String area4;

    public String getArea4() {
        return area4;
    }

    public void setArea4(String area4) {
        this.area4 = area4;
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

    public locationItem2(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "locationItem2{" +
                "result='" + result + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", area='" + area + '\'' +
                ", area4='" + area4 + '\'' +
                '}';
    }
}
