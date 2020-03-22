package com.amjadalbnny.coolshopchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amjadalbnny.coolshopchallenge.ImagesManager;
import com.amjadalbnny.coolshopchallenge.Interfaces.GetAvatarUrlCompletion;
import com.amjadalbnny.coolshopchallenge.Interfaces.GetUserProfileCompletion;
import com.amjadalbnny.coolshopchallenge.R;
import com.amjadalbnny.coolshopchallenge.managers.ServerCallsManager;
import com.amjadalbnny.coolshopchallenge.managers.SharedPreferencesManager;
import com.amjadalbnny.coolshopchallenge.utils.HelperMethods;
import com.amjadalbnny.coolshopchallenge.utils.ResponseCode;
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
    TextView signOutButton;

    @BindView(R.id.filter_button)
    TextView filterButton;

    private final int GALLERY = 1, CAMERA = 2;
    private final int REQUEST_ALL_PERMISSIONS = 100;

    private final static String READ_EXTERNAL_STORAGE_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE";
    private final static String CAMERA_PERMISSION = "android.permission.CAMERA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        initContent();

    }

    @Override
    protected void onResume(){
        super.onResume();

        enableAddFilterButton();
    }

    private void initContent(){

        ServerCallsManager.getInstance().getUserProfile(SharedPreferencesManager.getInstance().getUserID(), new GetUserProfileCompletion() {
            @Override
            public void onComplete(int responseCode, String email, String avatarUrl) {

                if(responseCode == ResponseCode.SUCCESS){

                    SharedPreferencesManager.getInstance().saveEmail(email);
                    SharedPreferencesManager.getInstance().saveAvatarUrl(avatarUrl);

                    Picasso.get().load(avatarUrl).into(profileImageView);

                    enableAddFilterButton();
                }

            }
        });

        emailTextView.setText(SharedPreferencesManager.getInstance().getEmail());
        passwordTextView.setText(getResources().getString(R.string.password) + " : " + SharedPreferencesManager.getInstance().getPassword());
    }

    @OnClick(R.id.profile_image_view)
    void profilePhotoClicked(){
        checkPermissionsThenUploadImage();
    }


    @OnClick(R.id.sign_out_button)
    void signOutClicked(){

        SharedPreferencesManager.getInstance().clearAll();
        if(!isTaskRoot())
            onBackPressed();
        else{
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @OnClick(R.id.filter_button)
    void filterClicked(){

        Bitmap bitmap = null;

        try {
            bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
        }
        catch (Exception ex){

        }

        if(bitmap != null){
            bitmap = ImagesManager.applyFilter(bitmap);
            profileImageView.setImageBitmap(bitmap);
        }

    }

    private void enableAddFilterButton(){
        if(SharedPreferencesManager.getInstance().getAvatarUrl().isEmpty()){
            filterButton.setEnabled(false);
            filterButton.setAlpha(0.8f);
        }
        else{
            filterButton.setEnabled(true);
            filterButton.setAlpha(1.0f);
        }
    }

    private void updateProfilePhoto(Bitmap bitmap){
        profileImageView.setImageBitmap(bitmap);

        ServerCallsManager.getInstance().getAvatarUrl(HelperMethods.BitMapToString(bitmap), new GetAvatarUrlCompletion() {
            @Override
            public void onComplete(int responseCode, String avatarUrl) {

                if(responseCode == ResponseCode.SUCCESS){
                    SharedPreferencesManager.getInstance().saveAvatarUrl(avatarUrl);
                }

            }
        });
    }

    private void checkPermissionsThenUploadImage(){

        String[] permissions = {
                READ_EXTERNAL_STORAGE_PERMISSION,
                CAMERA_PERMISSION
        };

        int CAMERA_permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int READ_EXTERNAL_STORAGE_permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if ((READ_EXTERNAL_STORAGE_permissionCheck != PackageManager.PERMISSION_GRANTED)
                ||(CAMERA_permissionCheck != PackageManager.PERMISSION_GRANTED) )
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ALL_PERMISSIONS);
        else{
            showUploadPictureDialog();
        }

    }

    private void showUploadPictureDialog(){
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

    private int getSquareCropDimensionForBitmap(Bitmap bitmap)
    {
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
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

        updateProfilePhoto(bitmap);

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
                    showUploadPictureDialog();
                }

                break;
            default:

                break;

        }

    }


}
