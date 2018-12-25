package com.kaloyanov.ivan.yourleaf.activities.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.kaloyanov.ivan.yourleaf.R;
import com.kaloyanov.ivan.yourleaf.activities.lightning.LightFragment;
import com.kaloyanov.ivan.yourleaf.activities.settings.SettingsActivity;
import com.kaloyanov.ivan.yourleaf.activities.temperature.TemperatureFragment;
import com.kaloyanov.ivan.yourleaf.activities.watering.WaterFragment;
/*
 * @author ivan.kaloyanov
 */
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String systemName = preferences.getString(String.valueOf(R.string.systemName), null);
        toolbar.setTitle(systemName);

        // Setup tabs management
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    *  Return a fragment based on clicked tab number
    */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new WaterFragment();
                case 1:
                    return new LightFragment();
                case 2:
                    return new TemperatureFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
