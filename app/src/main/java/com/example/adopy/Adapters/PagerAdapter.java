package com.example.adopy.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.adopy.Fragments.HomeFragment;
import com.example.adopy.Fragments.MassagesFragment;
import com.example.adopy.Fragments.MyPostsFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    public PagerAdapter(FragmentManager fm,int numOfTabs ) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new MyPostsFragment();
            case 2:
                return new MassagesFragment();
                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
