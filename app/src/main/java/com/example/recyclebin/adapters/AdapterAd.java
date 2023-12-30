package com.example.recyclebin.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget. Filterable;
import android.widget. ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recyclebin.FilterAd;
import com.example.recyclebin.activities.AdDetailsActivity;
import com.example.recyclebin.models.ModelAd;
import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.google.android.material. imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.recyclebin.databinding.RowAdBinding;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterAd extends RecyclerView.Adapter<AdapterAd.HolderAd> implements Filterable {
    //View Binding
    private RowAdBinding binding;
    private static final String TAG = "ADAPTER_AD_TAG";
    //Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;

    //Context of activity/fragment from where instance of AdapterAd class is created
    private Context context;
    //adArrayList The list of the Ads
    public ArrayList<ModelAd> adArrayList;
    private ArrayList<ModelAd> filterList;
    private FilterAd filter;
    /**
     * Constructor*
     *
     * @param context    The context of activity/fragment from where instance of AdapterAd class is created *
     * @param adArrayList The list of ads
     */

    public AdapterAd(Context context, ArrayList<ModelAd> adArrayList) {
        this.context = context;
        this.adArrayList = reverseList(adArrayList); // Reverse the list
        this.filterList = this.adArrayList;

        //get instance of firebase auth for Auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Helper method to reverse the list, so the latest ad stays on top
    private ArrayList<ModelAd> reverseList(ArrayList<ModelAd> list) {
        ArrayList<ModelAd> reversedList = new ArrayList<>(list);
        Collections.reverse(reversedList);
        return reversedList;
    }


    @NonNull
    @Override
    public HolderAd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate/bind the row_ad.xml
        binding = RowAdBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderAd(binding.getRoot());
    }
    @Override
    public void onBindViewHolder(@NonNull HolderAd holder, int position) {
        //get data from particular position of list and set to the UI Views of row_ad.xml and Handle clicks
        ModelAd modelAd = adArrayList.get(position);

        String title = modelAd.getTitle();
        String adOwnerName = modelAd.getAdOwnerName();
//        String description = modelAd.getDescription();
//        String address = modelAd.getAddress();
//        String condition = modelAd.getCondition();
        String price = modelAd.getPrice();
        Long timestamp = modelAd.getTimestamp();
        String formattedDate = Utils.formatTimestampDate(timestamp);

        //function call: load first image from available images of Ad e.g. if there are 5 images of Ad, load first one
        loadAdFirstImage(modelAd, holder);

        //If user is logged in then check that if the Ad is in Favourite of Current user
        if(firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite(modelAd, holder);
        }

        //set data to UI Views of row_ad.xml

        holder.ownerAdNameTv.setText(adOwnerName);
        holder.titleTv.setText(title);
//        holder.descriptionTv.setText(description);
//        holder.addressTv.setText(address);
//        holder.conditionTv.setText(condition);
        holder.priceTv.setText(price);
        holder.dateTv.setText(formattedDate);

        // handle itemView(i.e. Ad) click, open the AdDetailsActivity, also pass the id of the Ad to intent to load details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdDetailsActivity.class);
                intent.putExtra("adId", modelAd.getId());
                context.startActivity(intent);
            }
        });

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean favourite = modelAd.isFavorite();
                if (favourite){
                    Utils.removeFromFavorite(context, modelAd.getId());
                } else {
                    Utils.addToFavorite(context, modelAd.getId());
                }
            }
        });
    }



    private void checkIsFavorite(ModelAd modelAd, HolderAd holder) {
        // DB path to check if Ad is in Favorite of the current user. Users › vid › Favorites › adId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .child("Favorites")
                .child(modelAd.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // If snapshot exists (value is true), it means the Ad is in the favorite of the current user; otherwise, it is not.
                        boolean favorite = snapshot.exists();
                        // Set that value (true/false) to the model
                        modelAd.setFavorite(favorite);

                        // Check if it's a favorite or not to set the image of favBtn accordingly
                        if (favorite) {
                            // Favorite, set image ic_fav_yes to button favBtn
                            holder.favBtn.setImageResource(R.drawable.ic_fav_yes);
                        } else {
                            // Not Favorite, set image ic_fav_no to button favBtn
                            holder.favBtn.setImageResource(R.drawable.ic_fav_no);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled, if needed
                    }
                });
    }


    private void loadAdFirstImage(ModelAd modelAd, HolderAd holder) {
        Log.d (TAG, "LoadAdFirstImage: ");
        //load first image from available images of Ad e.g, if there are 5 images of Ad, load first one
        //Ad id to get image of it
        String adId = modelAd.getId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads");
        reference.child(adId).child("Images").limitToFirst(1)
                .addValueEventListener(new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){
                        //this will return only 1 image as we have used query .limitToFirst(1)
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get url of the image
                            String imageUrl = "" + ds.child("imageUrl").getValue();
                            Log.d(TAG, "onDataChange: imageUrl: " + imageUrl);
                            //set image to Image Vew i.e.
                            try {
                                Glide.with(context)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_image_gray)
                                        .into(holder.imageIv);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange", e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
        });
    }
    @Override
    public int getItemCount() {
        //return the size of list
        return adArrayList.size();
    }
    @Override
    public Filter getFilter() {
        //init the filter obj only if it is null
        if (filter == null) {
            filter = new FilterAd(this, filterList);
        }
        return filter;
    }
    class HolderAd extends RecyclerView.ViewHolder{
        //UI Views of the row_ad.xml
        ShapeableImageView imageIv;
        TextView titleTv, ownerAdNameTv, descriptionTv, addressTv, conditionTv, priceTv, dateTv;

        ImageButton favBtn;
        public HolderAd(@NonNull View itemView) {
            super(itemView);
            //init UI Views of the row_ad.xml
            ownerAdNameTv = binding.ownerAdNameTv;
            imageIv = binding.imageIv;
            titleTv = binding.titleTv;
//            descriptionTv = binding.descriptionTv;
            favBtn = binding.favBtn;
//            addressTv = binding.addressTv;
//            conditionTv = binding.conditionTv;
            priceTv = binding.priceTv;
            dateTv = binding.dateTv;
        }
    }
}