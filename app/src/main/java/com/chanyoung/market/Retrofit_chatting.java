package com.chanyoung.market;

import android.net.Uri;

import androidx.annotation.FractionRes;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Retrofit_chatting {

    @FormUrlEncoded
    @POST("load_chatList.php")
    Call<List<ChatList>> load_chatList(
            @Field("authNum") String authNum
    );

    @FormUrlEncoded
    @POST("load_chatMessage.php")
    Call<List<Message>> load_message(
            @Field("from_auth") String from_auth,
            @Field("to_auth") String to_auth,
            @Field("room_auth") String room_auth,
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("load_more_message.php")
    Call<List<Message>> load_more_message(
            @Field("from_auth") String from_auth,
            @Field("to_auth") String to_auth,
            @Field("room_auth") String room_auth,
            @Field("post_auth") String post_authNum,
            @Field("count") String count
    );

    @FormUrlEncoded
    @POST("upload_chat.php")
    Call<String> upload_chat(
            @Field("message") String message,
            @Field("from_auth") String from_auth,
            @Field("to_auth") String to_auth,
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("chatting_post_info.php")
    Call<List<post_info>> post_info(
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("get_room_pk.php")
    Call<String> get_room_pk(
            @Field("from_authNum") String from_authNum,
            @Field("to_authNum") String to_authNum,
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("message_username.php")
    Call<String> get_username(
            @Field("to_authNum") String to_authNum
    );

    @FormUrlEncoded
    @POST("get_leftover.php")
    Call<String> get_count(
            @Field("authNum") String authNum
    );

    @FormUrlEncoded
    @POST("service_manager.php")
    Call<String> service_destroyed(
            @Field("authNum") String authNum
    );
    @Multipart
    @POST("upload_chat_image.php")
    Call<List<ChatImage>> chat_image(
            @Part List<MultipartBody.Part> files,
            @PartMap Map<String, RequestBody> params
    );
}
