package com.chanyoung.market;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Retrofit_basic {

    @GET("category.php")
    Call<List<Category>> req_category();

    @FormUrlEncoded
    @POST("update_post_status.php")
    Call<String> update_status(
            @Field("status") String status,
            @Field("post_authNum") String post_authNum
    );


    @FormUrlEncoded
    @POST("update_like.php")
    Call<String> update_like(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum

    );

    @FormUrlEncoded
    @POST("update_comm_like.php")
    Call<String> update_comm_like(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum

    );

    @FormUrlEncoded
    @POST("check_wishlist.php")
    Call<String> check_wish(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum
    );
    @FormUrlEncoded
    @POST("check_comm_wishlist.php")
    Call<String> check_comm_wish(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum
    );


    @FormUrlEncoded
    @POST("load_profile.php")
    Call<List<profileItems>> load_profile(
            @Field("authNum") String authNum
    );

    @Multipart
    @POST("edit_profile.php")
    Call<String> edit_profile(
            @Part MultipartBody.Part added_file,
            @PartMap Map<String, RequestBody> params
    );
}
