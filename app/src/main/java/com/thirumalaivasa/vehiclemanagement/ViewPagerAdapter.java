package com.thirumalaivasa.vehiclemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> title;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments=new ArrayList<>();
        this.title=new ArrayList<>();
    }


    public void addFragment(Fragment f,String title){
        this.fragments.add(f);
        this.title.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
