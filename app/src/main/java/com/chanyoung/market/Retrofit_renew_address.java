package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_renew_address {

    @FormUrlEncoded
    @POST("renew_address.php")
    Call<String> updateAddress(
            @Field("authNum") String authNum,
            @Field("area") String area,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude
    );

}
