package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_login {

    @FormUrlEncoded
    @POST("login.php")
    Call<String> login(
            @Field("phone") String phone
    );
}
