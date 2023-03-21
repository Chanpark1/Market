package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_isSelected {

    @FormUrlEncoded
    @POST("is_selected.php")
    Call<String> is_selected(
            @Field("username") String username
    );


}
