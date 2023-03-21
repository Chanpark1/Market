package com.chanyoung.market;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Retrofit_file {
    @Multipart
    @POST("signup.php")
    Call<String> requestFile(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> params
            );
}
