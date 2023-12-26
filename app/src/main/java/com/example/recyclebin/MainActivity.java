package com.example.recyclebin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

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

        //handle sellFab click, start AdCreateActivity
        binding.sellFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(MainActivity.this, AdCreateActivity.class));
                } else {
                    Utils.toast(MainActivity.this, "Verify Account First");
                }
            }
        });
    }

    private void showHomeFragment() {
        //change toolbar textView text/title to Home
//        binding. toolbarTitleTv.setText ("Home");

        //Show HomeFragment
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "HomeFragment");
        fragmentTransaction.commit();
    }
    private void showChatsFragment(){
        //change toolbar textView text/title to Chats
//        binding. toolbarTitleTv.setText("Chats");

        //Show ChatsFragment
        ChatsFragment fragment = new ChatsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "ChatsFragment");
        fragmentTransaction.commit();
    }
    private void showMyAdsFragment(){
        //change toolbar textView text/title to My Ads
//        binding.toolbarTitleTv.setText("My Ads");

        //Show MyAdsFragment
        MyAdsFragment fragment = new MyAdsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "MyAdsFragment");
        fragmentTransaction.commit();
    }
    private void showAccountFragment(){
        //change toolbar textView text/title to Account
//        binding. toolbarTitleTv.setText ("Account");

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
* 8) Create Login Options Activity*/

/*Steps - 2
 * 1) Enable Authentication Method (Email) on Firebase
 * 2) Add Permission Internet in Android Manifest
 * 3) Create Login Register Activities
 * 4) Login UI
 * 5) Code Login Logic
 * 6) Register UI
 * 7) Code Register Logic*/

/*Steps - 3
* 01) Profile UI (fragment_account.xml)
* 02) Add Glide Library for image processing
* 03) Profile (AccountFragment.java)
* 04) Create the Activity ProfileEditActivity
* 05) Edit Profile UI (activity_profile.xml)
* 06) Edit Profile Coding (ProfileEditActivity.java)
* 07) Setup Firebase Storage. Rules: Every one can read but only signed in users can write*/

/*Steps - 4
 * 01) Verify Account UI
 * 02) Verify Account Code*/

/*Steps - 5
* 01) AdCreateActivity Create/Start
* 02) Create Ad UI
* 03) Create Ad Logic */

/*Steps - 6
* 01) Ads UI - fragment_home.xml
* 02) Ad Category UI - row_category_home.xml
* 03) Create Model (ModelCategoryHome) Class for Ad Categories List to show in RecyclerView (Horizontal)
* 04) Create Custom Adapter (AdapterCategory) Class for Ad Categories List to show in RecyclerView (Horizontal)
* 05) Create interface (RvListenerCategory) to handle Ad Category click event in Fragment (HomeFragment) instead of Adapter (AdapterCategory) Class
* 06) Show Ads UI - row_ad.xml
* 07) Create Model (ModelAd) Class for Ads List to show in RecyclerView
* 08) Create Custom Adapter (AdapterAd) Class for Ads List to show in RecyclerView
* 09) Create Filter (FilterAd) Class to search Ads
* 10) Code logic in - HomeFragment.java*/

/*Steps - 7
* 01) Check/Add/Remove the Ad to/from favorite
* 82) Create MyAdsFragment Fragment for Tabs e.g. My Ads & Favourites
* 83) My Ads Tabs - UI & Code
* 85) Create/Setup MyAdsAdsFragment & MyAdsFavoriteFragment
* 86) My Ads - UI & Code
* 08) Favorites UI & Code*/


