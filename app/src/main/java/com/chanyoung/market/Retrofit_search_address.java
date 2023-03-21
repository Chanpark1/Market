package com.chanyoung.market;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_search_address {
    @FormUrlEncoded
    @POST("search_address.php")
    Call<List<locationItem2>> InsertText(
      @Field("text") String text
    );
}
