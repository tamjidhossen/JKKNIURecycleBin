package com.example.recyclebin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget. ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recyclebin.FilterAd;
import com.example.recyclebin.activities.AdDetailsActivity;
import com.example.recyclebin.databinding.RowReportBinding;
import com.example.recyclebin.models.ModelAd;
import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.example.recyclebin.models.ModelReport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class AdapterReport extends RecyclerView.Adapter<AdapterReport.HolderReport> {

    //View Binding
    private RowReportBinding binding;
    private static final String TAG = "ADAPTER_REPORT_TAG";

    //Firebase Auth for auth related tasks
    private FirebaseAuth firebaseAuth;

    //Context of activity/fragment from where instance of AdapterReport class is created
    private Context context;
    //adArrayList The list of the Ads
    public ArrayList<ModelReport> reportsArrayList;

    public AdapterReport(Context context, ArrayList<ModelReport> reportsArrayList) {
        this.context = context;
        this.reportsArrayList = reverseList(reportsArrayList);

        //get instance of firebase auth for Auth related tasks
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Helper method to reverse the list, so the latest report stays on top
    private ArrayList<ModelReport> reverseList(ArrayList<ModelReport> list) {
        ArrayList<ModelReport> reversedList = new ArrayList<>(list);
        Collections.reverse(reversedList);
        return reversedList;
    }


    @NonNull
    @Override
    public HolderReport onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate/bind the row_report.xml
        binding = RowReportBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderReport(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReport holder, int position) {
        //get data from particular position of list and set to the UI Views of row_report.xml and Handle clicks
        ModelReport modelReport = reportsArrayList.get(position);

        String reportersName = modelReport.getReportersName();
        String reportDescription = modelReport.getReport();

        holder.reportersNameTv.setText(reportersName);
        holder.reportDescriptionTv.setText(reportDescription);

        loadReportersImage(modelReport, holder);

        binding.reportDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alert dialog to confirm if the admin really wants to delete the Report
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder.setTitle("Delete Report")
                        .setMessage("Are you sure you want to delete this Report?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete Clicked, delete Report
                                Log.d(TAG, "deleteReport: ");

                                String reportId = modelReport.getReportId();
                                String adId = modelReport.getAdId();

                                // Database path to delete the ad: Ads > adId
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads").child(adId).child("Reports");
                                ref.child(reportId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Success: Display a message and finish the activity
                                        Log.d(TAG, "onSuccess: Deleted");
                                        Utils.toast(context, "Deleted");
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failure: Display an error message
                                        Log.e(TAG, "onFailure: ", e);
                                        Utils.toast(context,"Failed to delete due to " + e.getMessage());
                                    }
                                });
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
    }

    private void loadReportersImage(ModelReport modelReport, HolderReport holder) {
        String reportId = modelReport.getReportId();
        String adId = modelReport.getAdId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads").child(adId).child("Reports");
        ref.child(reportId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = "" + snapshot.child("reportersProfileImageUrl").getValue();
                try {
                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_image_gray)
                            .into(holder.reportersImageIv);
                } catch (Exception e) {
                    Log.e(TAG, "onDataChange", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return reportsArrayList.size();
    }


    class HolderReport extends RecyclerView.ViewHolder{

        ShapeableImageView reportersImageIv;
        TextView reportersNameTv, reportDescriptionTv;

        ImageButton reportDeleteBtn;

        public HolderReport(@NonNull View itemView) {
            super(itemView);

            reportersImageIv = binding.reportersImageIv;
            reportersNameTv = binding.reportersNameTv;
            reportDescriptionTv = binding.reportDescriptionTv;
            reportDeleteBtn = binding.reportDeleteBtn;
        }
    }
}
