package com.akapps.scoretrackerv47.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.ListView_Adapters.ListViewAdapter_View_All_Games;
import com.akapps.scoretrackerv47.R;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class View_Games extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{

    private ListView all_Games_ListView;
    private Context context;
    private ListViewAdapter_View_All_Games adapter;
    private ArrayList<Game_Info> all_Games;
    private ArrayList<Player_Info> all_Players;
    private BottomNavigationBar bottomNavigationBar;
    private int lastSelectedPosition;
    private int bottom_Navigation_Color;

    @Override
    public void onBackPressed() {
        SharedPreferences preference = getSharedPreferences("position", Activity.MODE_PRIVATE);
        int position = preference.getInt("navigation",-1);

        try {
            if(position>-1 && position<3) {
                bottomNavigationBar.setFirstSelectedPosition(position);
                switch (position){
                    case 0:
                        Intent home = new Intent(View_Games.this, HomePage.class);
                        startActivity(home);
                        break;

                    case 2:
                        Intent settings = new Intent(View_Games.this, Settings.class);
                        startActivity(settings);
                        break;
                }
            }
        }catch (Exception e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_games);
        setTitle("All Games"); // sets title of activity
        context = this;

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");

        bottom_Navigation_Color = R.color.black;

        if (theme.equals("Light")) {
            View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            root.setBackgroundColor(getResources().getColor(R.color.white));
            bottom_Navigation_Color = R.color.white;
        }

        all_Games_ListView = findViewById(R.id.games_ListView);
        all_Games_ListView.setEmptyView(findViewById(R.id.empty_games));
        all_Games_ListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar_view_games);
        all_Games_ListView.setDividerHeight(1);

        bottomNavigationBar.setTabSelectedListener(this);
        lastSelectedPosition = 1;

        bottomNavigationBar
                .setBarBackgroundColor(bottom_Navigation_Color)
                .addItem(new BottomNavigationItem(R.drawable.icon_homepage, "Home").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.icon_view_games, "View Games").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_settings, "Settings").setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

        all_Games = getGameInfo();
        all_Players = getUserData();

        adapter = new ListViewAdapter_View_All_Games(context, R.layout.view_all_games_format, all_Games);
        all_Games_ListView.setAdapter(adapter);


        all_Games_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent view_Scores = new Intent(View_Games.this, View_Game_Scores.class);
                view_Scores.putExtra("Game Position", position);
                view_Scores.putExtra("View Scores Mode", 1);
                startActivity(view_Scores);
            }
        });

        all_Games_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new MaterialDialog.Builder(context)
                        .title(all_Games.get(position).getName_of_Game())
                        .content(R.string.edit_game)
                        .contentGravity(GravityEnum.CENTER)
                        .backgroundColor(getResources().getColor(R.color.black))
                        .positiveText("EDIT NAME")
                        .negativeText("CLOSE")
                        .neutralText("DELETE")
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editGameName(position);
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                deletePlayer(position);
                                dialog.dismiss();
                            }
                        })
                        .titleColor(getResources().getColor(R.color.orange_red))
                        .contentColor(getResources().getColor(R.color.colorPrimary))
                        .positiveColor(getResources().getColor(R.color.green))
                        .negativeColor(getResources().getColor(R.color.gray))
                        .neutralColor(getResources().getColor(R.color.red))
                        .show();
                return true;
            }
        });
    }

    private void bottom_Navigation_Position_Update(int position) {
        SharedPreferences sp = getSharedPreferences("position", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("navigation", position);
        editor.commit();
    }

    @Override
    public void onTabSelected(int position) {
        bottom_Navigation_Position_Update(lastSelectedPosition);
        lastSelectedPosition = position;

        if(lastSelectedPosition == 0){
            Intent startGame = new Intent(View_Games.this, HomePage.class);
            startActivity(startGame);
        }
        else if(lastSelectedPosition == 1){
        }
        else if(lastSelectedPosition==2){
            Intent startGame = new Intent(View_Games.this, Settings.class);
            startActivity(startGame);
        }
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates menu that is on the action bar
            getMenuInflater().inflate(R.menu.menu_view_game_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_Info_View_Game) {
            new MaterialDialog.Builder(this)
                    .title("Gestures:")
                    .content(R.string.view_game_gestures)
                    .contentGravity(GravityEnum.CENTER)
                    .backgroundColor(getResources().getColor(R.color.black))
                    .titleColor(getResources().getColor(R.color.orange_red))
                    .contentColor(getResources().getColor(R.color.colorPrimary))
                    .positiveColor(getResources().getColor(R.color.gray))
                    .positiveText("CLOSE")
                    .canceledOnTouchOutside(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        else if(item.getItemId() == R.id.action_Export_Game_Data){

            new MaterialDialog.Builder(this)
                    .title("Select Data to Export")
                    .backgroundColor(getResources().getColor(R.color.black))
                    .positiveText("GAME DATA")
                    .canceledOnTouchOutside(true)
                    .negativeText("PLAYER DATA")
                    .neutralText("BOTH")
                    .titleColor(getResources().getColor(R.color.orange_red))
                    .neutralColor(getResources().getColor(R.color.red))
                    .positiveColor(getResources().getColor(R.color.green))
                    .negativeColor(getResources().getColor(R.color.gray))
                    .onPositive(new MaterialDialog.SingleButtonCallback() { // game data
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(all_Games.size()<1){
                                Snackbar.make(findViewById(android.R.id.content), "No Game Data to Export!",
                                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }
                            else {
                                dialog_Choose_Games_to_Export(false, true);
                            }
                            dialog.dismiss(); // dismisses dialog
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() { // player data
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(all_Games.size()==0 || all_Players.size()==0){
                                Snackbar.make(findViewById(android.R.id.content), "No Game Or Player Data to Export!",
                                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }
                            else {
                                dialog_Choose_Games_to_Export(true, true);
                            }
                            dialog.dismiss(); // dismisses dialog
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) { // both types of data
                            if(all_Players.size()==0){
                                Snackbar.make(findViewById(android.R.id.content), "No player data to Export!",
                                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }
                            else {
                                dialog_Choose_Games_to_Export(true, false);
                            }
                            dialog.dismiss(); // dismisses dialog
                        }
                    })
                    .show();
        }
        else if(item.getItemId() == R.id.action_View_Delete_Games){
            if(all_Games.size()!=0) {
                // dialog verifies user wants to delete all games
                new MaterialDialog.Builder(this)
                        .title("Deleting All Games...")
                        .content("Are you sure ?")
                        .contentGravity(GravityEnum.CENTER)
                        .backgroundColor(getResources().getColor(R.color.black))
                        .positiveText("DELETE")
                        .canceledOnTouchOutside(false)
                        .negativeText("CANCEL")
                        .titleColor(getResources().getColor(R.color.orange_red))
                        .contentColor(getResources().getColor(R.color.colorPrimary))
                        .positiveColor(getResources().getColor(R.color.red))
                        .negativeColor(getResources().getColor(R.color.gray))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // deletes all players and saves in it a copy ArrayList
                                // in case user wants to undo action and updates it for user to see
                                final ArrayList<Game_Info> games_Copy = all_Games;
                                all_Games = new ArrayList<Game_Info>();
                                saveGames(all_Games);
                                adapter = new ListViewAdapter_View_All_Games(context, R.layout.view_all_games_format, all_Games);
                                all_Games_ListView.setAdapter(adapter);
                                Snackbar.make(findViewById(android.R.id.content),
                                        "All Games Deleted!", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Snackbar.make(v, "Games not Deleted!",
                                                Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                        // undoes the deletion of all players, saves it, and updates it for user to see
                                        saveGames(games_Copy);
                                        all_Games = games_Copy;
                                        adapter = new ListViewAdapter_View_All_Games(context, R.layout.view_all_games_format, all_Games);
                                        all_Games_ListView.setAdapter(adapter);
                                    }
                                }).show();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss(); // dismisses dialog
                            }
                        })
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String getGameData(Integer[] selected_Games, boolean include_Player_Data, boolean include_Game_Data){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String game_Data = "Exported on " + currentDateTimeString + "\n\n";

        if(include_Game_Data==true) {
            for (int i = 0; i < selected_Games.length; i++) {
                Game_Info current_Game = all_Games.get(selected_Games[i]);
                game_Data += "Name of game: " + current_Game.getName_of_Game() + "\n";
                game_Data += "Number of Players: " + current_Game.getNumber_of_Players() + "\n";
                game_Data += "Duration of Game: " + current_Game.getGame_Duration_Formatted() + "\n";
                game_Data += "Date Created: " + current_Game.getDate_of_Game() + "\n\n";

                for (int j = 0; j < current_Game.getPlayers().size(); j++) {
                    Player_Info current_Player = current_Game.getPlayers().get(j);
                    game_Data += current_Player.getName() + "\t\tScore: " + current_Player.getCurrentScore() + "\n";
                }

                game_Data += "\n_________________________________\n";
            }
        }

        if(include_Player_Data==true) {
            game_Data += "\n##########################\n\n";

            for (int i = 0; i < all_Players.size(); i++) {
                game_Data += "Player Name: " + all_Players.get(i).getName() + "\n";
                game_Data += "Wins: " + all_Players.get(i).getGamesWon() + "\n";
                game_Data += "Losses: " + all_Players.get(i).getGamesLost() + "\n";
                game_Data += "Draws: " + all_Players.get(i).getTies() + "\n";
                game_Data += "Total Games: " + all_Players.get(i).getTotalGames() + "\n";
                game_Data += "************************************\n";
            }
        }

        return game_Data;
    }

    private void dialog_Choose_Games_to_Export(final boolean include_Player_Data, final boolean include_Game_Data){
        final ArrayList game_Names= new ArrayList();

        for(int i=0; i<all_Games.size(); i++){
            game_Names.add(all_Games.get(i).getName_of_Game());
        }

        if(include_Player_Data==true && include_Game_Data==false){
            final String gameData = getGameData(new Integer[]{0}, include_Player_Data, include_Game_Data);
            dialog_Export_Option(gameData);
        }
        else {
            new MaterialDialog.Builder(this)
                    .title("Select Game(s) Data to Export:")
                    .backgroundColor(getResources().getColor(R.color.black))
                    .positiveText("CONFIRM")
                    .items(game_Names)
                    .autoDismiss(false)
                    .canceledOnTouchOutside(false)
                    .negativeText("CLOSE")
                    .neutralText("SELECT ALL")
                    .neutralColor(getResources().getColor(R.color.orange))
                    .titleColor(getResources().getColor(R.color.orange_red))
                    .contentColor(getResources().getColor(R.color.colorPrimary))
                    .positiveColor(getResources().getColor(R.color.dark_green))
                    .negativeColor(getResources().getColor(R.color.gray))
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (dialog.getSelectedIndices().length == all_Games.size()) {
                                dialog.clearSelectedIndices(false);
                            } else {
                                dialog.selectAllIndices(false);
                            }
                        }
                    })
                    .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                            final String gameData = getGameData(which, include_Player_Data, include_Game_Data);

                            if (which.length != 0) {
                                dialog.dismiss();
                                dialog_Export_Option(gameData);
                            }
                            return false;
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss(); // dismisses dialog
                        }
                    })
                    .show();
        }
    }

    public ArrayList<Player_Info> getUserData() {
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

        if(savedArrayList == null)
            return new ArrayList<>();
        else
            return savedArrayList;
    }

    private void dialog_Export_Option(final String gameData){
        new MaterialDialog.Builder(context)
                .title("Choose Format:")
                .backgroundColor(getResources().getColor(R.color.black))
                .titleColor(getResources().getColor(R.color.orange_red))
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.gray))
                .neutralColor(getResources().getColor(R.color.red))
                .positiveText("Email")
                .negativeText("Copy to Clipboard")
                .neutralText("CLOSE")
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Score Keeper Games Data");
                        i.putExtra(Intent.EXTRA_TEXT, gameData);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                        }
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Score Keeper Games Data", gameData);
                        clipboard.setPrimaryClip(clip);
                        dialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Copied to Clipboard!",
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                })
                .show();
    }

    private void editGameName(final int position){
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Inflate your custom view with an Edit Text
        LayoutInflater objLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View snackView = objLayoutInflater.inflate(R.layout.snack_layout, null);

        final ImageView close_Snack = snackView.findViewById(R.id.close_Snack);
        final ImageView group_Button = snackView.findViewById(R.id.set_To_All);
        final Button game_Submit = snackView.findViewById(R.id.enter_Score_Snack_Button);
        final EditText game_Name_Submit = snackView.findViewById(R.id.enter_Score_Snack_Edittext);

        group_Button.setVisibility(View.GONE);
        game_Name_Submit.setHint("Changing " + all_Games.get(position).getName_of_Game() + " Name");
        game_Name_Submit.setInputType(InputType.TYPE_CLASS_TEXT);
        game_Name_Submit.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(12)
        });
        game_Name_Submit.requestFocus();

        //closes snackbar and shows end game button again
        close_Snack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(close_Snack.getApplicationWindowToken(), 0);
            }
        });

        // if user enters a proper score, then that is set as new score and saved
        game_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String game_Name_Entered = game_Name_Submit.getText().toString();
                if(!game_Name_Entered.equals("")){
                    if(uniqueGameName(game_Name_Entered)) {
                        all_Games.get(position).setName_of_Game(game_Name_Entered);
                        saveGames(all_Games);
                        adapter = new ListViewAdapter_View_All_Games(context, R.layout.view_all_games_format, all_Games);
                        all_Games_ListView.setAdapter(adapter);
                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(game_Submit.getApplicationWindowToken(), 0);
                        snackbar.dismiss();
                    }
                    else
                        game_Name_Submit.setError("Name Exists");
                }
                else
                    game_Name_Submit.setError("Blank");
            }
        });

        layout.addView(snackView, 0);
        snackbar.show();
    }

    private boolean uniqueGameName(String game){
        for(int i=0; i<all_Games.size(); i++){
            if(all_Games.get(i).getName_of_Game().equals(game))
                return false;
        }
        return true;
    }

    private void deletePlayer(final int position){
        final Game_Info game = all_Games.get(position);
        all_Games.remove(game);
        saveGames(all_Games);
        adapter.notifyDataSetChanged();

        Snackbar.make(findViewById(android.R.id.content), game.getName_of_Game() + " has been deleted!", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // if user pressed undo, then game is re-added and saved
                        all_Games.add(position, game);
                        adapter.notifyDataSetChanged();
                        saveGames(all_Games);
                        // notifies user that player has not been deleted
                        Snackbar.make(findViewById(android.R.id.content), all_Games.get(position).getName_of_Game() + " not deleted!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }).show();

        adapter.notifyDataSetChanged();
    }

    private ArrayList<Game_Info> getGameInfo() {
        ArrayList<Game_Info> savedArrayList = new ArrayList<Game_Info>();

        try {
            FileInputStream inputStream = openFileInput(getString(R.string.game_Data));
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
            FileOutputStream fileOutputStream = openFileOutput(getString(R.string.game_Data), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(games);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
