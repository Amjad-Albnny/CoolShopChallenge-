package com.amjadalbnny.collshopchallenge.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static SharedPreferencesManager instance;
    private Context context;

    private final String USER_PROFILE = "UserProfile";

    public static SharedPreferencesManager getInstance(){

        if(instance == null)
            instance = new SharedPreferencesManager();

        return instance;
    }

    public void init(Context context){
        this.context = context;
    }

    public void clearAll(){
        saveUserID("");
        savePassword("");
        saveEmail("");
        saveToken("");
        saveAvatarUrl("");
    }

    public void saveUserID(String userID) {

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userID", userID);

        editor.commit();

    }

    public String getUserID(){

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        String userID = "";
        if (sp.contains("userID")) {
            userID = sp.getString("userID","");
        }

        return userID;

    }

    public void saveEmail(String email) {

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);

        editor.commit();

    }

    public String getEmail(){

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        String email = "";
        if (sp.contains("email")) {
            email = sp.getString("email","");
        }

        return email;

    }

    public void saveAvatarUrl(String avatarUrl) {

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("avatarUrl", avatarUrl);

        editor.commit();

    }

    public String getAvatarUrl(){

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        String avatarUrl = "";
        if (sp.contains("avatarUrl")) {
            avatarUrl = sp.getString("avatarUrl","");
        }

        return avatarUrl;

    }

    public void saveToken(String token) {

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);

        editor.commit();

    }

    public String getToken(){

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        String token = "";
        if (sp.contains("token")) {
            token = sp.getString("token","");
        }

        return token;

    }

    public void savePassword(String password) {

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password", password);

        editor.commit();

    }

    public String getPassword(){

        SharedPreferences sp = context.getSharedPreferences(USER_PROFILE, Context.MODE_PRIVATE);

        String password = "";
        if (sp.contains("password")) {
            password = sp.getString("password","");
        }

        return password;

    }

}
