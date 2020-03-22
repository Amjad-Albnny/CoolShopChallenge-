package com.amjadalbnny.collshopchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amjadalbnny.collshopchallenge.Interfaces.GetAvatarUrlCompletion;
import com.amjadalbnny.collshopchallenge.Interfaces.GetUserProfileCompletion;
import com.amjadalbnny.collshopchallenge.R;
import com.amjadalbnny.collshopchallenge.managers.ServerCallsManager;
import com.amjadalbnny.collshopchallenge.managers.SharedPreferencesManager;
import com.amjadalbnny.collshopchallenge.utils.ResponseCode;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_image_view)
    ImageView profileImageView;

    @BindView(R.id.email_text_view)
    TextView emailTextView;

    @BindView(R.id.password_text_view)
    TextView passwordTextView;

    @BindView(R.id.sign_out_button)
    Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        initContent();

    }

    private void initContent(){

        ServerCallsManager.getInstance().getUserProfile(SharedPreferencesManager.getInstance().getUserID(), new GetUserProfileCompletion() {
            @Override
            public void onComplete(int responseCode, String email, String avatarUrl) {

                if(responseCode == ResponseCode.SUCCESS){
                    Log.d("Email", email);
                    Log.d("AvatarUrl", avatarUrl);

                    SharedPreferencesManager.getInstance().saveEmail(email);
                    SharedPreferencesManager.getInstance().saveToken(avatarUrl);

                    Picasso.get().load(avatarUrl).into(profileImageView);
                }

            }
        });

//        ServerCallsManager.getInstance().getAvatarUrl("", new GetAvatarUrlCompletion() {
//            @Override
//            public void onComplete(int responseCode, String avatarUrl) {
//
//                if(responseCode == ResponseCode.SUCCESS){
//                    Log.d("AvatarUrl1", avatarUrl);
//                    SharedPreferencesManager.getInstance().saveToken(avatarUrl);
//                }
//
//            }
//        });

        emailTextView.setText(SharedPreferencesManager.getInstance().getEmail());
        passwordTextView.setText(SharedPreferencesManager.getInstance().getPassword());

    }

}
