package com.chanyoung.market;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Retrofit_verification {
    @FormUrlEncoded // Form-urlencoded 방식으로 전송
    // 어노테이션 필수 추가
    @POST("verification.php")
    Call<String> InsertPhone(
            @Field("phone") String phone);
}
