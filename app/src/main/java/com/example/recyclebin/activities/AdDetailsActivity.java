package com.example.recyclebin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
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

public class AdDetailsActivity extends AppCompatActivity {
    // View Binding
    private ActivityAdDetailsBinding binding;

    // Log tag for logs in logcat
    private static final String TAG = "AD_DETAILS_TAG";

    // Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;

    // Ad id, will get from intent
    private String adId = "";

    // To load seller info, chat with seller, SMS, and call
    private String sellerUid = null;
    private String sellerPhone = "";

    // Hold the Ad's favorite state by the current user
    private boolean favorite = false;

    // List of Ad's images to show in the slider
    private ArrayList<ModelImageSlider> imageSliderArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init view binding... activity_ad_details.xml = ActivityAdDetailsBinding
        binding = ActivityAdDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide some UI views at the start.
        // We will show the Edit, Delete option if the user is Ad owner.
//        binding.toolbarEditBtn.setVisibility(View.GONE);
        binding.toolbarDeleteBtn.setVisibility(View.GONE);
//        binding.chatBtn.setVisibility(View.GONE);
//        binding.callBtn.setVisibility(View.GONE);
//        binding.smsBtn.setVisibility(View.GONE);

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

        // Handle toolbarBackBtn click, go back
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Handle toolbarDeleteBtn click, delete Ad
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
//        binding.toolbarEditBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editOptions();
//            }
//        });

        // Handle toolbarFavBtn click, add/remove favorite
        binding.toolbarFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorite) {
                    // This Ad is in the favorite of the current user, remove from favorite
                    Utils.removeFromFavorite(AdDetailsActivity.this, adId);
                } else {
                    // This Ad is not in the favorite of the current user, add to favorite
                    Utils.addToFavorite(AdDetailsActivity.this, adId);
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

//        binding.callBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(firebaseAuth.getCurrentUser() == null) {
//                    Utils.toast(AdDetailsActivity.this, "Login Required");
//                } else {
//                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
//                        // implement call button action
//                    } else {
//                        Utils.toast(AdDetailsActivity.this, "Verify Account First");
//                    }
//                }
//
//            }
//        });



//        binding.smsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(firebaseAuth.getCurrentUser() == null) {
//                    Utils.toast(AdDetailsActivity.this, "Login Required");
//                } else {
//                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
//                        // implement sms button action
//                    } else {
//                        Utils.toast(AdDetailsActivity.this, "Verify Account First");
//                    }
//                }
//            }
//        });


        // Method to initiate a phone call
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Utils.toast(AdDetailsActivity.this, "Login Required");
                } else {
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
                } else {
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



    }

    // Edit Option for Ads and Mark as sold
//    private void editOptions() {
//        Log.d(TAG, "editOptions: ");
//
//        // Init/setup popup menu
//        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarEditBtn);
//
//        // Add menu items to PopupMenu with params Group ID, Item ID, Order, Title
//        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Edit");
//        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Mark As Sold");
//
//        // Show popup menu
//        popupMenu.show();
//
//        // Handle popup menu item click
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                // Get id of the menu item clicked
//                int itemId = item.getItemId();
//
//                if (itemId == 0) {
//                    // Edit Clicked, start the AdCreateActivity with Ad Id and isEditMode as true
//                    Intent intent = new Intent(AdDetailsActivity.this, AdCreateActivity.class);
//                    intent.putExtra("isEditMode", true);
//                    intent.putExtra("adId", adId);
//                    startActivity(intent);
//                } else if (itemId == 1) {
//                    // Mark As Sold
//                    showMarkAsSoldDialog();
//                }
//                return true;
//            }
//        });
//    }


//    private void showMarkAsSoldDialog() {
//        // Material Alert Dialog - Setup and show
//        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
//        alertDialogBuilder.setTitle("Mark as Sold")
//                .setMessage("Are you sure you want to mark this Ad as sold?")
//                .setPositiveButton("SOLD", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(TAG, "onClick: Sold Clicked..");
//
//                        // Setup info to update in the existing Ad
//                        // Mark as sold by setting the value of status to SOLD
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("status", "" + Utils.AD_STATUS_SOLD);
//
//                        // Ad's db path to update its available/sold status. Ads > AdId
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
//                        ref.child(adId).updateChildren(hashMap)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        // Success
//                                        Log.d(TAG, "onSuccess: Marked as sold");
//                                        Utils.toast(AdDetailsActivity.this, "Marked as sold");
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        // Failure
//                                        Log.e(TAG, "onFailure: ", e);
//                                        Utils.toast(AdDetailsActivity.this,
//                                                "Failed to mark as sold due to " + e.getMessage());
//                                    }
//                                });
//                    }
//                })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(TAG, "onClick: Cancel Clicked...");
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

    private void loadAdDetails() {
        Log.d(TAG, "LoadAdDetails: ");
        // Ad's db path to get the Ad details. Ads > AdId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            // Setup model from firebase DataSnapshot
                            ModelAd modelAd = snapshot.getValue(ModelAd.class);

                            // Get data from the model
//                            sellerUid = modelAd.getUid();
                            sellerUid = snapshot.child("vid").getValue(String.class);
                            String title = modelAd.getTitle();
                            String description = modelAd.getDescription();
                            String address = modelAd.getAddress();
                            String condition = modelAd.getCondition();
                            String price = modelAd.getPrice();
                            long timestamp = modelAd.getTimestamp();

                            // Format date time e.g. timestamp to dd/MM/yyyy
                            String formattedDate = Utils.formatTimestampDate(timestamp);

                            Log.d(TAG, "Uid = " + sellerUid);

                            // Check if the Ad is by the currently signed-in user
                            if (sellerUid != null && sellerUid.equals(firebaseAuth.getUid())) {
                                // Ad is created by currently signed-in user
                                // 1) Should be able to edit and delete Ad
//                                binding.toolbarEditBtn.setVisibility(View.VISIBLE);
                                binding.toolbarDeleteBtn.setVisibility(View.VISIBLE);
                            } else {
                                // Ad is not created by currently signed-in user
                                // 1) Shouldn't be able to edit and delete Ad
//                                binding.toolbarEditBtn.setVisibility(View.GONE);
                                binding.toolbarDeleteBtn.setVisibility(View.GONE);
                            }

                            // Set data to UI Views
        //                    binding.titleTv.setText(title);
                            binding.titleTv.setText(title);
                            binding.descriptionTv.setText(description);
                            binding.addressTv.setText(address);
                            binding.conditionTv.setText(condition);
                            binding.priceTv.setText(price);
                            binding.dateTv.setText(formattedDate);

                            // Function call, load seller info e.g. profile image, name, member since
                            loadSellerDetails();

                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
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

                } catch (Exception e) {
                    Log.e(TAG, "onDataChange: ", e);
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

    // Method to load images associated with the current ad from the Firebase Realtime Database
    private void loadAdImages() {
        Log.d(TAG, "loadAdImages: ");

        // Initialize list before adding data into it
        imageSliderArrayList = new ArrayList<>();

        // Database path to load ad images: Ads > adId > Images
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
            }
        });
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

