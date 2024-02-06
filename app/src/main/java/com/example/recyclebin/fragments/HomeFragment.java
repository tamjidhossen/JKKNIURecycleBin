package com.example.recyclebin.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.recyclebin.models.ModelAd;
import com.example.recyclebin.models.ModelCategoryHome;
import com.example.recyclebin.R;
import com.example.recyclebin.Utils;
import com.example.recyclebin.adapters.AdapterAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.recyclebin.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //View Binding
    private FragmentHomeBinding binding;
    //TAG to show logs in logcat
    private static final String TAG = "HOME_TAG";
    private static final int MAX_DISTANCE_TO_LOAD_ADS_KM = 10;
    //Context for this fragment class
    private Context mContext;
    //adArrayList to hold ads list to show in RecyclerView
    private ArrayList<ModelAd> adArrayList;
    //AdapterAd class instance to set to Recyclerview to show Ads list
    private AdapterAd adapterAd;
    //SharedPreferences to store the selected location from map to load ads nearby
    private SharedPreferences locationSp;
    //location info required to load ads nearby. We will get this info from the SharedPreferences saved after picking from map
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private String currentAddress = "";

    @Override
    public void onAttach(@NonNull Context context) {//get and init the context for this fragment class
        mContext = context;
        super .onAttach(context);
    }
    public HomeFragment(){
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate/bind the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //loads category
        loadCategories();

        //load all ads
        loadAds("All");

        // search box is one line only
        binding.searchEt.setSingleLine(true);

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: Query: " + s);
                try {
                    String query = s.toString();
                    adapterAd.getFilter().filter(query);
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: ", e);
                }
            }
            @Override
            public void afterTextChanged (Editable s){

            }
        });
    }

    private void loadCategories(){
        //init categoryArrayList
        ArrayList<ModelCategoryHome> categoryArrayList = new ArrayList<>();

        //ModelCategory instance to show all products
        ModelCategoryHome modelCategoryAll = new ModelCategoryHome ("All", R.drawable.ic_category_all);
        categoryArrayList.add(modelCategoryAll);

        //get categories from Utils class and add in categoryArraylist
        for (int i = 0; i< Utils.categories.length; i++){
            //ModelCategory instance to get/hold category from current index
            ModelCategoryHome modelCategory = new ModelCategoryHome(Utils.categories[i], Utils.categoryIcons[i]);
            //add modelCategory to categoryArrayList
            categoryArrayList.add(modelCategory);
        }

        // Click listener for Mobiles category
        binding.mobileCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[0]);
                binding.catNameTv.setText(Utils.categories[0]);
            }
        });

        // Click listener for Computers category
        binding.computersCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[1]);
                binding.catNameTv.setText(Utils.categories[1]);
            }
        });

        // Click listener for Electronics category
        binding.electronicsCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[2]);
                binding.catNameTv.setText(Utils.categories[2]);
            }
        });

        // Click listener for Vehicles category
        binding.vehiclesCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[3]);
                binding.catNameTv.setText(Utils.categories[3]);
            }
        });

        // Click listener for Furniture category
        binding.furnitureCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[4]);
                binding.catNameTv.setText(Utils.categories[4]);
            }
        });

        // Click listener for Fashion category
        binding.fashionCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[5]);
                binding.catNameTv.setText(Utils.categories[5]);
            }
        });

        // Click listener for Books category
        binding.booksCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[6]);
                binding.catNameTv.setText(Utils.categories[6]);
            }
        });

        // Click listener for Others category
        binding.othersCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds(Utils.categories[7]);
                binding.catNameTv.setText(Utils.categories[7]);
            }
        });

        // Click listener for Sports category
        binding.allCatCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds("All");
                binding.catNameTv.setText("All");
            }
        });

    }

    private void loadAds(String category){
        Log.d(TAG, "loadAds: Category: "+category);

        //init adArraylist before starting adding data into it
        adArrayList = new ArrayList<>();

        //Firebase DB listener to load ads based on Category
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear adArraylist each time starting adding data into it
                adArrayList.clear();

                //load ads list
                for (DataSnapshot ds: snapshot.getChildren()){

                    //Prepare ModelAd with all data from Firebase DB
                    ModelAd modelAd = ds.getValue (ModelAd.class);

                    //filter
                    if (category.equals("All")) {
                        adArrayList.add(modelAd);
                    } else {
                        //Some category is selected e.g. Furniture
                        if (modelAd.getCategory().equals(category)){
                            adArrayList.add(modelAd);
                        }
                    }
                }
                adapterAd = new AdapterAd(mContext, adArrayList);
                binding.adsRv.setAdapter (adapterAd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}