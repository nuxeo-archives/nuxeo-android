package org.nuxeo.android.automation.fragments;

import org.nuxeo.android.automation.R;

import android.support.v4.app.FragmentManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class SettingsActivity extends FragmentActivity {

    ViewPager mViewPager;

    SettingsPagerAdapter mSettingsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_screen_frag);

        if (findViewById(R.id.pager) != null) {
            // Here we are on small screens only, we have to use a ViewPager to
            // display the setting screens
            mSettingsPagerAdapter = new SettingsPagerAdapter(
                    getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSettingsPagerAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (Build.VERSION.SDK_INT >= 11) {
                        getActionBar().setSelectedNavigationItem(position);
                    }
                }
            });

            if (Build.VERSION.SDK_INT >= 11) {
                final ActionBar actionBar = getActionBar();
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

                ActionBar.TabListener tabListener = new ActionBar.TabListener() {

                    @Override
                    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                    }

                    @Override
                    public void onTabSelected(Tab tab, FragmentTransaction ft) {
                        mViewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabReselected(Tab tab, FragmentTransaction ft) {
                    }
                };
                actionBar.addTab(actionBar.newTab().setText("Network Settings").setTabListener(
                        tabListener));
                actionBar.addTab(actionBar.newTab().setText("Server Settings").setTabListener(
                        tabListener));
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
        // if we are on large screens, the layout-large/offline_screen_frag.xml
        // will be displayed, with both fragments fitting on one large screen,
        // we have nothing more to do
    }

    public class SettingsPagerAdapter extends FragmentPagerAdapter {
        public SettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
            case 0:
                fragment = new NetworkSettingsFragment();
                break;
            case 1:
                fragment = new ServerSettingsFragment();
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
