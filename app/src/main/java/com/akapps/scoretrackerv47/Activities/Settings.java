package com.akapps.scoretrackerv47.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.akapps.scoretrackerv47.R;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class Settings extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{

    private BottomNavigationBar bottomNavigationBar;
    private int lastSelectedPosition;
    private int bottom_Navigation_Color;
    private static Context context;
    private static View root;

    @Override
    public void onBackPressed() {
        SharedPreferences preference = getSharedPreferences("position", Activity.MODE_PRIVATE);
        int position = preference.getInt("navigation",-1);

        try {
            if(position>-1 && position<3) {
                bottomNavigationBar.setFirstSelectedPosition(position);
                switch (position){
                    case 0:
                        Intent home = new Intent(Settings.this, HomePage.class);
                        startActivity(home);
                        break;

                    case 1:
                        Intent settings = new Intent(Settings.this, View_Games.class);
                        startActivity(settings);
                        break;
                }
            }
        }catch (Exception e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        context = this;
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar_setting);

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");

        bottom_Navigation_Color = R.color.black;

        root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (theme.equals("Light")) {
            root.setBackgroundColor(getResources().getColor(R.color.white));
            bottom_Navigation_Color = R.color.white;
        }

        bottomNavigationBar.setTabSelectedListener(this);
        lastSelectedPosition=2;

        bottomNavigationBar
                .setBarBackgroundColor(bottom_Navigation_Color)
                .addItem(new BottomNavigationItem(R.drawable.icon_homepage, "Home").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.icon_view_games, "View Games").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_settings, "Settings").setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onTabSelected(int position) {
        bottom_Navigation_Position_Update(lastSelectedPosition);
        lastSelectedPosition = position;

        if(lastSelectedPosition == 0){
            Intent startGame = new Intent(this, HomePage.class);
            startActivity(startGame);
        }
        else if(lastSelectedPosition == 1){
            Intent startGame = new Intent(this, View_Games.class);
            startActivity(startGame);
        }
        else if(lastSelectedPosition==2){
        }
    }

    private void bottom_Navigation_Position_Update(int position) {
        SharedPreferences sp = getSharedPreferences("position", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("navigation", position);
        editor.commit();
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String theme = sharedPreferences.getString("theme","-1");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            boolean iconMode = sp.getBoolean("icon mode",false);

            if(iconMode){
                Intent i = new Intent(context, HomePage.class);
                startActivity(i);
            }

            if(theme.equals("Light") || theme.equals("Dark")) {
                startActivity(getActivity().getIntent());
            }
            if ( getContext().getSystemService(VIBRATOR_SERVICE) == null ) {
                Toast.makeText(context, "Device does not support vibration", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onStop() {
            super.onStop();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}