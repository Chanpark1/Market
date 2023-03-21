package com.chanyoung.market;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_set_address {

    @FormUrlEncoded
    @POST("set_address.php")
    Call<List<addressItem>> insertQuery (
            @Field("authNum") String authNum
    );
}
