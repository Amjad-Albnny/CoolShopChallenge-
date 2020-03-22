package com.amjadalbnny.coolshopchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amjadalbnny.coolshopchallenge.Interfaces.GetUserCredentialsInterface;
import com.amjadalbnny.coolshopchallenge.R;
import com.amjadalbnny.coolshopchallenge.managers.ServerCallsManager;
import com.amjadalbnny.coolshopchallenge.managers.SharedPreferencesManager;
import com.amjadalbnny.coolshopchallenge.utils.HelperMethods;
import com.amjadalbnny.coolshopchallenge.utils.ResponseCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.email_edit_text)
    EditText emailEditText;

    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @BindView(R.id.sign_in_button)
    TextView signInButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.warning_message_text_view)
    TextView warningMessageTextView;

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
    void signInClicked( ){

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        warningMessageTextView.setVisibility(View.INVISIBLE);

        if(checkFieldsValidation(email, password)){
            ServerCallsManager.getInstance().getUserCredentials(email, password, new GetUserCredentialsInterface() {
                @Override
                public void onComplete(int responseCode, String userId, String token) {
                    progressBar.setVisibility(View.INVISIBLE);

                    if(responseCode == ResponseCode.SUCCESS){

                        SharedPreferencesManager.getInstance().saveEmail(email);
                        SharedPreferencesManager.getInstance().saveUserID(userId);
                        SharedPreferencesManager.getInstance().saveToken(token);
                        SharedPreferencesManager.getInstance().savePassword(password);

                        openProfileActivity();
                    }
                    else if(responseCode == ResponseCode.FAILURE){
                        warningMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            warningMessageTextView.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isLoggedIn(){
        if(!SharedPreferencesManager.getInstance().getToken().isEmpty()){
            return true;
        }

        return false;
    }

    private boolean checkFieldsValidation(String email, String password){

        if(email.isEmpty() || !HelperMethods.isValidEmail(email) || password.isEmpty()){

            if(email.isEmpty() || !HelperMethods.isValidEmail(email)){
                emailEditText.setError(getResources().getString(R.string.enter_valid_email));
                emailEditText.setBackgroundResource(R.drawable.edit_text_error_border);
            }
            else
                emailEditText.setBackgroundResource(R.drawable.edit_text_normal_border);

            if (password.isEmpty()){
                passwordEditText.setError(getResources().getString(R.string.enter_valid_password));
                passwordEditText.setBackgroundResource(R.drawable.edit_text_error_border);
            }
            else
                passwordEditText.setBackgroundResource(R.drawable.edit_text_normal_border);

            return false;
        }

        return true;
    }

    private void openProfileActivity(){
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);

    }

}
