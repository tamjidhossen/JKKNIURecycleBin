package com.example.recyclebin;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
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
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy", calendar).toString();
        return date;
    }
}
