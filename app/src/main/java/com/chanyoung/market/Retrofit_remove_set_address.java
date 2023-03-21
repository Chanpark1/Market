package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_remove_set_address {

    @FormUrlEncoded
    @POST("remove_set_address.php")
    Call<String> insertIdx(
            @Field("idx") String idx
    );



}
