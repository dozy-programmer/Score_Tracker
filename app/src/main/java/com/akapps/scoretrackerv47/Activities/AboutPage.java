package com.akapps.scoretrackerv47.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akapps.scoretrackerv47.R;

public class AboutPage extends AppCompatActivity {

    private Button see_All_Updates;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        setTitle("About Page"); // sets title of activity
        context = this;

        see_All_Updates = findViewById(R.id.see_All_Updates);

        // start of setting the background color to what the user specified in settings
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1"); //

        if (theme.equals("Light")) {
            View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            root.setBackgroundColor(getResources().getColor(R.color.white));
            see_All_Updates.setBackgroundColor(Color.TRANSPARENT);
            see_All_Updates.setTextColor(getResources().getColor(R.color.orange));
        }
        else{
            see_All_Updates.setBackgroundColor(Color.TRANSPARENT);
            see_All_Updates.setTextColor(getResources().getColor(R.color.orange));
        }
        // end of setting the background color

        see_All_Updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("All Updates:")
                        .titleColor(getResources().getColor(R.color.colorPrimary))
                        .content(R.string.all_Updates)
                        .contentColor(getResources().getColor(R.color.orange))
                        .contentGravity(GravityEnum.START)
                        .backgroundColor(getResources().getColor(R.color.black))
                        .positiveText("CLOSE")
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .positiveColor(getResources().getColor(R.color.colorPrimary))
                        .show();
            }
        });
    }
}
