package com.akapps.scoretrackerv47.Dialogs;

// this dialog is used in HomePage activity in Activities folder

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.akapps.scoretrackerv47.Activities.Game;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class dialog_add_game extends Dialog {

    private Activity activity;
    private TextView enter, cancel;
    private EditText game_Name;
    private TextView letters_Entered, default_Game_Name;
    private ArrayList<Game_Info> games;
    private ArrayList<Player_Info> players;
    private Context context;
    private View view;
    private Game_Info current_Game;

    public dialog_add_game(Activity a, View v, Context context, ArrayList<Player_Info> players) {
        super(a);
        this.activity = a;
        this.view= v;
        this.context= context;
        this.players = players;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_add_game);
        context= activity;

        // uses method to get current user data
        games = getGameInfo();

        // initializes TextViews and EditTexts
        letters_Entered= findViewById(R.id.game_Entered_Length);
        enter = findViewById(R.id.enter_game);
        cancel = findViewById(R.id.close_game);
        game_Name= findViewById(R.id.game_Name_EditText);
        default_Game_Name = findViewById(R.id.default_Game_Name);

        // focuses user attention to EditText box and opens keyboard for them
        game_Name.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // when EditText is changed, this method is called
        game_Name.addTextChangedListener(mTextEditorWatcher);

        String default_game_Name = "Game " + (games.size()+1);

        default_Game_Name.setText("Default game name is " + default_game_Name);

        // if user decides to name the game, then this method is called
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(game_Name.getText().toString().equals(""))
                    game_Name.setError("Enter Game Name");
                else {
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    games.remove(current_Game);
                    current_Game = new Game_Info(game_Name.getText().toString(), currentDateTimeString,
                            players.size(), "In progress", players);
                    games.add(current_Game);
                    ((Game) context).setTitle(games.get(games.size() - 1).getName_of_Game());
                    ((Game) context).gameInProgress();
                    ((Game) context).gameNameSet();
                    ((Game) context).startChronometer();
                    ((Game) context).games = games;
                    ((Game) context).updateCustomInterval(1, games.size()-1);
                    saveGames(games);
                    dismiss();
                }
            }
        });

        // dismisses dialog and sets the game name to be a generic name (Game [num of games+1])
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                int gameSize;

                if(games.isEmpty())
                    gameSize = 1;
                else
                    gameSize = games.size()+1;

                current_Game = new Game_Info("Game " + gameSize, currentDateTimeString,
                        players.size(), "In progress", players);
                games.add(current_Game);
                ((Game) context).setTitle(games.get(games.size()-1).getName_of_Game());
                ((Game) context).gameInProgress();
                ((Game) context).gameNameSet();
                ((Game) context).startChronometer();
                ((Game) context).games = games;
                ((Game) context).updateCustomInterval(1, games.size()-1);
                saveGames(games);
                dismiss();
            }
        });
    }

    // when user is inputting name in EditText, this method keeps track of the string length
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        // as long as EditText length is less than 13, it will keep updating length
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() < 13)
                letters_Entered.setText(s.length() + " / 12");
        }

        public void afterTextChanged(Editable s) { }
    };

    private ArrayList<Game_Info> getGameInfo() {
        ArrayList<Game_Info> savedArrayList = new ArrayList<>();

        try {
            FileInputStream inputStream = context.openFileInput(context.getResources().getString(R.string.game_Data));
            ObjectInputStream in = new ObjectInputStream(inputStream);
            savedArrayList = (ArrayList<Game_Info>) in.readObject();
            in.close();
            inputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return savedArrayList;
    }

    private void saveGames(ArrayList<Game_Info> games){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(context.getResources().getString(R.string.game_Data), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(games);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
