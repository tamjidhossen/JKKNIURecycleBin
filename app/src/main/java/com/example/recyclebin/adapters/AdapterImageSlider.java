package com.example.recyclebin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.example.recyclebin.R;
import com.example.recyclebin.databinding.RowImageSliderBinding;
import com.example.recyclebin.models.ModelImageSlider;

import java.util.ArrayList;

// Adapter class for the image slider in RecyclerView
public class AdapterImageSlider extends RecyclerView.Adapter<AdapterImageSlider.HolderImageSlider> {

    private RowImageSliderBinding binding;  // View binding instance for the row item
    private static final String TAG = "IMAGE_SLIDER_TAG";  // Log tag for debugging
    private Context context;  // Context of the activity/fragment where the adapter is used
    private ArrayList<ModelImageSlider> imageSliderArraylist;  // List of images for the slider

    // Constructor for the AdapterImageSlider class
    public AdapterImageSlider(Context context, ArrayList<ModelImageSlider> imageSliderArraylist) {
        this.context = context;
        this.imageSliderArraylist = imageSliderArraylist;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public HolderImageSlider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the row layout and create a binding instance
        binding = RowImageSliderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderImageSlider(binding.getRoot());
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull HolderImageSlider holder, int position) {
        // Get the data at the specified position and set it to the UI views
        ModelImageSlider modelImageSlider = imageSliderArraylist.get(position);
        String imageUrl = modelImageSlider.getImageUrl();
        String imageCount = (position + 1) + "/" + imageSliderArraylist.size();

        holder.imageCountTv.setText(imageCount);

        try {
            // Load the image using Glide library with a placeholder
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_gray)
                    .into(holder.imageIv);
        } catch (Exception e) {
            // Log any exceptions that occur during image loading
            Log.e(TAG, "onBindViewHolder: ", e);
        }

        // Handle image click (e.g., open in full screen - ImageViewActivity)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the action to be performed on image click
            }
        });
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return imageSliderArraylist.size();
    }

    // ViewHolder class to hold references to the UI views of the row item
    public static class HolderImageSlider extends RecyclerView.ViewHolder {
        ShapeableImageView imageIv;
        TextView imageCountTv;

        // Constructor to initialize UI views
        public HolderImageSlider(View itemView) {
            super(itemView);
            imageIv = itemView.findViewById(R.id.imageIv);
            imageCountTv = itemView.findViewById(R.id.imageCountTv);
        }
    }
}
