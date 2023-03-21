package com.chanyoung.market;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_req_address {

    @FormUrlEncoded
    @POST("getAddress.php")
    Call<List<requestAddress>> req_address(
            @Field("authNum") String authNum
    );

}
