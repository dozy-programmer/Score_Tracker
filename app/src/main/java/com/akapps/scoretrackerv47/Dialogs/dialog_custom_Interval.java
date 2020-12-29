package com.akapps.scoretrackerv47.Dialogs;

// this dialog is used in Game activity in Activities folder

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.akapps.scoretrackerv47.Activities.Game;
import com.akapps.scoretrackerv47.R;
import com.akapps.scoretrackerv47.Activities.View_Game_Scores;

public class dialog_custom_Interval extends Dialog {

    private Activity activity;
    private View view;
    private TextView close, enter;
    private EditText custom_Number;
    private Context context;
    private int view_Game, gamePosition;
    private int points_Interval;

    public dialog_custom_Interval(Activity a, View v, Context context, int view_Game, int gamePosition) {
        super(a);
        this.activity= a;
        this.view= v;
        this.context = context;
        this.view_Game = view_Game;
        this.gamePosition = gamePosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_interval_dialog);

        // initializes TextViews and EditTexts
        close= findViewById(R.id.cancel_TextView);
        enter= findViewById(R.id.enter_TextView);
        custom_Number= findViewById(R.id.custom_Interval_Input);

        // opens keyboard for user without them having to touch EditText
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // if entered is pressed, then some things occur before dismissing
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks to see if EditText is empty
                if(custom_Number.getText().toString().equals(""))
                    custom_Number.setError("Please enter a number!");
                else
                    points_Interval= Integer.valueOf(custom_Number.getText().toString());
                // checks to see if input is in the correct interval and throws error if not
                if( points_Interval>100000 || points_Interval<3)
                    custom_Number.setError("Out of Bounds!");
                else {
                    if(view_Game==1){
                        // updates interval number in View_Game_Scores activity
                        ((View_Game_Scores) context).interval = points_Interval;
                        ((View_Game_Scores) context).custom_Interval.setLabelText(Integer.toString(points_Interval));
                        ((View_Game_Scores) context).updateInterval(points_Interval);
                        ((View_Game_Scores) context).closeButtonMenu();
                    }
                    else {
                        // updates interval number in Game activity
                        ((Game) context).interval = points_Interval;
                        ((Game) context).custom_Interval.setLabelText(Integer.toString(points_Interval));
                        ((Game) context).updateCustomInterval(points_Interval, gamePosition);
                        ((Game) context).closeButtonMenu();
                    }
                    dismiss();
                }
            }
        });

        // if close is pressed, then dialog is dismissed
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_Game==1){
                    ((View_Game_Scores) context).closeButtonMenu();
                }
                else {
                    ((Game) context).closeButtonMenu();
                }
                dismiss();
            }
        });
    }
}
