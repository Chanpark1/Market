package com.chanyoung.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReplyItems {
    public ReplyItems(String content,
                      String replyIdx,
                      String authNum,
                      String post_authNum,
                      String reply_authNum,
                      String like_num,
                      String user_path,
                      String image_path,
                      String area,
                      String username,
                      String created) {
        this.replyIdx = replyIdx;
        this.content = content;
        this.authNum = authNum;
        this.post_authNum = post_authNum;
        this.reply_authNum = reply_authNum;
        this.like_num = like_num;
        this.user_path = user_path;
        this.image_path = image_path;
        this.area = area;
        this.username = username;
        this.created = created;
    }

    @Expose
    @SerializedName("replyIdx") private String replyIdx;

    @Expose
    @SerializedName("content") private String content;

    @Expose
    @SerializedName("authNum") private String authNum;

    @Expose
    @SerializedName("post_authNum") private String post_authNum;

    @Expose
    @SerializedName("reply_authNum") private String reply_authNum;

    @Expose
    @SerializedName("like_num") private String like_num;

    @Expose
    @SerializedName("user_path") private String user_path;

    @Expose
    @SerializedName("image_path") private String image_path;

    @Expose
    @SerializedName("area") private String area;

    @Expose
    @SerializedName("username") private String username;

    @Expose
    @SerializedName("created") private String created;

    public String getReplyIdx() {
        return replyIdx;
    }

    public void setReplyIdx(String replyIdx) {
        this.replyIdx = replyIdx;
    }

    public String getReply_authNum() {
        return reply_authNum;
    }

    public void setReply_authNum(String reply_authNum) {
        this.reply_authNum = reply_authNum;
    }

    public String getUser_path() {
        return user_path;
    }

    public void setUser_path(String user_path) {
        this.user_path = user_path;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getPost_authNum() {
        return post_authNum;
    }

    public void setPost_authNum(String post_authNum) {
        this.post_authNum = post_authNum;
    }


    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }


}
