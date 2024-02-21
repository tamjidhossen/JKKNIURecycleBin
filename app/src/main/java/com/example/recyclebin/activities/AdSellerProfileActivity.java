package com.example.recyclebin.activities;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.recyclebin.R;
import com.example.recyclebin.databinding.ActivityAdSellerProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdSellerProfileActivity extends AppCompatActivity {

    private String sellerUid = "";

    private static final String TAG = "AD_SELLER_PROFILE_TAG";

    private ActivityAdSellerProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdSellerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setStatusBarColor(R.color.DarkGreen, R.color.DarkGreen);


        sellerUid = getIntent().getStringExtra("sellerUid");
        Log.d(TAG, "onCreate: sellerUid: " + sellerUid);

        loadSellerDetails();

        binding.sellerInfoPageCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

            //bottom nav bar is always Dark Green
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.DarkGreen));
        }
    }



    private void loadSellerDetails() {
        Log.d(TAG, "loadSellerDetails: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sellerUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get user info, spelling should be same in firebase realtime database
                        String name = ""+ snapshot.child("name").getValue();
                        String dept = ""+ snapshot.child("dept").getValue();
                        String session = ""+ snapshot.child("session").getValue();
                        String profileImageUrl = ""+ snapshot.child("profileImageUrl").getValue();

                        //set data to UI
                        binding.sellerNameTv.setText(name);
                        binding.sellerDeptTv.setText(dept);
                        binding.sellerSessionTv.setText(session);
                        // Seller always verified, because you cant post Ad without verification :)
                        binding.sellerVerificationTv.setText("Verified");
                        binding.sellerVerificationTv.setTextColor(getResources().getColor(R.color.DarkGreen));

                        try {
                            // set profile image to profileIV
                            Glide.with(AdSellerProfileActivity.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.sellerImageIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}