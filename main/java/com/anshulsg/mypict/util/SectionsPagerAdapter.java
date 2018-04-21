package com.anshulsg.mypict.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.anshulsg.mypict.ui.fragments.AttendanceFragment;
import com.anshulsg.mypict.ui.fragments.NewsFragment;
import com.anshulsg.mypict.ui.fragments.SettingsFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AttendanceFragment();
            case 1:
                return new NewsFragment();
            case 2:
                return new SettingsFragment();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
}
