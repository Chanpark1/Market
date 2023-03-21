package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_email {
    @FormUrlEncoded
    @POST("email_verification.php")
    Call<String> InsertEmail(
            @Field("email") String email
    );
}
