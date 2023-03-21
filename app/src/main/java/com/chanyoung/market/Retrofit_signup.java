package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_signup {

    @FormUrlEncoded
    @POST("signup.php")
    Call<String> InsertProfile(
            @Field("username") String username,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("area") String area,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude


//            @Field("image") String image
    );


}
