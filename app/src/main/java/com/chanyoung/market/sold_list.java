package com.chanyoung.market;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class sold_list extends AppCompatActivity {

    TabLayout tab;

    Fragment frag_sale;
    Fragment frag_reserved;
    Fragment frag_done;

    ViewPager2 viewPager;

    TabAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_list);

        createFragment();
        createViewPager();
        settingTabLayout();

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void createFragment() {
        frag_sale = new frag_sale();
        frag_reserved = new frag_reserved();
        frag_done = new frag_done();
    }

    private void createViewPager() {
        viewPager = (ViewPager2) findViewById(R.id.sold_pager);

        adapter = new TabAdapter(getSupportFragmentManager(),getLifecycle());
        adapter.addFragment(frag_sale);
        adapter.addFragment(frag_reserved);
        adapter.addFragment(frag_done);

        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);
    }

    private void settingTabLayout() {
        tab = (TabLayout) findViewById(R.id.sold_tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int i = tab.getPosition();

                switch(i) {

                    case 0 :
                        viewPager.setCurrentItem(0);
                        Log.d(TAG, String.valueOf(i));
                        break;
                    case 1 :
                        viewPager.setCurrentItem(1);
                        Log.d(TAG, String.valueOf(i));
                        break;
                    case 2 :
                        viewPager.setCurrentItem(2);
                        Log.d(TAG, String.valueOf(i));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }



}