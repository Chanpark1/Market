package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class locationItem {

//    @SerializedName()
//    직렬화 : Object 를 Json String 형태로 Convert 해준다.
//    @Expose
//    Object 중 해당 값이 null 일 경우, json으로 만들 필드를 자동으로 생략해준다.

    @Expose
    @SerializedName("Area1") private String area1;
    @Expose
    @SerializedName("Area2") private String area2;
    @Expose
    @SerializedName("Area3") private String area3;
    @Expose
    @SerializedName("Area4") private String area4;
    @Expose
    @SerializedName("Longitude") private String longitude;
    @Expose
    @SerializedName("Latitude") private String latitude;


    public locationItem(String area1, String area2, String area3, String area4, String longitude, String latitude) {
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
        this.area4 = area4;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public String getArea1() {
        return area1;
    }

    public void setArea1(String area1) {
        this.area1 = area1;
    }

    public String getArea2() {
        return area2;
    }

    public void setArea2(String area2) {
        this.area2 = area2;
    }

    public String getArea3() {
        return area3;
    }

    public void setArea3(String area3) {
        this.area3 = area3;
    }

    public String getArea4() {
        return area4;
    }

    public void setArea4(String area4) {
        this.area4 = area4;
    }

    @Override
    public String toString() {
        return "locationItem{" +
                "area1='" + area1 + '\'' +
                ", area2='" + area2 + '\'' +
                ", area3='" + area3 + '\'' +
                ", area4='" + area4 + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
