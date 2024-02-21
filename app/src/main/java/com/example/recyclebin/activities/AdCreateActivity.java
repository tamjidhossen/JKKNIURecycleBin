package com.example.recyclebin.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.example.recyclebin.adapters.AdapterImageSlider;
import com.example.recyclebin.fragments.HomeFragment;
import com.example.recyclebin.models.ModelImagePicked;
import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.example.recyclebin.adapters.AdapterImagesPicked;
import com.example.recyclebin.models.ModelImageSlider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.recyclebin.databinding.ActivityAdCreateBinding;

import java.util.ArrayList;
import java.util. HashMap;
import java.util.Map;

public class AdCreateActivity extends AppCompatActivity {

    //View Binding
    private ActivityAdCreateBinding binding;
    //TAG for logs in logcat
    private static final String TAG = "AD_CREATE_TAG";
    //ProgressDialog to show while adding/updating the Ad
    private ProgressDialog progressDialog;
    //Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;
    //Image Uri to hold uri of the image (picked/captured using Gallery/Camera) to add in Ad Images List
    private Uri imageUri = null;

    private boolean isEditMode = false;
    private String adIdForEditing = "";

    //list of images (picked/captured using Gallery/Camera or from internet)
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagesPicked adapterImagesPicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init view binding... activity_ad_create.xml = ActivityAdCreateBinding
        binding = ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setStatusBarColor(R.color.white, R.color.colorGray03);

        //init/setup ProgressDialog to show while adding/updating the Ad
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //Firebase Auth for auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();

