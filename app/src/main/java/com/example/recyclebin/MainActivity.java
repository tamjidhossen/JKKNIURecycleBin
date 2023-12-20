package com.example.recyclebin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.recyclebin.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.internal.Util;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    //Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super. onCreate(savedInstanceState);
        //activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get instance of firebase auth for Auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();
        //check if user is logged in or not
        if (firebaseAuth.getCurrentUser() == null){
            //user is not logged in, move to LoginOptionsActivity
            startLoginOptions();
        }

        // By default (When App Opens) show Home
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
                    if(firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this, "Login Required");
                        startLoginOptions();

                        return false;
                    } else {
                        showChatsFragment();
                        return true;
                    }

                } else if(itemId == R.id.menu_my_ads) {
                    //My Ads item clicked, show fragment
                    if(firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this, "Login Required");
                        startLoginOptions();

                        return false;
                    } else {
                        showMyAdsFragment();
                        return true;
                    }

                } else if(itemId == R.id.menu_account) {
                    //Account item clicked, show fragment
                    if(firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this, "Login Required");
                        startLoginOptions();

                        return false;
                    } else {
                        showAccountFragment();
                        return true;
                    }

                } else {
                    return false;
                }
            }
        });
    }

    private void showHomeFragment() {
        //change toolbar textView text/title to Home
        binding. toolbarTitleTv.setText ("Home");

        //Show HomeFragment
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "HomeFragment");
        fragmentTransaction.commit();
    }
    private void showChatsFragment(){
        //change toolbar textView text/title to Chats
        binding. toolbarTitleTv.setText("Chats");

        //Show ChatsFragment
        ChatsFragment fragment = new ChatsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "ChatsFragment");
        fragmentTransaction.commit();
    }
    private void showMyAdsFragment(){
        //change toolbar textView text/title to My Ads
        binding.toolbarTitleTv.setText("My Ads");

        //Show MyAdsFragment
        MyAdsFragment fragment = new MyAdsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "MyAdsFragment");
        fragmentTransaction.commit();
    }
    private void showAccountFragment(){
        //change toolbar textView text/title to Account
        binding. toolbarTitleTv.setText ("Account");

        //Show AccountFragment
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "AccountFragment");
        fragmentTransaction.commit();
    }


    private void startLoginOptions() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }

}


/*Steps - 1
* 1) Enable ViewBinding in build.gradle (optional: you may use findViewById too)
* 2) Add Required Colors in colors.xml
* 3) create menu_bottom.xml for Bottom Navigation Menu
* 4) Add Bottom Navigation Menu UI in activity_main.xml
* 5) Handle Bottom Navigation Menu item click in MainActivity.java
* 6) Create Required Fragments to show on clicking Bottom Navigation Menu
* 7) Fragment Navigation
* 8) Create Login Options Activity
* 9) Add Login Options e.g. Google, Phone, Email*/

/*Steps - 2
 * 1) Enable Authentication Methods (Email) on Firebase
 * 2) Add Permission Internet in Android Manifest
 * 3) Create Login Register Activities
 * 4) Login UI
 * 5) Code Login Logic
 * 6) Register UI
 * 7) Code Register Logic*/