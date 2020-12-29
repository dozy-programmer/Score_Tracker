package com.akapps.scoretrackerv47.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.akapps.scoretrackerv47.R;

public class Bugs_Suggestions extends AppCompatActivity {

    private Button rateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs__suggestions);
        setTitle("Suggestions or Bugs?"); // sets title of activity

        rateApp = findViewById(R.id.review_Btn);

        // sets the background color to what the user specified in settings
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");

        if (theme.equals("Light")) {
            View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            root.setBackgroundColor(getResources().getColor(R.color.white));
        }
        // end of setting background color

        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(
                            "https://play.google.com/store/apps/details?id=com.akapps.scoretrackerv47"));
                    intent.setPackage("com.android.vending");
                    startActivity(intent);
                }catch(Exception c){}
            }
        });

    }
}
