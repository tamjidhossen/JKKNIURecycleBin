package com.example.recyclebin.activities;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.recyclebin.fragments.ChatsFragment;
import com.example.recyclebin.fragments.HomeFragment;
import com.example.recyclebin.fragments.MyAdsFragment;
import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.example.recyclebin.databinding.ActivityMainBinding;
import com.example.recyclebin.fragments.AccountFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

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
                } /*else if(itemId == R.id.menu_chats) {
                    //Chats item clicked, show fragment
                    if(firebaseAuth.getCurrentUser() == null) {
                        Utils.toast(MainActivity.this, "Login Required");
                        startLoginOptions();
                        return false;
                    } else {
                        showChatsFragment();
                        return true;
                    }

                }*/ else if(itemId == R.id.menu_my_ads) {
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

                } else if(itemId == R.id.menu_logout) {
                    if(firebaseAuth.getCurrentUser() == null) {
                        // Already logged out
                        return false;
                    } else {
                        logoutPopup();
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
                if(firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(MainActivity.this, "Login Required");
                    startLoginOptions();
                } else {
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, AdCreateActivity.class));
                        // Below is for Edit button
//                    Intent intent = new Intent(MainActivity.this, AdCreateActivity.class);
//                    intent.putExtra("isEditMode", false);
//                    startActivity(intent);
                    } else {
                        Utils.toast(MainActivity.this, "Verify Account First");
                    }
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

        // Set status bar color
        setStatusBarColor(R.color.white, R.color.colorGray03);

    }
//    private void showChatsFragment(){
//        //change toolbar textView text/title to Chats
//      binding. toolbarTitleTv.setText("Chats");
//
//        //Show ChatsFragment
//        ChatsFragment fragment = new ChatsFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "ChatsFragment");
//        fragmentTransaction.commit();
//    }
    private void showMyAdsFragment(){
        //change toolbar textView text/title to My Ads
//        binding.toolbarTitleTv.setText("My Ads");

        //Show MyAdsFragment
        MyAdsFragment fragment = new MyAdsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "MyAdsFragment");
        fragmentTransaction.commit();

        // Set status bar color
        setStatusBarColor(R.color.DarkGreen, R.color.DarkGreen);
    }
    private void showAccountFragment(){
        //change toolbar textView text/title to Account
//        binding. toolbarTitleTv.setText ("Account");

        //Show AccountFragment
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "AccountFragment");
        fragmentTransaction.commit();

        // Set status bar color
        setStatusBarColor(R.color.white, R.color.colorGray03);
    }

    private void logoutPopup(){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
        materialAlertDialogBuilder.setTitle("Log Out")
                .setMessage("Are you sure you want to Log Out?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete Clicked, delete Ad
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel Clicked, dismiss dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void startLoginOptions() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }

    private void setStatusBarColor(@ColorRes int lightColorRes, @ColorRes int darkColorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // Check the current theme mode
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            int colorRes = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? darkColorRes : lightColorRes;

            window.setStatusBarColor(ContextCompat.getColor(this, colorRes));

            //bottom nav bar is always Dark id dark mode on
            if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorGray03));
            }
        }
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

/*Steps - 8
* 01) Create/Start AdDetailsActivity
* 02) Ad Details - UI
* 03) Load the Ad Details
        03.1) row_image_slider
        03.2) ModelImageSlider
        03.3) AdapterImageSlider
* 04) Load the Seller Info*/

/*Steps - 9
* 01) Edit Ad button click handle
* 02) Mark as sold*/