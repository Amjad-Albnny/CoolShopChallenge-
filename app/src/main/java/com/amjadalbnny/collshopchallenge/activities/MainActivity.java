package com.amjadalbnny.collshopchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amjadalbnny.collshopchallenge.Interfaces.GetAvatarUrlCompletion;
import com.amjadalbnny.collshopchallenge.Interfaces.GetUserCredentialsInterface;
import com.amjadalbnny.collshopchallenge.Interfaces.GetUserProfileCompletion;
import com.amjadalbnny.collshopchallenge.R;
import com.amjadalbnny.collshopchallenge.managers.ServerCallsManager;
import com.amjadalbnny.collshopchallenge.managers.SharedPreferencesManager;
import com.amjadalbnny.collshopchallenge.utils.ResponseCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.email_edit_text)
    EditText emailEditText;

    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @BindView(R.id.sign_in_button)
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(isLoggedIn()){
            openProfileActivity();
        }
    }

    @OnClick(R.id.sign_in_button)
    void signInClicked(View view){

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        ServerCallsManager.getInstance().getUserCredentials(email, password, new GetUserCredentialsInterface() {
            @Override
            public void onComplete(int responseCode, String userId, String token) {

                if(responseCode == ResponseCode.SUCCESS){
                    Log.d("UserId", userId);
                    Log.d("Token", token);

                    SharedPreferencesManager.getInstance().saveUserID(userId);
                    SharedPreferencesManager.getInstance().saveToken(token);
                    SharedPreferencesManager.getInstance().savePassword(password);

                    openProfileActivity();
                }
            }
        });

    }

    private boolean isLoggedIn(){
        if(!SharedPreferencesManager.getInstance().getToken().isEmpty()){
            return true;
        }

        return false;
    }

    private void openProfileActivity(){
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
