package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_update_address {

    @FormUrlEncoded
    @POST("update_address_set.php")
    Call<String> updateAddress(
            @Field("authNum") String authNum,
            @Field("area3") String area3,
            @Field("area4") String area4,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude
    );
}
