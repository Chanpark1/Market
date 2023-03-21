package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_phone {
    @FormUrlEncoded
    @POST("verification.php")
    Call<String> insertPhone(
            @Field("phone") String phone
    );
}
