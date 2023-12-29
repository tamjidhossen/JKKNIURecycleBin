package com.example.recyclebin;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/*A class that will contain static functions, constants, variables that we will be used in whole application*/
public class Utils {

    public static final String AD_STATUS_AVAILABLE = "AVAILABLE";
    public static final String AD_STATUS_SOLD = "SOLD";

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


    /**
     * Add the ad to favorites.
     *
     * @param context The context of the activity/fragment from where this function will be called.
     * @param adId    The ID of the ad to be added to the favorites of the current user.
     */
    public static void addToFavorite(Context context, String adId) {
        // Check if the user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            // Not logged in, can't add to favorites
            Utils.toast(context, "You're not logged in!");
        } else {
            // Logged in, can add to favorites
            // Get timestamp
            long timestamp = Utils.getTimestamp();

            // Setup data to add in Firebase database
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("adId", adId);
            hashMap.put("timestamp", timestamp);

            // Add data to the database: Users › vid › Favorites › adId › favoriteDataObj
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid())
                    .child("Favorites")
                    .child(adId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Success
                            Utils.toast(context, "Added to favorites!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed
                            Utils.toast(context, "Failed to add to favorites due to " + e.getMessage());
                        }
                    });
        }
    }


    /**
    * Remove the add from favorite
    * @param context the context of activity/fragment from where this function will be called
    the Id of the add to be removed from favorite of current user
    */
    public static void removeFromFavorite(Context context, String adId) {
        // Check if the user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            // Not logged in, can't remove from favorite
            Utils.toast(context, "You're not logged in!");
        } else {
            // Logged in, can remove from favorite
            // Remove data from the database: Users › vid › Favorites › adId
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid())
                    .child("Favorites")
                    .child(adId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Success
                            Utils.toast(context, "Removed from favorites");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed
                            Utils.toast(context, "Failed to remove from favorites due to " + e.getMessage());
                        }
                    });
        }
    }



}
