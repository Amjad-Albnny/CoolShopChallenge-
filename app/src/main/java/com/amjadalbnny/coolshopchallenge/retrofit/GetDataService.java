package com.amjadalbnny.coolshopchallenge.retrofit;

import com.amjadalbnny.coolshopchallenge.utils.Keys;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @FormUrlEncoded
    @POST(Keys.BASE_URL + "sessions/new")
    Call<ResponseBody> getUserCredentials(@Field("email") String email,@Field("password") String password);

    @GET(Keys.BASE_URL + "users")
    Call<ResponseBody> getUserProfile(@Query("user_id") String userId);

    @FormUrlEncoded
    @POST(Keys.BASE_URL + "users/{USER_ID}/avatar")
    Call<ResponseBody> getAvatarUrl(@Path("USER_ID") String userId,@Field("avatar") String avatar);
}
