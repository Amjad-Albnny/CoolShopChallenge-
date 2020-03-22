package com.amjadalbnny.coolshopchallenge.managers;

import com.amjadalbnny.coolshopchallenge.Interfaces.GetAvatarUrlCompletion;
import com.amjadalbnny.coolshopchallenge.Interfaces.GetDataCompletion;
import com.amjadalbnny.coolshopchallenge.Interfaces.GetUserCredentialsInterface;
import com.amjadalbnny.coolshopchallenge.Interfaces.GetUserProfileCompletion;
import com.amjadalbnny.coolshopchallenge.utils.AESCrypt;
import com.amjadalbnny.coolshopchallenge.utils.ResponseCode;
import com.amjadalbnny.coolshopchallenge.retrofit.GetDataService;
import com.amjadalbnny.coolshopchallenge.retrofit.RetrofitClientInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerCallsManager {

    private static ServerCallsManager instance;

    public static ServerCallsManager getInstance(){

        if(instance == null)
            instance = new ServerCallsManager();

        return instance;
    }

    public void getUserProfile(String userID, final GetUserProfileCompletion completion){

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = service.getUserProfile(userID);

        getData(call, new GetDataCompletion() {
            @Override
            public void onComplete(int responseCode, String result) {

                String email = "";
                String avatarUrl = "";

                if(responseCode == ResponseCode.SUCCESS){

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        email = jsonObject.getString("email");
                        avatarUrl = jsonObject.getString("avatar_url");

                        completion.onComplete(responseCode, email, avatarUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                completion.onComplete(responseCode, email, avatarUrl);

            }
        });
    }

    public void getUserCredentials(String email, String password, final GetUserCredentialsInterface completion){

        try {
            password = AESCrypt.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = service.getUserCredentials(email, password);

        getData(call, new GetDataCompletion() {
            @Override
            public void onComplete(int responseCode, String result) {

                String userId = "";
                String token = "";

                if(responseCode == ResponseCode.SUCCESS){
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        userId = jsonObject.getString("userId");
                        token = jsonObject.getString("token");

                        completion.onComplete(responseCode, userId, token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    completion.onComplete(responseCode, "", "");
                }
            }
        });
    }

    public void getAvatarUrl(String avatar, final GetAvatarUrlCompletion completion){

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = service.getAvatarUrl(SharedPreferencesManager.getInstance().getUserID(),avatar);

        getData(call, new GetDataCompletion() {
            @Override
            public void onComplete(int responseCode, String result) {
                String avatarUrl = "";

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    avatarUrl = jsonObject.getString("avatar_url");

                    completion.onComplete(responseCode, avatarUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void getData(Call<ResponseBody> call, GetDataCompletion completion){

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 400){
                    completion.onComplete(ResponseCode.SERVER_ERROR, "");
                    return;
                }

                if(response.body() == null){
                    completion.onComplete(ResponseCode.EMPTY,"");
                    return;
                }

                try {
                    completion.onComplete(ResponseCode.SUCCESS, response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                completion.onComplete(ResponseCode.FAILURE, "");
            }
        });
    }
}