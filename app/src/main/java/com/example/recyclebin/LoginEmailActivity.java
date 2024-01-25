package com.example.recyclebin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.example.recyclebin.databinding.ActivityLoginEmailBinding;
import com.example.recyclebin.databinding.ActivityLoginOptionsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends AppCompatActivity {

    private ActivityLoginEmailBinding binding;

    private static final String TAG = "LOGIN_TAG";

    private ProgressDialog progressDialog;

    //Firebase Auth for auth related task
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_login_email.xml = ActivityLoginEmailBinding
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        // get instance of firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //init setup progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);




        // handle back button click
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        // handle's Don't have an account, Register
        // Tv = TextView
        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginEmailActivity.this, RegisterEmailActivity.class));
            }
        });


        // handles login button click
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        //handle forget password
        binding.forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
    }
    private String email, password;
    private void validateData() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString();

        Log.d(TAG, "validateData: email: "+email);
        Log.d(TAG, "validateData: password: "+password);

        // if email pattern invalid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.setError("Invalid Email");
            binding.emailEt.requestFocus();
        } else if (password.isEmpty()) {
            binding.passwordEt.setError("Enter Password");
            binding.passwordEt.requestFocus();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        //show progress
        progressDialog.setMessage("Login In");
        progressDialog.show();

        //start user login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //user login success
                        Log.d(TAG, "onSuccess: Logged In...");
                        progressDialog.dismiss();

                        //start main Activity
                        startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                        finishAffinity(); // finishes current and all activities from back stock
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // user login failed
                        Log.e(TAG, "onFailure", e);
                        Utils.toast(LoginEmailActivity.this, "Failed Due to "+e.getMessage());
                        progressDialog.dismiss();
                    }
                });

    }
        //send link for forget password
    private void showForgotPasswordDialog() {
        EditText resetEmail = new EditText(this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link.");
        passwordResetDialog.setView(resetEmail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = resetEmail.getText().toString().trim();
                sendPasswordResetEmail(mail);
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close the Dialog
            }
        });

        passwordResetDialog.create().show();
    }

    private void sendPasswordResetEmail(String email) {
        progressDialog.setMessage("Sending Reset Link...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        progressDialog.dismiss();
                        Utils.toast(LoginEmailActivity.this, "Reset Link Sent To Your Email.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Utils.toast(LoginEmailActivity.this, "Error! Reset Link is Not Sent" + e.getMessage());
                    }
                });
    }

}