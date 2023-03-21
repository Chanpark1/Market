package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_change_phone {
    @FormUrlEncoded
    @POST("change_phone.php")
    Call<String> updatePhone(
            @Field("phone") String phone,
            @Field("email") String email
    );
}
