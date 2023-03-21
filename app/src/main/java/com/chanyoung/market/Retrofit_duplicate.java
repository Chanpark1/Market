package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_duplicate {

    @FormUrlEncoded // Form-urlencoded 방식으로 전송
    // 어노테이션 필수 추가
    @POST("duplicate_check.php")
    Call<String> InsertUsername(
            @Field("username") String username);
}
