package com.example.recyclebin;

import android.content.Context;
import android.widget.Toast;

/*A class that will contain static functions, constants, variables that we will be used in whole application*/
public class Utils {
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
}
