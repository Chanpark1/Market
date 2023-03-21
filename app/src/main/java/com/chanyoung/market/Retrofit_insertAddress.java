package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_insertAddress {

    @FormUrlEncoded
    @POST("insert_address.php")
    Call<String> insertAddress(
            @Field("authNum") String authNum,
            @Field("area") String area,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude
    );


}
