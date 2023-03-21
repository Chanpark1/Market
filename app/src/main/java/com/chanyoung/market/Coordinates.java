package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coordinates {
    @Expose
    @SerializedName("trade_lat") String trade_lat;

    @Expose
    @SerializedName("trade_lng") String trade_lng;

    @Expose
    @SerializedName("authNum") String authNum;

    public Coordinates(String trade_lat, String trade_lng, String authNum) {
        this.trade_lat = trade_lat;
        this.trade_lng = trade_lng;
        this.authNum = authNum;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getTrade_lat() {
        return trade_lat;
    }

    public void setTrade_lat(String trade_lat) {
        this.trade_lat = trade_lat;
    }

    public String getTrade_lng() {
        return trade_lng;
    }

    public void setTrade_lng(String trade_lng) {
        this.trade_lng = trade_lng;
    }
}
