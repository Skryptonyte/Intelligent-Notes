package com.example.madprojectt;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter
        extends FragmentStateAdapter {

    String dbname;
    public ViewPagerAdapter(
            FragmentActivity fm, String dbname)
    {
        super(fm);
        this.dbname = dbname;
    }

    @Override
    public Fragment createFragment(int position)
    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new DocumentListFragment(dbname);
        else if (position == 1)
            fragment = new TranslateActivity(dbname);
        else if (position == 2)
            fragment = new ScanFragment(dbname);

        return fragment;
    }

    @Override
    public int getItemCount()
    {
        return 3;
    }


}