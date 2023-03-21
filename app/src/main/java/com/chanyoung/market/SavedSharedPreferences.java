package com.chanyoung.market;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;

public class SavedSharedPreferences {

    static final String PREF_USERNAME = "username";

    static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("Username", Context.MODE_PRIVATE);
    }

    static SharedPreferences getSavedPreferences(Context context) {
        return context.getSharedPreferences("savedState",Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPreferences_state(Context context) {
        return context.getSharedPreferences("savedState",Context.MODE_PRIVATE);
    }

    static SharedPreferences getPreferences_post(Context context) {
        return context.getSharedPreferences("post",Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPreferences_uri(Context context) {
        return context.getSharedPreferences("uri",Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPreferences_editPost(Context context) {
        return context.getSharedPreferences("edit_post",Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPreferences_message(Context context) {
        return context.getSharedPreferences("message",Context.MODE_PRIVATE);
    }

    public void setMessage(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences_message(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public void clearMessage(Context context, String key) {
        SharedPreferences prefs = getPreferences_message(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }


    public void setEdit(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences_editPost(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void clearEdit(Context context, String key) {
        SharedPreferences prefs = getPreferences_editPost(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void setUri(Context context, String key, JSONArray jsonArray) {
        SharedPreferences prefs = getPreferences_uri(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, String.valueOf(jsonArray));
        editor.apply();
    }

    public void clearUri(Context context, String key) {
        SharedPreferences prefs = getPreferences_uri(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }


    public void save_state(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences_state(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public void setUserName(Context context, String key, String username) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, username);
        editor.apply();
    }

    public void setPost(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences_post(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public void clearPost(Context context, String key) {
        SharedPreferences prefs = getPreferences_post(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(PREF_USERNAME,"");
    }

    public void clearSavedState(Context context, String key) {
        SharedPreferences prefs = getSavedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    public void clearUserName(Context context, String key) {
        SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
