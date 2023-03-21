package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_upload_check_auth {

    @FormUrlEncoded
    @POST("upload_check_auth.php")
    Call<String> checkAuth(
            @Field("authNum")  String authNum
    );
}
