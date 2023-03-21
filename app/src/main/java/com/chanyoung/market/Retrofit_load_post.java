package com.chanyoung.market;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Retrofit_load_post {
    @Multipart
    @POST("upload_post.php")
    Call<String> uploadPost(
            @Part List<MultipartBody.Part> files,
            @PartMap Map<String, RequestBody> params
            );

    @FormUrlEncoded
    @POST("load_post.php")
    Call<List<postItem>> load_post(
            @Field("longitude") String longitude,
            @Field("latitude") String latitude,
            @Field("distance") String distance
    );

    @FormUrlEncoded
    @POST("load_post_simplified.php")
    Call<List<postItem>> load_post_simplified(
            @Field("authNum") String authNum,
            @Field("distance") String distance
    );



    @FormUrlEncoded
    @POST("read_post.php")
    Call<List<read_postItem>> read_post(
      @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("request_post_image.php")
    Call<List<multipleImage>> request_image(
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("request_comm_image.php")
    Call<List<multipleImage>> request_comm_image(
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("delete_sale_post.php")
    Call<String> delete_sale_post (
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("delete_comm_post.php")
    Call<String> delete_comm_post (
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("load_map.php")
    Call<List<Coordinates>> request_co(
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("up_hit.php")
    Call<String> up_hit(
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("load_post_category.php")
    Call<List<sortedItems>> load_post_category(
            @Field("category") String category
    );

    @FormUrlEncoded
    @POST("search_post.php")
    Call<List<postItem>> search_post(
            @Field("text") String text,
            @Field("distance") String progress,
            @Field("authNum") String authNum
    );

    @FormUrlEncoded
    @POST("load_sold_post.php")
    Call<List<postItem>> sold_post(
            @Field("authNum") String authNum
    );

    @FormUrlEncoded
    @POST("load_reserved_post.php")
    Call<List<postItem>> reserved_post(
            @Field("authNum") String authNum
    );
    @FormUrlEncoded
    @POST("load_done_post.php")
    Call<List<postItem>> done_post(
            @Field("authNum") String authNum
    );

    @FormUrlEncoded
    @POST("load_main_comm.php")
    Call<List<communityItems>> comm_post(
            @Field("authNum") String authNum,
            @Field("distance") String distance
    );

    @FormUrlEncoded
    @POST("load_pop_comm.php")
    Call<List<communityItems>> pop_post(
            @Field("authNum") String authNum,
            @Field("distance") String distance
    );
    @FormUrlEncoded
    @POST("load_all_pop.php")
    Call<List<communityItems>> all_pop (
            @Field("authNum") String auth,
            @Field("distance") String distance
            );

    @Multipart
    @POST("upload_comm_post.php")
    Call<String> upload_comm(
            @Part List<MultipartBody.Part> files,
            @PartMap Map<String, RequestBody> params
    );

    @FormUrlEncoded
    @POST("comm_read_post.php")
    Call<List<comm_read_items>> load_comm_post(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("load_edit_post.php")
    Call<List<comm_read_items>> load_edit_post(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("load_reply.php")
    Call<List<ReplyItems>> load_reply(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("load_more_reply.php")
    Call<List<ReplyItems>> load_more_reply(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum,
            @Field("count") String count
    );



    @Multipart
    @POST("upload_reply.php")
    Call<List<ReplyItems>> upload_reply(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> params
    );

    @FormUrlEncoded
    @POST("update_comm_hit.php")
    Call<String> update_hit(
            @Field("post_authNum") String post_authNum
    );

    @FormUrlEncoded
    @POST("get_username.php")
    Call<String> get_username(
            @Field("authNum") String authNum,
            @Field("post_authNum") String post_authNum
    );

    @Multipart
    @POST("upload_rep_rep.php")
    Call<List<Rep_rep_Items>> upload_rep_rep(
            @Part MultipartBody.Part file,
            @PartMap Map<String, RequestBody> params
    );

    @FormUrlEncoded
    @POST("load_reply_reply.php")
    Call<List<Rep_rep_Items>> load_rep_rep(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum
    );
    @FormUrlEncoded
    @POST("load_more_replies.php")
    Call<List<Rep_rep_Items>> load_more_replies(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum,
            @Field("count") String count
    );

    @FormUrlEncoded
    @POST("isThereReplies.php")
    Call<String> isThere(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum
    );

    @FormUrlEncoded
    @POST("delete_reply.php")
    Call<String> delete_reply(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum
    );

    @FormUrlEncoded
    @POST("delete_replies.php")
    Call<String> delete_replies(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum,
            @Field("replyIdx") String replyIdx
    );

    @FormUrlEncoded
    @POST("load_origin_reply.php")
    Call<List<ReplyItems>> load_origin_reply(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum
    );

    @Multipart
    @POST("update_reply.php")
    Call<String> update_reply(
            @Part MultipartBody.Part file,
            @PartMap Map<String,RequestBody> params
    );

    @FormUrlEncoded
    @POST("load_origin_replies.php")
    Call<List<Rep_rep_Items>> load_origin_replies(
            @Field("post_authNum") String post_authNum,
            @Field("reply_authNum") String reply_authNum,
            @Field("replyIdx") String replyIdx
    );

    @Multipart
    @POST("update_replies.php")
    Call<String> update_replies(
            @Part MultipartBody.Part file,
            @PartMap Map<String,RequestBody> params
    );

    @FormUrlEncoded
    @POST("get_postAuth.php")
    Call<String> get_post(
            @Field("post_authNum") String post_authNum
    );



}
