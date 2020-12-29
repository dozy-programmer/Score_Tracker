package com.akapps.scoretrackerv47.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.akapps.scoretrackerv47.ListView_Adapters.LstViewAdapter_Scores;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Scores extends AppCompatActivity {

    private ListView scores_Listview;
    private LstViewAdapter_Scores adapter;
    private ArrayList<Player_Info> players;
    private Context context;
    private  Player_Info column;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        setTitle("Recent Games Scores"); // sets title of activity
        context = this;

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");


        if (theme.equals("Light")) {
            View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            root.setBackgroundColor(getResources().getColor(R.color.white));
        }

        // initializes ListView and sets the divide between each item and color inside it
        scores_Listview = findViewById(R.id.scores_Listview);
        scores_Listview.setDivider(new ColorDrawable(Color.TRANSPARENT));
        scores_Listview.setDividerHeight(20);

        // gets players from text file with all user data
        players = getUserInfo();

        // this player_Row object makes it easier for user to read the scores chart
        column = new Player_Info("Game", 0, 0, 0, 0,
                0, false, new int[6], 0, "");

        // player_Row has columns 1 2 3 4 & 5
        for(int i=1; i<6; i++)
            column.setRecentScores(i, i);
        // then player_Row is added to top of players ArrayList
        players.add(0, column);

        // updates ListView for user to see
        adapter = new LstViewAdapter_Scores(context, R.layout.scores_format, players);
        scores_Listview.setAdapter(adapter);
    }

    private ArrayList<Player_Info> getUserInfo() {
        ArrayList<Player_Info> savedArrayList = null;

        try {
            FileInputStream inputStream = openFileInput(getString(R.string.user_Data));
            ObjectInputStream in = new ObjectInputStream(inputStream);
            savedArrayList = (ArrayList<Player_Info>) in.readObject();
            in.close();
            inputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return savedArrayList;
    }
}
