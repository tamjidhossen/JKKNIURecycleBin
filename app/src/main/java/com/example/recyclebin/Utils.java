package com.example.recyclebin;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/*A class that will contain static functions, constants, variables that we will be used in whole application*/
public class Utils {

    public static final String AD_STATUS_AVAILABLE = "AVAILABLE";
    public static final String AD_STATUS_SOLD = "SOLD";

    private static final String TAG = "UTILS_TAG";

    //Categories array of the Ads
    public static final String[] categories = {
            "Mobiles",
            "Computers",
            "Electronics",
            "Vehicles",
            "Furniture",
            "Fashion",
            "Books",
            "Others"
    };

    //Categories icon array of Ads
    public static final int[] categoryIcons = {
            R.drawable.ic_category_mobiles,
            R.drawable.ic_category_computer,
            R.drawable.ic_category_electronics,
            R.drawable.ic_category_vehicles,
            R.drawable.ic_category_furniture,
            R.drawable.ic_category_fashion,
            R.drawable.ic_category_books,
            R.drawable.ic_category_others
    };

    // Address Array of the Ads
    public static final String[] address = {
            "Central Field",
            "Muktamanch",
            "Bangamata Hall",
            "Bangabondhu Hall",
            "Agnibina Hall",
            "Dolonchapa Hall",
            "First Gate",
            "Second Gate",
            "Chorpara"
    };

    //Conditions array of the Ads
    public static final String[] conditions = {"New", "Used", "Refurbished"};


    /** A Function to show Toast
     @param context the context of activity/fragment from where this function will be called
     @param message the message to be shown in the Toast */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /** A Function to get current timestamp
     @return Returns the current timestamp as long datatype
     */
    public static long getTimestamp(){
        return System.currentTimeMillis();
    }

    /**
     @param timestamp the timestamp of type Long that we need to format to dd/MM/yyyy
     @return timestamp formatted to date dd/MM/yyyy*/
    public static String formatTimestampDate(Long timestamp){
        if (timestamp == null) {
            return "00/00/00"; // or some default value
        }
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy", calendar).toString();
        return date;
    }

    //fixed issue of app crashing
    public static void addToFavorite(Context context, String adId, TextView favoriteCountTv) {
        Log.d(TAG, "addToFavorite called");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            toast(context, "Login Required");
        } else {
            long timestamp = getTimestamp();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("adId", adId);
            hashMap.put("timestamp", timestamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid())
                    .child("Favorites")
                    .child(adId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            toast(context, "Added to favorites!");
                            // Update favorite count immediately after adding to favorites
                            DatabaseReference adRef = FirebaseDatabase.getInstance().getReference("Ads").child(adId).child("favoriteCount");
                            adRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Integer currentFavoriteCount = mutableData.getValue(Integer.class);
                                    if (currentFavoriteCount == null) {
                                        currentFavoriteCount = 0;
                                    }
                                    // Increment the favorite count
                                    int newFavoriteCount = currentFavoriteCount + 1;
                                    mutableData.setValue(newFavoriteCount);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                    if (!committed) {
                                        // Transaction failed
                                        Utils.toast(context, "Failed to update favorite count due to " + databaseError.getMessage());
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toast(context, "Failed to add to favorites due to " + e.getMessage());
                        }
                    });
        }
    }

    public static void removeFromFavorite(Context context, String adId, TextView favoriteCountTv) {
        Log.d(TAG, "removeFromFavorite called");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            toast(context, "Login Required");
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid())
                    .child("Favorites")
                    .child(adId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            toast(context, "Removed from favorites");
                            // Update favorite count immediately after removing from favorites
                            DatabaseReference adRef = FirebaseDatabase.getInstance().getReference("Ads").child(adId).child("favoriteCount");
                            adRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    Integer currentFavoriteCount = mutableData.getValue(Integer.class);
                                    if (currentFavoriteCount == null) {
                                        currentFavoriteCount = 0;
                                    }
                                    // Decrement the favorite count, ensuring it doesn't go below 0
                                    int newFavoriteCount = Math.max(currentFavoriteCount - 1, 0);
                                    mutableData.setValue(newFavoriteCount);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                    if (!committed) {
                                        // Transaction failed
                                        Utils.toast(context, "Failed to update favorite count due to " + databaseError.getMessage());
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toast(context, "Failed to remove from favorites due to " + e.getMessage());
                        }
                    });
        }
    }

}
