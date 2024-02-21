package com.example.recyclebin.activities;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.recyclebin.R;
import com.example.recyclebin.databinding.ActivityLoginOptionsBinding;

public class LoginOptionsActivity extends AppCompatActivity{
    private ActivityLoginOptionsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_login_options.xml = ActivityLoginOptionsBinding
        binding = ActivityLoginOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setStatusBarColor(R.color.Navy, R.color.Navy);

        //handle closeBtn click, go back
        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle loginEmailBtn click
        binding.loginEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginOptionsActivity.this, LoginEmailActivity.class));
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

            //bottom nav bar is always Navy
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.Navy));
        }
    }
}