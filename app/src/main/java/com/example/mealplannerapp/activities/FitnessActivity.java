package com.example.mealplannerapp.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.fragments.FitnessFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FitnessActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FitnessPagerAdapter());

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {

                    if (position == 0) {
                        tab.setText("Exercise");
                    } else {
                        tab.setText("Yoga");
                    }

                }).attach();
    }

    // ---------------- VIEW PAGER ADAPTER ----------------

    private class FitnessPagerAdapter extends FragmentStateAdapter {

        public FitnessPagerAdapter() {
            super(FitnessActivity.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if (position == 0) {

                return FitnessFragment.newInstance("Exercise");

            } else {

                return FitnessFragment.newInstance("Yoga");
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}