        //Setup and set the categories adapter to the Category Input Filed i.e. categoryAct
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this, R.layout.row_category_act, Utils.categories);
        binding.categoryAct.setAdapter(adapterCategories);

        //Setup and set the conditions adapter to the Condition Input Filed i.e. conditionAct
        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this, R.layout.row_condition_act, Utils.conditions);
        binding.conditionAct.setAdapter(adapterConditions);

        //Setup and set the Location adapter to the Location Input Filed i.e. addressAct
        ArrayAdapter<String> adapterLocation = new ArrayAdapter<>(this, R.layout.row_location_act, Utils.address);
        binding.locationAct.setAdapter(adapterLocation);

        //init imagePickedArrayList
        imagePickedArrayList = new ArrayList<>();

        Intent intent = getIntent(); // Added parentheses to the method call
        isEditMode = intent.getBooleanExtra("isEditMode", false); // Removed colon, fixed method call
        Log.d(TAG, "onCreate: isEditMode: " + isEditMode); // Removed colon

        if (isEditMode) {
            // Edit Ad Model: Get the Ad Id for editing the Ad
            adIdForEditing = intent.getStringExtra("adId");
            // Function call to load Ad details by using Ad Id
            loadAdDetails();

            // Change toolbar title and submit button text
            binding.toolbarTitleTv.setText("Update Ad");
            binding.postAdBtn.setText("Update Ad");
        } else {
            // New Ad Mode: Change toolbar title and submit button text
            binding.toolbarTitleTv.setText("Create Ad");
            binding.postAdBtn.setText("Post Ad");
        }


        //loadImages
        loadImages();

        //handle toolbarBackBtn click, go-back
        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle toolbarAddImageBtn click, show image add options (Gallery/Camera)
        binding. toolbarAddImageBtn.setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOptions();
            }
        });
        binding.postAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
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

            //bottom nav bar is always Dark id dark mode on
            if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorGray03));
            }
        }
    }

    private void loadImages(){
        Log.d(TAG,"loadImages: ");
        //init setup adapterImagesPicked to set it RecyclerView i.e. imagesRv. Param 1 is Context, Param 2 is Images List to show in RecyclerView
        adapterImagesPicked = new AdapterImagesPicked(this, imagePickedArrayList);
        //set the adapter to the RecyclerView i.e. imagesRv
        binding.imagesRv.setAdapter (adapterImagesPicked);
    }

    private void showImagePickOptions() {
        Log.d(TAG, "showImagePickOptions: ");
        //init the PopupMenu. Param 1 is context. Param 2 is Anchor view for this popup. The popup will appear below the anchor if there is room, or above it if there is not enough room
        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarAddImageBtn);

        //add menu items to our popup menu Param#1 is GroupID, Param#2 is ItemID, Param#3 is OrderID, Param#4 is Menu Item Title
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");
        //Show Popup Menu
        popupMenu.show();

        //handle popup menu item click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //get the id of the item clicked in popup menu
                int itemId = item.getItemId();
                //check which item id is clicked from popup menu. 1=Camera. 2=Gallery as we defined
                if (itemId == 1) {
                    //Camera is clicked we need to check if we have permission of Camera, Storage before Launching Camera to Capture image
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //Device version is TIRAMISU or above. We only need Camera permission
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermission.launch(cameraPermissions);
                    } else {
                        //Device version is below TIRAMISU. We need Camera & Storage permissions
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermission.launch(cameraPermissions);
                    }
                } else if (itemId == 2) {
                    //Gallery is clicked we need to check if we have permission of Storage before launching Gallery to Pick image
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //Device version is TIRAMISU or above. We don't need Storage permission to launch Gallery
                        pickImageGallery();
                    } else {
                        //Device version is below TIRAMISU. We need Storage permission to launch Gallery
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermission);
                    }
                }
                return true;
            }
        });
    }

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG,"onActivityResult: isGranted: "+isGranted);
                    if (isGranted) {
                        //launch gallery to pick image
                        pickImageGallery();
                    } else {
                        //Storage permission denied
                        Utils.toast(AdCreateActivity.this, "Storage permission denied!");
                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: ");
                    Log.d(TAG, "onActivityResult: "+result.toString());

                    //lets check if permission are granted or not
                    boolean areAllGranted = true;
                    for (Boolean isGranted: result.values()) {
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if(areAllGranted) {
                        Log.d(TAG, "onActivityResult: All Granted");
                        pickImageCamera();
                    } else {
                        //permission denied
                        Log.d(TAG, "onActivityResult: All or either one is denied");
                        Utils.toast(AdCreateActivity.this, "Camera or Storage or both permission denied");
                    }
                }
            }
    );
    private void pickImageCamera(){
        Log.d(TAG,"pickImageCamera: ");
        //Setup Content values, MediaStore to capture high quality image using compare intent
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_IMAGE_DESCRIPTION");

        //Uri of image to be captured
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        // Intent to lunch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    //Check if image is captured or not
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Image Captured, we have image in imageUri as assigned in pickImageCamera()
                        Log.d(TAG, "onActivityResult: Image Captured: " + imageUri);

                        //timestamp will be used as id of the image picked
                        String timestamp = "" + Utils.getTimestamp();

                        //setup model for image, Param 1 is id, Param 2 is imageUri, Param 3 is imageUrl, fromInternet
                        // Param == Parameter :)
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);

                        //reload the images
                        loadImages();
                    } else {
                        //cancelled
                        Utils.toast(AdCreateActivity.this, "Cancelled");
                    }
                }
            }
    );
    private void pickImageGallery(){
        Log.d(TAG, "pickImageGallery: ");

        //Intent to lunch Image picker e.g. gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //we only want to pick image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    //Check if image is picked or not
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //get data
                        Intent data = result.getData();
                        //get uri of image picked
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: Image Picked From Gallery: " + imageUri);

                        //timestamp will be used as id of the image picked
                        String timestamp = "" + Utils.getTimestamp();

                        //setup model for image, Param 1 is id, Param 2 is imageUri, Param 3 is imageUrl, fromInternet
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);

                        //reload the images
                        loadImages();

                    } else {
                        //Cancelled
                        Utils.toast(AdCreateActivity.this, "Cancelled");
                    }

                }
            }
    );

    //variables to hold Ad data
    private String brand = "";
    private String category = "'";
    private String condition = "";
    private String address = "";
    private String price = "";
    private String title = "";
    private String description = "";
    private String adOwnerName = "";
    private double latitude = 0;
    private double longitude = 0;
    private void validateData() {
        Log.d(TAG, "validateData: ");

        //input data
        adOwnerName = firebaseAuth.getCurrentUser().getDisplayName();
        brand = binding.brandEt.getText().toString().trim();
        category = binding.categoryAct.getText().toString().trim();
        condition = binding.conditionAct.getText().toString().trim();
        address = binding.locationAct.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        if (brand.isEmpty()) {
            //no brand entered in brandEt, show error in brandEt and focus
            binding.brandEt.setError("Enter Brand");
            binding.brandEt.requestFocus();
        } else if (category.isEmpty()) {
            //no categoryAct entered in categoryAct, show error in categoryAct and focus
            binding.categoryAct.setError("Choose Category");
            binding.categoryAct.requestFocus();
        } else if (condition.isEmpty()) {
            //no conditionAct entered in conditionAct, show error in conditionAct and focus
            binding.conditionAct.setError("Choose Condition");
            binding.conditionAct.requestFocus();
        } else if (address.isEmpty()) {
            //no locationAct entered in locationAct, show error in locationAct and focus
            binding.locationAct.setError("Choose Location");
            binding.locationAct.requestFocus();
        } else if (title.isEmpty()) {
            //no titleEt entered in titleEt, show error in titleEt and focus
            binding.titleEt.setError("Enter Title");
            binding.titleEt.requestFocus();
        } else if (description.isEmpty()) {
            //no descriptionEt entered in descriptionEt, show error in descriptionEt and focus
            binding.descriptionEt.setError("Enter Description");
            binding.descriptionEt.requestFocus();
        } else if (imagePickedArrayList.isEmpty()) {
            Utils.toast(this, "Pick at-least one image");
        } else {
            //All data is validated, we can proceed further now
            if(isEditMode) {
                updateAd();
            } else {
                postAd();
            }
        }
    }

    private void postAd() {
        Log.d(TAG, "postAd: ");
        //show progress
        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();

        //get current timestamp
        Long timestamp = Utils.getTimestamp();
        //firebase database Ads reference to store new Ads
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        //key id from the reference to use as Ad id
        String keyId = refAds.push().getKey();

        //Reference of current user info in Firebase Realtime Database to get user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adOwnerName = "" + snapshot.child("name").getValue();

                //setup data to add in firebase database
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("adOwnerName", "" + adOwnerName);
                hashMap.put("id", "" + keyId);
                hashMap.put("vid", "" + firebaseAuth.getUid());
                hashMap.put("brand", "" + brand);
                hashMap.put("category", "" + category);
                hashMap.put("condition", "" + condition);
                hashMap.put("address", "" + address);
                hashMap.put("price", "" + price);
                hashMap.put("title", "" + title);
                hashMap.put("description", "" + description);
                hashMap.put("status", "" + Utils.AD_STATUS_AVAILABLE);
                hashMap.put("timestamp", timestamp);
//                hashMap.put("latitude", latitude);
//                hashMap.put("longitude", longitude);

                //set data to firebase database. Ads -> AdId -> AdDataJSON
                refAds.child(keyId)
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: Ad Published");
                                uploadImagesStorage(keyId);
                                wasUploadSuccessful(keyId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ", e);
                                progressDialog.dismiss();
                                Utils.toast(AdCreateActivity.this, "Failed to publish Ad due to "+e.getMessage());
                            }
                        });

            }

            @Override

            public void onCancelled(@NonNull DatabaseError error) {


            }

        });


    }

    private void updateAd() {
        Log.d(TAG, "updateAd: ");

        // Show a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Ad");
        progressDialog.show();

        // Setup data to add in Firebase database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("brand", brand); // Assuming brand is a variable
        hashMap.put("category", category);
        hashMap.put("condition", condition);
        hashMap.put("address", address); // Assuming address is a variable
        hashMap.put("price", price); // Assuming price is a variable
        hashMap.put("title", title);
        hashMap.put("description", description); // Assuming description is a variable

        // Database path to update Ad. Ads > AdId > DataToUpdate
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adIdForEditing).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Ad data update success
                        progressDialog.dismiss();
                        // Start uploading images picked for the Ad
                        uploadImagesStorage(adIdForEditing);
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ad data update failed
                        progressDialog.dismiss();
                        Log.e(TAG, "onFailure: ", e);
                        Utils.toast(AdCreateActivity.this, "Failed to update Ad due to " + e.getMessage());
                    }
                });
    }

    private void uploadImagesStorage(String adId) {
        Log.d(TAG, "uploadImagesStorage: ");

        //there are multiple images in imagePickedArrayList, loop to upload all
        for (int i = 0; i < imagePickedArrayList.size(); i++) {
            //get model from the current position of the imagePickedArrayList
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);

            //Upload image only if picked from Gallery or cam
            if(!modelImagePicked.getFromInternet()) {
                //for name of the image in firebase storage
                String imageName = modelImagePicked.getId();
                //path and name of the image in firebase storage
                String filePathAndName = "Ads/" + imageName;

                int imageIndexForProgress = i + 1;

                //Storage reference with filePathAndName
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

                storageReference.putFile(modelImagePicked.getImageUri())
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                //calculate the current progress of the image being uploaded
                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                //setup progress dialog message on basis of current progress. e.g. Uploading 1 of 10 images... Progress 95%
                                String message = "Uploading " + imagePickedArrayList.size() + " images... \nPlease Wait... ";
                                Log.d (TAG, "onProgress: message: "+message);
                                //show progress
                                progressDialog. setMessage(message);
                                progressDialog.show() ;
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "onSuccess: ");

                                //image uploaded get url of uploaded image
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                Uri uploadedImageUrl = uriTask.getResult();

                                if (uriTask.isSuccessful()){
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put ("id", ""+modelImagePicked.getId());
                                    hashMap.put("imageUrl", ""+uploadedImageUrl);

                                    //add in firebase db. Ads -> AdId -> Images -> ImageId > ImageData
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference ("Ads");
                                    ref.child(adId).child("Images")
                                            .child(imageName)
                                            .updateChildren (hashMap) ;
                                }
//
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: ", e);
                                progressDialog.dismiss();
                            }
                        });
            }

        }


    }

    private void wasUploadSuccessful(String adId) {
        // Database path to load ad images: Ads > adId > Images
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the count of images under the "Images" node for the specified ad
                long imageCount = snapshot.getChildrenCount();

                // Now you can use the 'imageCount' variable as needed
                Log.d(TAG, "Number of images uploaded: " + imageCount);

                // Check the value of numOfUploaded using numOfUploaded[0]
                if (imageCount < imagePickedArrayList.size()) {
//                    progressDialog.dismiss();
//                    Utils.toast(AdCreateActivity.this, "Some images failed to upload.");
                    // You may want to implement additional logic, such as retrying the upload.
                } else {
                    progressDialog.dismiss();
                    Utils.toast(AdCreateActivity.this, "Ad Published Successfully");

                    // After publishing Ad successfully Go to Home
                    startActivity(new Intent(AdCreateActivity.this, HomeFragment.class));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAdDetails() {
        Log.d(TAG, "LoadAdDetails: ");

        // Ad's db path to get the Ad details. Ads › AdId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adIdForEditing)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Get the Ad details from the Firebase db
                        String brand = "" + snapshot.child("brand").getValue();
//                        String category = "" + snapshot.child("category").getValue();
                        String condition = "" + snapshot.child("condition").getValue();
//                        double latitude = (Double) snapshot.child("latitude").getValue();
//                        double longitude = (Double) snapshot.child("Longitude").getValue(); // Corrected case
                        String address = "" + snapshot.child("address").getValue();
                        String price = "" + snapshot.child("price").getValue();
                        String description = "" + snapshot.child("description").getValue();

                        // Set data to UI Views (Form)
                        binding.brandEt.setText(brand);
//                        binding.categoryAct.setText(category);
                        binding.conditionAct.setText(condition);
                        binding.locationAct.setText(address);
                        binding.priceEt.setText(price);
                        binding.descriptionEt.setText(description);

                        // Load the Ad images. Ads > AdId › Images
                        DatabaseReference refImages = snapshot.child("Images").getRef();
                        refImages.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Might be multiple images, so loop to get all
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String id = "" + ds.child("id").getValue();
                                    String imageUrl = "" + ds.child("imageUrl").getValue();
                                    ModelImagePicked modelImagePicked = new ModelImagePicked(id, null, imageUrl, true);
                                    imagePickedArrayList.add(modelImagePicked);
                                }
                                // Call a method to load images
                                loadImages();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle onCancelled
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }

}