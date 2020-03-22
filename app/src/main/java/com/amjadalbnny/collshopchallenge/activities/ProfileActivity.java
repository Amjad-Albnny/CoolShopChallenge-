package com.amjadalbnny.collshopchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amjadalbnny.collshopchallenge.Interfaces.GetUserProfileCompletion;
import com.amjadalbnny.collshopchallenge.R;
import com.amjadalbnny.collshopchallenge.managers.ServerCallsManager;
import com.amjadalbnny.collshopchallenge.managers.SharedPreferencesManager;
import com.amjadalbnny.collshopchallenge.utils.ResponseCode;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {



    @BindView(R.id.profile_image_view)
    ImageView profileImageView;

    @BindView(R.id.email_text_view)
    TextView emailTextView;

    @BindView(R.id.password_text_view)
    TextView passwordTextView;

    @BindView(R.id.sign_out_button)
    Button signOutButton;

    private int GALLERY = 1, CAMERA = 2;
    private static final int REQUEST_ALL_PERMISSIONS = 100;

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

    @OnClick(R.id.profile_image_view)
    void profilePhotoClicked(){
        getPermissions();
    }


    @OnClick(R.id.sign_out_button)
    void signOutClicked(){

        SharedPreferencesManager.getInstance().clearAll();
        onBackPressed();

    }

    private void getPermissions(){

        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.CAMERA"
        };

        int CAMERA_permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int READ_EXTERNAL_STORAGE_permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if ((READ_EXTERNAL_STORAGE_permissionCheck != PackageManager.PERMISSION_GRANTED)
                ||(CAMERA_permissionCheck != PackageManager.PERMISSION_GRANTED) )
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ALL_PERMISSIONS);
        else{
            showPictureDialog();
        }

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(R.string.update_profile_photo));
        String[] pictureDialogItems = {
                getResources().getString(R.string.gallery),
                getResources().getString(R.string.camera)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        Bitmap bitmap = null;

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error", e.toString());
                }
            }

        } else if (requestCode == CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

        }

        profileImageView.setImageBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case REQUEST_ALL_PERMISSIONS:
                if ((grantResults.length <= 0)
                        || (grantResults[0] == PackageManager.PERMISSION_DENIED)
                        || (grantResults[1] == PackageManager.PERMISSION_DENIED)
                ) {

                }else{
                    showPictureDialog();
                }

                break;
            default:

                break;

        }

    }
}
