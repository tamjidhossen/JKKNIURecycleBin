package com.example.recyclebin.activities;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.recyclebin.adapters.AdapterAd;
import com.example.recyclebin.adapters.AdapterReport;
import com.example.recyclebin.models.ModelReport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.example.recyclebin.adapters.AdapterImageSlider;
import com.example.recyclebin.databinding.ActivityAdDetailsBinding;
import com.example.recyclebin.models.ModelAd;
import com.example.recyclebin.models.ModelImageSlider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class AdDetailsActivity extends AppCompatActivity {
    // View Binding
    private ActivityAdDetailsBinding binding;
    private TextView soldStatusTv;
    private TextView favoriteCountTv;//+++++++++++++++++++++++++++++++++

    // Log tag for logs in logcat
    private static final String TAG = "AD_DETAILS_TAG";

    //Context for this fragment class
    private Context mContext;

    // Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;
    DatabaseReference reportRef;
    boolean sold = false;

    // Ad id, will get from intent
    private String adId = "";

    // To load seller info, chat with seller, SMS, and call
    private String sellerUid = null;
    private String sellerPhone = "";

    // Hold the Ad's favorite state by the current user
    private boolean favorite = false;

    // List of Ad's images to show in the slider
    private ArrayList<ModelImageSlider> imageSliderArrayList;

    //reportsArrayList to hold ads list to show in RecyclerView
    private ArrayList<ModelReport> reportsArrayList;
    //AdapterAd class instance to set to Recyclerview to show Ads list
    private AdapterReport adapterReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init view binding... activity_ad_details.xml = ActivityAdDetailsBinding
        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // so it does not blend with DarkGreen Background
        binding.toolbarFavBtn.setColorFilter(R.color.black);

        setStatusBarColor(R.color.DarkGreen, R.color.DarkGreen);

        soldStatusTv = findViewById(R.id.soldStatusTv);
        favoriteCountTv = findViewById(R.id.favoriteCountTv);//++++++++++++++++++++++++++++


        // Hide some UI views at the start.
        // We will show the Edit, Delete option if the user is Ad owner.
        binding.toolbarEditBtn.setVisibility(View.GONE);
        binding.toolbarDeleteBtn.setVisibility(View.GONE);

        // Get the id of the Ad (as passed in AdapterAd class while starting this activity)
        adId = getIntent().getStringExtra("adId");
        Log.d(TAG, "onCreate: adId: " + adId);

        // Firebase Auth for auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();

        // If the user is logged in, then check if the Ad is in favorites of the user
        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite();
        }

        // Load Ad details, Ad images, and handle toolbar button clicks
        loadAdDetails();
        loadAdImages();

        binding.reportsTextTv.setVisibility(View.GONE);
        // Enable Delete Options, Report Viewing
        if (firebaseAuth.getCurrentUser() != null) {
            binding.reportsTextTv.setVisibility(View.VISIBLE);
            optionsForAdmin();
        }


        // Handle toolbarBackBtn click, go back
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        //Handle toolbarDeleteBtn click, delete Ad
        binding.toolbarDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alert dialog to confirm if the user really wants to delete the Ad
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(AdDetailsActivity.this);
                materialAlertDialogBuilder.setTitle("Delete Ad")
                        .setMessage("Are you sure you want to delete this Ad?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete Clicked, delete Ad
                                deleteAd();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel Clicked, dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        // Handle toolbarEditBtn click, start AdCreateActivity to edit this Ad
        binding.toolbarEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOptions();
            }
        });

        // Handle toolbarFavBtn click, add/remove favorite
        binding.toolbarFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorite) {
                    // This Ad is in the favorite of the current user, remove from favorite
                    //Utils.removeFromFavorite(AdDetailsActivity.this, adId);
                    Utils.removeFromFavorite(AdDetailsActivity.this, adId, favoriteCountTv); // Pass the TextView reference++++++++++++++
                } else {
                    // This Ad is not in the favorite of the current user, add to favorite
                    //Utils.addToFavorite(AdDetailsActivity.this, adId);
                    Utils.addToFavorite(AdDetailsActivity.this, adId, favoriteCountTv); // Pass the TextView reference++++++++++++++++++
                }
            }
        });

        // Handle sellerProfileCv click, start SellerProfileActivity
        binding.sellerProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SellerProfileActivity
                if(firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(AdDetailsActivity.this, "Login Required");
                } else {
                    Intent intent = new Intent(AdDetailsActivity.this, AdSellerProfileActivity.class);
                    intent.putExtra("sellerUid", sellerUid);
                    startActivity(intent);
                }
            }
        });


        // Method to initiate a phone call
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(AdDetailsActivity.this, "Login Required");
                }
                else if ("Sold".equals(binding.soldStatusTv.getText().toString())) {
                    Utils.toast(AdDetailsActivity.this, "Item Sold");
                }
                else {
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        // Get the seller's phone number
                        if (sellerPhone != null && !sellerPhone.isEmpty()) {
                            // Intent to initiate a call
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + sellerPhone));

                            // Check if there is an app to handle the intent
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                // Start the activity to make a call
                                startActivity(intent);
                            } else {
                                Utils.toast(AdDetailsActivity.this, "No app to handle calls");
                            }
                        } else {
                            Utils.toast(AdDetailsActivity.this, "Seller's phone number not available");
                        }
                    } else {
                        Utils.toast(AdDetailsActivity.this, "Verify Account First");
                    }
                }
            }
        });


        // Handle smsBtn click, make a sms
        binding.smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(AdDetailsActivity.this, "Login Required");
                }
                else if ("Sold".equals(binding.soldStatusTv.getText().toString())) {
                    Utils.toast(AdDetailsActivity.this, "Item Sold");
                }else {
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        // Get the seller's phone number
                        if (sellerPhone != null && !sellerPhone.isEmpty()) {
                            // Intent to send an SMS
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", sellerPhone, null));
                            intent.putExtra("sms_body", "Hello, I'm interested in your ad. Can you provide more information?");

                            // Check if there is an app to handle the intent
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                // Start the activity to send an SMS
                                startActivity(intent);
                            } else {
                                Utils.toast(AdDetailsActivity.this, "No app to handle SMS");
                            }
                        } else {
                            Utils.toast(AdDetailsActivity.this, "Seller's phone number not available");
                        }
                    } else {
                        Utils.toast(AdDetailsActivity.this, "Verify Account First");
                    }
                }
            }
        });


        // report ad button
        binding.reportThisAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportDialog();
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

            //bottom nav bar is always Dark id dark mode on
            if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorGray03));
            }
        }
    }


    private void showReportDialog() {
        if(firebaseAuth.getCurrentUser() == null) {
            Utils.toast(AdDetailsActivity.this, "Login Required");
        } else {
            EditText reportEditText = new EditText(this);
            reportEditText.setHint("Enter your report here");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Report This Ad");
            builder.setView(reportEditText);
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String reportText = reportEditText.getText().toString().trim();

                    if (!TextUtils.isEmpty(reportText)) {
                        // Here, you can do something with the reportText, userName, and adId
                        // For example, send the report to a server
                        sendReportToDatabase(reportText);
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }


    private void sendReportToDatabase(String reportText) {

        //firebase database Ads reference to store the report in "Ads/ reports"
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads").child(adId).child("Reports");

        //Reference of current user info in Firebase Realtime Database to get user name
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String reportersName = "" + snapshot.child("name").getValue();
                String reportersProfileImageUrl = "" + snapshot.child("profileImageUrl").getValue();

//                String randomReportKey = firebaseAuth.getUid() + "" + new Random().nextInt(1000);
                String randomReportKey = refAds.push().getKey();

                //setup data to add in firebase database
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("reportersUid", "" + firebaseAuth.getUid());
                hashMap.put("reportersName", "" + reportersName);
                hashMap.put("reportersProfileImageUrl", "" + reportersProfileImageUrl);
                hashMap.put("report", "" + reportText);
                hashMap.put("reportId", "" + randomReportKey);
                hashMap.put("adId", "" + adId);

                Log.d(TAG, "Report Text: " + reportText);
                Log.d(TAG, "User Name: " + reportersName);
                Log.d(TAG, "Ad ID: " + adId);

                //set data to firebase database. Ads -> AdId -> Reports
                refAds.child(randomReportKey)
                        .updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Utils.toast(AdDetailsActivity.this, "Report Submitted");
                                Log.d(TAG, "onSuccess: Report Added");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ", e);
                                Utils.toast(AdDetailsActivity.this, "Report Submission Failed "+e.getMessage());
                            }
                        });

            }

            @Override

            public void onCancelled(@NonNull DatabaseError error) {


            }

        });

    }

    // Edit Option for Ads and Mark as sold
    private void editOptions() {
        Log.d(TAG, "editOptions: ");

        // Init/setup popup menu
        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarEditBtn);

        // Add menu items to PopupMenu with params Group ID, Item ID, Order, Title
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Edit");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Mark As Sold");

        // Show popup menu
        popupMenu.show();

        // Handle popup menu item click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Get id of the menu item clicked
                int itemId = item.getItemId();

                if (itemId == 0) {
                    // Edit Clicked, start the AdCreateActivity with Ad Id and isEditMode as true
                    Intent intent = new Intent(AdDetailsActivity.this, AdCreateActivity.class);
                    intent.putExtra("isEditMode", true);
                    intent.putExtra("adId", adId);
                    startActivity(intent);
                } else if (itemId == 1) {
                    // Mark As Sold
                    showMarkAsSoldDialog();
                }
                return true;
            }
        });
    }

    private void showMarkAsSoldDialog() {
        // Material Alert Dialog - Setup and show
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Mark as Sold")
                .setMessage("Are you sure you want to mark this Ad as sold?")
                .setPositiveButton("SOLD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Sold Clicked..");

                        // Setup info to update in the existing Ad
                        // Mark as sold by setting the value of status to SOLD
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "" + Utils.AD_STATUS_SOLD);

                        // Ad's db path to update its available/sold status. Ads > AdId
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                        ref.child(adId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Success
                                        Log.d(TAG, "onSuccess: Marked as sold");
                                        Utils.toast(AdDetailsActivity.this, "Marked as sold");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failure
                                        Log.e(TAG, "onFailure: ", e);
                                        Utils.toast(AdDetailsActivity.this,
                                                "Failed to mark as sold due to " + e.getMessage());
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Cancel Clicked...");
                        dialog.dismiss();
                    }
                })
                .show();

    }


    private void loadAdDetails() {
        Log.d(TAG, "LoadAdDetails: ");
        // Ad's db path to get the Ad details. Ads > AdId
        Log.d(TAG, "adid here "+adId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            // Setup model from firebase DataSnapshot
                            ModelAd modelAd = snapshot.getValue(ModelAd.class);

                            // Get data from the model
                            //sellerUid = modelAd.getUid();
                            sellerUid = snapshot.child("vid").getValue(String.class);
                            String title = modelAd.getTitle();
                            String description = modelAd.getDescription();
                            String address = modelAd.getAddress();
                            String condition = modelAd.getCondition();
                            String price = modelAd.getPrice();
                            long timestamp = modelAd.getTimestamp();
                            String isSold = modelAd.getStatus();

                            Log.d(TAG, "IS_SOLD = " + isSold);

                            // Format date time e.g. timestamp to dd/MM/yyyy
                            String formattedDate = Utils.formatTimestampDate(timestamp);

                            Log.d(TAG, "Uid = " + sellerUid);

                            //if item sold already
                            if(Objects.equals(isSold, Utils.AD_STATUS_SOLD)) {
                                binding.soldStatusTv.setText("Sold");
                                int soldTextColor = ContextCompat.getColor(AdDetailsActivity.this, R.color.Red);
                                binding.soldStatusTv.setTextColor(soldTextColor); // Set color from colors.xml
                            }

                            if(firebaseAuth.getCurrentUser() != null) {
                                DatabaseReference refAdmin = FirebaseDatabase.getInstance().getReference ("Users");
                                refAdmin.child(firebaseAuth.getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.child("isAdmin").exists() == true) {
                                                    binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
                                                } else if(sellerUid != null && sellerUid.equals(firebaseAuth.getUid()) && Objects.equals(isSold, Utils.AD_STATUS_AVAILABLE)){
                                                    binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
                                                } else {
                                                    binding.toolbarDeleteBtn.setVisibility(View.GONE);
                                                    binding.toolbarEditBtn.setVisibility(View.GONE);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                // Check if the Ad is by the currently signed-in user
                                if (sellerUid != null && sellerUid.equals(firebaseAuth.getUid())) {
                                    // Ad is created by currently signed-in user
                                    // 1) Should be able to edit and delete Ad
                                    binding.toolbarEditBtn.setVisibility(View.VISIBLE);
                                } else {
                                    // Ad is not created by currently signed-in user
                                    // 1) Shouldn't be able to edit and delete Ad
                                    binding.toolbarEditBtn.setVisibility(View.GONE);

                                }
                            }


                            // Set data to UI Views
                            binding.titleTv.setText(title);
                            binding.descriptionTv.setText(description);
                            binding.addressTv.setText(address);
                            binding.conditionTv.setText(condition);
                            binding.priceTv.setText(price);
                            binding.dateTv.setText(formattedDate);


                            // Function call, load seller info e.g. profile image, name, member since
                            loadSellerDetails();

                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange_LoadAd: ", e);
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    // Method to load details of the seller from the Firebase Realtime Database
    private void loadSellerDetails() {
        Log.d(TAG, "loadSellerDetails: ");

        // Database path to load seller information: Users > sellerUid
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(sellerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // Extract data from the snapshot
                    String phoneNumber = "" + snapshot.child("phoneNumber").getValue();
                    String name = "" + snapshot.child("name").getValue();
                    String profileImageUrl = "" + snapshot.child("profileImageUrl").getValue();
                    String dept = "" + snapshot.child("dept").getValue();


                    // Combine phone code and phone number to get the seller's phone
                    sellerPhone = phoneNumber;

                    // Set data to UI Views
                    binding.sellerNameTv.setText(name);
                    binding.deptTv.setText(dept);

                    // Load seller's profile image using Glide
                    Glide.with(AdDetailsActivity.this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_white)
                            .into(binding.sellerProfileIv);

                    // Load seller's profile image using Glide only if the activity is valid
//                    if (!isDestroyed() && !isFinishing()) {
//                        Glide.with(AdDetailsActivity.this)
//                                .load(profileImageUrl)
//                                .placeholder(R.drawable.ic_person_white)
//                                .into(binding.sellerProfileIv);
//                    }

                    Log.d(TAG, "Image Set");

                } catch (Exception e) {
                    Log.e(TAG, "onDataChange_LoadSeller: ", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    //Options for Admin
    private void optionsForAdmin() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference ("Users");

        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("isAdmin").exists() == false) {
                            binding.reportsTextTv.setVisibility(View.GONE);
                            binding.reportsRv.setVisibility(View.GONE);
                        } else {
                            binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
                            loadReports();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    // Method to check if the ad is marked as a favorite by the user
    private void checkIsFavorite() {
        Log.d(TAG, "checkIsFavorite: ");

        // Database path to check if the ad is in the user's favorites: Users > userId > Favorites > adId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // If snapshot exists (value is true), the ad is in the user's favorites
                        favorite = snapshot.exists();
                        Log.d(TAG, "onDataChange: favorite: " + favorite);

                        // Update the UI based on whether the ad is a favorite or not
                        if (favorite) {
                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_yes);
                        } else {
                            binding.toolbarFavBtn.setImageResource(R.drawable.ic_fav_no_white);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    private void loadReports() {
        Log.d(TAG, "LoadReports: ");

        // init before adding data
        reportsArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads").child(adId).child("Reports");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reportsArrayList.clear();

                // load reports
                for (DataSnapshot ds: snapshot.getChildren()) {
                    //prepare ModelReport with all reports from firebase
                    ModelReport modelReport = ds.getValue(ModelReport.class);

                    reportsArrayList.add(modelReport);
                }
                adapterReport = new AdapterReport(AdDetailsActivity.this, reportsArrayList);
                binding.reportsRv.setAdapter(adapterReport);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Method to load images associated with the current ad from the Firebase Realtime Database
    private void loadAdImages() {
        Log.d(TAG, "loadAdImages: ");

        // Initialize list before adding data into it
        imageSliderArrayList = new ArrayList<>();

        // Database path to load ad images: Ads > adId > Images
        if (adId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
            ref.child(adId).child("Images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Clear the list before adding data into it
                    imageSliderArrayList.clear();

                    // Loop through each child snapshot to load all images
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        // Prepare model (spellings in model class should be the same as in Firebase)
                        ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);

                        // Add the prepared model to the list
                        imageSliderArrayList.add(modelImageSlider);
                    }

                    // Setup adapter and set it to the ViewPager (imageSliderVp)
                    AdapterImageSlider adapterImageSlider = new AdapterImageSlider(
                            AdDetailsActivity.this, imageSliderArrayList);
                    binding.imageSliderVp.setAdapter(adapterImageSlider);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event
                }
            });
        } else {
            Log.e(TAG, "Ad ID is null");
        }

    }

    // Method to delete the current ad
    private void deleteAd() {
        Log.d(TAG, "deleteAd: ");

        // Database path to delete the ad: Ads > adId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Success: Display a message and finish the activity
                Log.d(TAG, "onSuccess: Deleted");
                Utils.toast(AdDetailsActivity.this, "Deleted");
                //finish activity and go - back
                finish();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failure: Display an error message
                Log.e(TAG, "onFailure: ", e);
                Utils.toast(AdDetailsActivity.this,
                        "Failed to delete due to " + e.getMessage());
            }
        });
    }
}

