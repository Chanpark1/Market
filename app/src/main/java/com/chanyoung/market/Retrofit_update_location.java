package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_update_location {
    @FormUrlEncoded
    @POST("update_address.php")
    Call<String> insertAddress(
      @Field("authNum") String authNum,
      @Field("Area3") String area3,
      @Field("Area4") String area4,
      @Field("Longitude") String longitude,
      @Field("Latitude") String latitude
    );

}
