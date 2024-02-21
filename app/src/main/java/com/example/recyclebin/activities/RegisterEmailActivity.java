package com.example.recyclebin.activities;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.example.recyclebin.databinding.ActivityRegisterEmailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterEmailActivity extends AppCompatActivity {

    private ActivityRegisterEmailBinding binding;

    private static final String TAG = "REGISTER_TAG";

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setStatusBarColor(R.color.Navy, R.color.Navy);


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //handles toolbar back button
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.haveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 onBackPressed();
            }
        });

        // register button click
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }


    private void setStatusBarColor(@ColorRes int lightColorRes, @ColorRes int darkColorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // Check the current theme mode
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            int colorRes = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? darkColorRes : lightColorRes;

            window.setStatusBarColor(ContextCompat.getColor(this, colorRes));

            //bottom nav bar is always Green
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.Green));
        }
    }

    private String email, password, cPassword;
    private void validateData() {
        //input data
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString();
        cPassword = binding.cPasswordEt.getText().toString();

        Log.d(TAG, "validateData: email: "+email);
        Log.d(TAG, "validateData: password: "+password);
        Log.d(TAG, "validateData: cPassword: "+cPassword);

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //email pattern is invalid, show error
            binding.emailEt.setError("Invalid Email Pattern");
            binding.emailEt.requestFocus();
        } else if (password.isEmpty()) {
            //password is not entered, show error
            binding.passwordEt.setError("Enter Password");
            binding.passwordEt.requestFocus();
        } else if (!password.equals(cPassword)) {
            //password and confirm password is not same, show error
            binding.cPasswordEt.setError("Password doesn't match");
            binding.cPasswordEt.requestFocus();
        } else {
            //all data is valid, start sign-up
            registerUser();
        }
    }

    private void registerUser() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: Register Success");
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        Utils.toast(RegisterEmailActivity.this, "Failed due to "+e.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateUserInfo() {

        String registerUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String registerUserUid = firebaseAuth.getUid();

        // initializes db with these fields
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "");
        hashMap.put ("phoneNumber", "");
        hashMap.put ("dept", "");
        hashMap.put ("session", "");
        hashMap.put("profileImageUrl", "");
        hashMap.put("userType", "Email");
        hashMap.put("typingTo", "");
        hashMap.put("onlineStatus", true);
        hashMap.put("email", registerUserEmail);
        hashMap.put("uid", registerUserUid);

        //Save to firebase DB
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(registerUserUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // firebase db saved successful
                        Log.d(TAG, "onSuccess: Info Saved...");

                        startActivity(new Intent(RegisterEmailActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // firebase db save failed
                        Log.e(TAG, "onFailure: ", e);

                        Utils.toast(RegisterEmailActivity.this, "Failed to save info due to "+e.getMessage());
                    }
                });
    }
}