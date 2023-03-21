package com.chanyoung.market;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Retrofit_location {
    @FormUrlEncoded
    @POST("location.php")
    Call<List<locationItem>> InsertLocation(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

}
