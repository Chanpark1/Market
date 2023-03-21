package com.chanyoung.market;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Retrofit_edit_post {

    @Multipart
    @POST("edit_post.php")
    Call<String> editPost(
            @Part List<MultipartBody.Part> added_files,
//            @Part List<MultipartBody.Part> origin_files,
            @PartMap Map<String, RequestBody> params
    );

    @Multipart
    @POST("edit_comm.php")
    Call<String> edit_comm(
      @Part List<MultipartBody.Part> files,
      @PartMap Map<String, RequestBody> params
    );
}
