package com.example.recyclebin.fragments;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.example.recyclebin.databinding.FragmentMyAdsBinding;

public class MyAdsFragment extends Fragment {

    private static final String TAG = "MY_ADS_TAG";
    private FragmentMyAdsBinding binding;
    private Context mContext;
    private MyTabsViewPagerAdapter myTabsViewPagerAdapter;

    public MyAdsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyAdsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the tabs to the TabLayout
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My Ads"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Favorites"));
        // Set tab text color
        binding.tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        // Fragment manager initializing using getChildFragmentManager() because we are using tabs in the fragment, not the activity
        FragmentManager fragmentManager = getChildFragmentManager();
        myTabsViewPagerAdapter = new MyTabsViewPagerAdapter(fragmentManager, getLifecycle());

        // Tab selected listener to set the current item on the view page
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Set the current item on the view page
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Change Tab when swiping
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

        // Set up ViewPager2 with the adapter
        binding.viewPager.setAdapter(myTabsViewPagerAdapter);
    }

    public class MyTabsViewPagerAdapter extends FragmentStateAdapter {
        public MyTabsViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Tab position starts from 0. If 0, set/show MyAdsAdsFragment; otherwise, it is definitely 1, so show MyAdsFavFragment
            if (position == 0) {
                return new MyAdsAdsFragment();
            } else {
                return new MyAdsFavFragment();
            }
        }

        @Override
        public int getItemCount() {
            // Return the number of items/tabs
            return 2; // Setting static size 2 because we have two tabs/fragments
        }
    }
}
