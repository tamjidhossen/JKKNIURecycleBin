package com.example.recyclebin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.recyclebin.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showHomeFragment();

        //handle bottomNv item clicks to navigate between fragments
        binding.bottomNv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //get id of the menu item clicked
                int itemId = item.getItemId();
                if(itemId == R.id.menu_home) {
                    //home item clicked, show fragment
                    showHomeFragment();
                    return true;
                } else if(itemId == R.id.menu_chats) {
                    //Chats item clicked, show fragment
                    showChatsFragment();
                    return true;
                } else if(itemId == R.id.menu_my_ads) {
                    //My Ads item clicked, show fragment
                    showMyAdsFragment();
                    return true;
                } else if(itemId == R.id.menu_account) {
                    //Account item clicked, show fragment
                    showAccountFragment();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void showHomeFragment() {

    }
    private void showChatsFragment() {
    }
    private void showMyAdsFragment() {
    }
    private void showAccountFragment() {

    }
}


/*Steps
* 1) Enable ViewBinding in build.gradle (optional: you may use findViewById too)
* 2) Add Required Colors in colors.xml
* 3) create menu_bottom.ml for Bottom Navigation Menu
* 4) Add Bottom Navigation Menu UI in activity_main.ml
* 5) Handle Bottom Navigation Menu item click in MainActivity.java
* 6) Create Required Fragments to show on clicking Bottom Navigation Menu
* 7) Fragment Navigation
* 8) Create Login Options Activity
* 9) Add Login Options e.g. Google, Phone, Email*/