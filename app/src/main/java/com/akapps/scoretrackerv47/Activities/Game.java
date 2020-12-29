package com.akapps.scoretrackerv47.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.Dialogs.dialog_add_game;
import com.akapps.scoretrackerv47.Dialogs.dialog_custom_Interval;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.ListView_Adapters.LstViewAdapter_Game;
import com.akapps.scoretrackerv47.R;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.afollestad.materialdialogs.MaterialDialog;

public class Game extends AppCompatActivity {

    private ListView player_ListView;
    private FloatingActionMenu interval_Menu;
    private com.google.android.material.floatingactionbutton.FloatingActionButton endGame_Button;
    private ArrayList<Player_Info> players, totalPlayers;
    private LstViewAdapter_Game adapter;
    private FloatingActionButton one_Interval, two_Interval;
    public  FloatingActionButton custom_Interval;
    private Chronometer game_Duration;
    private long pauseOffset;
    private boolean running;
    private AppCompatActivity Game;
    private Context context;
    public  int interval;
    public ArrayList<Game_Info> games;
    private Toolbar myToolbar;
    private boolean gameNameSet;

    @Override
    public void onBackPressed() {
        // if user pressed back, a dialog pops up to verify the action of ending game
        end_Game_Dialog();
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("ChronoTime2", game_Duration.getBase());
        savedInstanceState.putBoolean("timer state2", running);
        savedInstanceState.putInt("point interval2", interval);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = this;
        Game=this;

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.inflateMenu(R.menu.menu_game_page);

        game_Duration = findViewById(R.id.game_Duration_Chronometer);

        game_Duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(Game)
                        .title("Game Timer")
                        .titleColor(getResources().getColor(R.color.colorPrimary))
                        .contentGravity(GravityEnum.CENTER)
                        .backgroundColor(getResources().getColor(R.color.black))
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                pauseChronometer();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startChronometer();
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                resetChronometer();
                            }
                        })
                        .negativeText("START")
                        .positiveText("STOP")
                        .neutralText("RESET")
                        .positiveColor(getResources().getColor(R.color.green))
                        .negativeColor(getResources().getColor(R.color.red))
                        .neutralColor(getResources().getColor(R.color.yellow_darker))
                        .show();
            }
        });

        // sets the background color to what the user specified in settings
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");

        View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (theme.equals("Light")) {
            root.setBackgroundColor(Color.WHITE);
        }
        // sets the screen to stay on during a game if user chooses option in settings
        if(preference.getBoolean("screen on",false))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // sets the timer to start during a game if user chooses option in settings
        if(preference.getBoolean("game timer",false) == false)
            game_Duration.setVisibility(View.INVISIBLE);

        // initializing of ListView and buttons
        endGame_Button= findViewById(R.id.done_Button);
        interval_Menu = findViewById(R.id.interval_Menu);
        one_Interval= findViewById(R.id.one_Interval);
        two_Interval= findViewById(R.id.two_Interval);
        custom_Interval= findViewById(R.id.custom_Interval);
        player_ListView= findViewById(R.id.players_List_Game);

        // sets the space between each item in ListView to be transparent
        // and have a space of 7. Also if List is empty, a text is shown
        player_ListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        player_ListView.setDividerHeight(7);

        // loading data from textfile
        totalPlayers = getUserData();

        // retrieves the value of continue game mode
        SharedPreferences sp = getSharedPreferences("Continue Game Mode", Activity.MODE_PRIVATE);
        final int continue_Game = sp.getInt("mode", -1);

        // if this is a new game, then game mode is set to one
        // this is done in case app closes mid game from an error or user closes it,
        // it allows user to continue where they left off in the game
        if(continue_Game==0 || continue_Game!=1){
            // out of all those player, those that are selected to play will be updated in ListView
            players = getPlayersPlaying(totalPlayers);
            resetPlayersLastPlay();
            adapter = new LstViewAdapter_Game(this, R.layout.game_player_format, players, 1);
            player_ListView.setAdapter(adapter);
            resetScores();
            saveUserDataInGame(players);
            prompt_User_For_Game_Name();
        }
        else if(continue_Game==1){
            try {
                ArrayList<Game_Info> games = getGameInfo();
                setTitle(games.get(games.size()-1).getName_of_Game());
                players = getUserDataPlaying();
                updateCustomInterval(1, games.size()-1);

                if(savedInstanceState!=null) {
                    running = savedInstanceState.getBoolean("timer state2");
                    if(!running)
                        outputError();
                    else {
                        game_Duration.setBase(savedInstanceState.getLong("ChronoTime2"));
                        game_Duration.start();
                    }
                }
                else
                    startChronometer();
            }
            catch(Exception exception) {
                Snackbar.make(findViewById(android.R.id.content), "An error just occurred, sorry! Please continue this game in View Games Page.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // doesn't continue game
                        SharedPreferences sp = getSharedPreferences("Continue Game Mode", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("mode", 0);
                        editor.commit();
                        restartApp();
                    }
                }).show();
            }
        }

        if(savedInstanceState != null){
            // retrieves the value of continue game name
            sp = getSharedPreferences("Game Name Set", Activity.MODE_PRIVATE);
            gameNameSet = sp.getBoolean("status", false);
            running = savedInstanceState.getBoolean("timer state2");
            interval = savedInstanceState.getInt("point interval2");
            games = getGameInfo();

            if(interval>2) {
                updateCustomInterval(interval, games.size() - 1);
                custom_Interval.setLabelText(Integer.toString(interval));
                custom_Interval.setColorNormal(getResources().getColor(R.color.gray));
            }
            else if(interval==1){
                updateInterval(interval);
                one_Interval.setColorNormal(getResources().getColor(R.color.gray));
            }
            else if(interval==2){
                updateInterval(interval);
                two_Interval.setColorNormal(getResources().getColor(R.color.gray));
            }

            if(gameNameSet && running) {
                game_Duration.setBase(savedInstanceState.getLong("ChronoTime2"));
                game_Duration.start();
            }
            else if(gameNameSet && !running){
                outputError();
            }
        }

        // makes sure the interval menu does not close on outside touch
        interval_Menu.setClosedOnTouchOutside(false);

        // if end game button is presses, then end game dialog pops up to verify action
        endGame_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end_Game_Dialog();
            }
        });

        player_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // hides end game floating action button so that snackbar can be seen
                endGame_Button.hide();
                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

                // Inflate your custom view with an Edit Text
                LayoutInflater objLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View snackView = objLayoutInflater.inflate(R.layout.snack_layout, null);

                final ImageView close_Snack = snackView.findViewById(R.id.close_Snack);
                final ImageView setToAll = snackView.findViewById(R.id.set_To_All);
                Button score_Submit = snackView.findViewById(R.id.enter_Score_Snack_Button);
                final EditText score_Enter = snackView.findViewById(R.id.enter_Score_Snack_Edittext);

                score_Enter.setHint("Enter Score for " + players.get(position).getName());
                score_Enter.requestFocus();

                //closes snackbar and shows end game button again
                close_Snack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(close_Snack.getApplicationWindowToken(), 0);
                        endGame_Button.show();
                    }
                });

                setToAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int score = getInputtedScore(score_Enter);
                        if(score!=-1) {
                            new MaterialDialog.Builder(Game)
                                    .title("Set all players scores to " + score + "?")
                                    .backgroundColor(getResources().getColor(R.color.black))
                                    .content("Are you sure ?")
                                    .titleColor(getResources().getColor(R.color.orange_red))
                                    .contentColor(getResources().getColor(R.color.colorPrimary))
                                    .contentGravity(GravityEnum.CENTER)
                                    .positiveText("CONFIRM")
                                    .canceledOnTouchOutside(false)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (score != -1) {
                                                setScoreForAllPlayers(score);
                                                saveUserDataInGame(players);
                                                games = getGameInfo();
                                                games.get(games.size()-1).setPlayers(players);
                                                saveGame();
                                                adapter.notifyDataSetChanged();
                                                snackbar.dismiss();
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(close_Snack.getApplicationWindowToken(), 0);
                                                endGame_Button.show();
                                            }
                                        }
                                    })
                                    .negativeText("CLOSE")
                                    .positiveColor(getResources().getColor(R.color.green))
                                    .negativeColor(getResources().getColor(R.color.gray))
                                    .show();
                        }
                    }
                });

                // if user enters a proper score, then that is set as new score and saved
                score_Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int score=0;
                        if(score_Enter.getText().toString().equals(""))
                            score_Enter.setError("Empty");
                        else {
                            score = Integer.valueOf(score_Enter.getText().toString());

                            if (score > -1 && score < 1000000) {
                                players.get(position).setCurrentScore(score);
                                saveUserDataInGame(players);
                                games = getGameInfo();
                                games.get(games.size()-1).setPlayers(players);
                                saveGame();
                                adapter.notifyDataSetChanged();
                                snackbar.dismiss();
                                endGame_Button.show();
                                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(Game.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            } else
                                Toast.makeText(Game, "Out of bounds (3 - 100,000)", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                layout.addView(snackView, 0);
                snackbar.show();
            }
        });


        player_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                endGame_Button.show();
                if(players.get(position).getLastScore()!=-1) {
                    new MaterialDialog.Builder(Game)
                            .title("Undo last play")
                            .titleColor(getResources().getColor(R.color.orange_red))
                            .contentColor(getResources().getColor(R.color.colorPrimary))
                            .content("Are you sure ?")
                            .contentGravity(GravityEnum.CENTER)
                            .backgroundColor(getResources().getColor(R.color.black))
                            .positiveText("UNDO")
                            .canceledOnTouchOutside(false)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    undoLastPlay(position);
                                }
                            })
                            .negativeText("CLOSE")
                            .positiveColor(getResources().getColor(R.color.green))
                            .negativeColor(getResources().getColor(R.color.gray))
                            .show();
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Earlier Scores",
                          Snackbar.LENGTH_LONG).setAction("Action", null);
                    View snackBarView = snackbar.getView();
                    TextView snack_Text = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    snack_Text.setTextColor(getResources().getColor(R.color.black));
                    snack_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    snack_Text.setTypeface(null, Typeface.BOLD);
                    snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.show();
                }
                return true;
            }
        });

        // if user clicks on the button to set interval to one, then the color of the button is set
        // to be darker and the other two button are set to be lighter
        // also, the interval is updated via the adapter and the menu closes
        one_Interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interval!=1) {
                    interval =1;
                    one_Interval.setColorNormal(getResources().getColor(R.color.gray));
                    two_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                    custom_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                    custom_Interval.setLabelText("#");
                    updateInterval(1);
                    closeButtonMenu();
                }
            }
        });

        // if user clicks on the button to set interval to two, then the color of the button is set
        // to be darker and the other two button are set to be lighter
        // also, the interval is updated via the adapter and the menu closes
        two_Interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interval!=2) {
                    interval = 2;
                    one_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                    two_Interval.setColorNormal(getResources().getColor(R.color.gray));
                    custom_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                    custom_Interval.setLabelText("#");
                    updateInterval(2);
                    closeButtonMenu();
                }
            }
        });

        // if user clicks on the button to set interval to be a custom interval, a dialog pops up
        // to take their input and sets that number to be the new interval
        // Also the color of the button is set to be darker and the other two button are set to be lighter
        // also, the interval is updated via the adapter and the menu closes
        custom_Interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                games = getGameInfo();
                one_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                two_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                custom_Interval.setColorNormal(getResources().getColor(R.color.gray));
                dialog_custom_Interval dialogCustom_interval = new dialog_custom_Interval(Game, getWindow().getDecorView().findViewById(android.R.id.content), Game, 0, games.size()-1);
                dialogCustom_interval.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialogCustom_interval.show();
                dialogCustom_interval.setCancelable(false);
            }
        });
    }

    private void restartApp() {
        // restarts app
        try{
            Intent mStartActivity = new Intent(context, HomePage.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startChronometer() {
        if (!running) {
            game_Duration.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            game_Duration.start();
            running = true;
        }
    }

    public void pauseChronometer() {
        if (running) {
            game_Duration.stop();
            pauseOffset = SystemClock.elapsedRealtime() - game_Duration.getBase();
            running = false;
        }
    }

    public void resetChronometer() {
        game_Duration.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    // creates a menu on the top toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_game_page, menu);
        return true;
    }

    // gives action to menu items in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if user selects to reset scores, then a dialog pops up to verify action and resets if agreed
        if(item.getItemId() == R.id.action_reset_Scores){
            new MaterialDialog.Builder(this)
                    .title("Resetting All Scores...")
                    .content("Are you sure ?")
                    .contentGravity(GravityEnum.CENTER)
                    .backgroundColor(getResources().getColor(R.color.black))
                    .titleColor(getResources().getColor(R.color.orange_red))
                    .contentColor(getResources().getColor(R.color.colorPrimary))
                    .positiveText("RESET")
                    .canceledOnTouchOutside(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            resetScores();
                        }
                    })
                    .negativeText("CLOSE")
                    .positiveColor(getResources().getColor(R.color.red))
                    .negativeColor(getResources().getColor(R.color.gray))
                    .show();
        }
        // if user selects interval menu item, then a menu appears for them to choose from
        else if(item.getItemId() == R.id.action_Interval){
            interval_Menu.hideMenuButton(false);
            if(interval_Menu.isMenuHidden()){
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        interval_Menu.showMenu(true);
                        interval_Menu.open(true);
                        interval_Menu.setClosedOnTouchOutside(true);
                    }
                }, 100);
            }
            else {
                closeButtonMenu();
            }
        }
        else if(item.getItemId() == R.id.action_Info_Game){
            new MaterialDialog.Builder(this)
                .title("Gestures:")
                        .content(R.string.game_gestures)
                        .contentGravity(GravityEnum.CENTER)
                        .backgroundColor(getResources().getColor(R.color.black))
                        .titleColor(getResources().getColor(R.color.orange_red))
                        .contentColor(getResources().getColor(R.color.colorPrimary))
                        .positiveText("CLOSE")
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .positiveColor(getResources().getColor(R.color.gray))
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }

    // closes interval menu
    public void closeButtonMenu(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                interval_Menu.close(true);
                interval_Menu.hideMenu(true);
            }
        }, 200);
    }

    // sets all players score to be the same score
    private void setScoreForAllPlayers(final int score) {
        for(int position=0; position<players.size(); position++)
            players.get(position).setCurrentScore(score);
    }

    // returns what the user entered in edittext
    private int getInputtedScore(EditText score_Enter) {
        int score=0;
        if(score_Enter.getText().toString().equals(""))
            score_Enter.setError("Empty");
        else {
            score = Integer.valueOf(score_Enter.getText().toString());

            if (score > -1 && score < 1000000) {
                return score;
            }
            else
                Toast.makeText(Game, "Out of bounds (3 - 100,000)", Toast.LENGTH_LONG).show();
        }
        return -1;
    }

    // opens a dialog to get input of desired game name
    private void prompt_User_For_Game_Name(){
        // creates dialog_edit_player object and initializes it
        dialog_add_game addGame = new dialog_add_game
                (Game, getWindow().getDecorView().findViewById(android.R.id.content),
                        context, players);
        // background of dialog set to light gray
        addGame.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        addGame.setCancelable(false); // user can touch outside dialog box, but it will not close
        addGame.show();               // shows dialog for edit player
    }

    // resets all player scores to zero
    private void resetScores(){
        for(int i=0; i<players.size(); i++){
            players.get(i).setCurrentScore(0);
        }
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

    // undoes last play for a specific user
    private void undoLastPlay(int position){
        players.get(position).setCurrentScore(players.get(position).getLastScore());
        saveUserDataInGame(players);
        adapter.notifyDataSetChanged();
    }

    // I dont know what this does but I am not playing with it
    private void resetPlayersLastPlay(){
        for(int i=0; i<players.size(); i++){
            players.get(i).setLastScore(-1);
        }
    }

    // updates the interval in the adapter to be whatever is chosen by user and 1 by default
    public void updateCustomInterval(int interval, int currentGamePosition) {
        adapter = new LstViewAdapter_Game(Game, R.layout.game_player_format, players, interval, currentGamePosition);
        player_ListView.setAdapter(adapter);
    }

    // updates the interval in the adapter to be whatever is chosen by user and 1 by default
    public void updateInterval(int interval) {
        adapter = new LstViewAdapter_Game(this, R.layout.game_player_format, players, interval);
        player_ListView.setAdapter(adapter);
    }

    // sets the game mode to be true
    public void gameInProgress(){
        SharedPreferences sp = getSharedPreferences("Continue Game Mode", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("mode", 1);
        editor.commit();
    }

    // sets the game name to be true
    public void gameNameSet(){
        SharedPreferences sp = getSharedPreferences("Game Name Set", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("status", true);
        editor.commit();
    }

    // returns an ArrayList object of player info players who are currently selected to play
    private ArrayList<Player_Info> getPlayersPlaying(ArrayList<Player_Info> totalPlayers){
        ArrayList<Player_Info> players_Playing = new ArrayList<>();
        for(int i=0; i<totalPlayers.size(); i++) {
            if(totalPlayers.get(i).getSelectedState()==true)
                players_Playing.add(totalPlayers.get(i));
        }
        return players_Playing;
    }

    // method ends the game by changing game mode to 0 and by determining the winner, losers,
    // and the players with draws
    private void endGame(){
        // changes game mode to 0, meaning that there are no games in progress
        SharedPreferences sp = getSharedPreferences("Continue Game Mode", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("mode", 0);
        editor.commit();

        // playerBuff is a copy of the current players
        ArrayList<Player_Info> playersBuff= players;
        // the highest score in playerBuff is set to to Player Info object highScore
        Player_Info high_Score= Collections.max(playersBuff, new Comparator<Player_Info>() {
            @Override
            public int compare(Player_Info one, Player_Info two) {
                return Integer.valueOf(one.getCurrentScore()).compareTo(two.getCurrentScore());
            }
        });

        int winner=-1;  // check to see if there is a winner and sets their position as winner
        boolean multiple_Winners= false; // if there is a tie, means no one won and there are draws

        // the highest number of games in playerBuff is set to to Player Info object num_Of_Games
        Player_Info num_Of_Games= Collections.max(playersBuff, new Comparator<Player_Info>() {
            @Override
            public int compare(Player_Info one, Player_Info two) {
                return Integer.valueOf(one.getTotalGames()).compareTo(two.getTotalGames());
            }
        });



        // if the highest score is zero, then that means user did not play a game "correctly"
        // and so nothing is saved and they are sent back to the Homepage
        if(high_Score.getCurrentScore()==0){
            // players that were not playing are added back to the totalPlayers ArrayList
            // totalPlayers is saved, scores reset, and user is directed back to HomePage
            for (int i = 0; i < totalPlayers.size(); i++) {
                if (totalPlayers.get(i).getSelectedState() == false) {
                    players.add(i, totalPlayers.get(i));
                }
            }

            saveGame("Completed");
            saveUserData(players);
            Intent homePage= new Intent(Game, HomePage.class);
            startActivity(homePage);
        }

        else {
            // if this is the first game, then recent scores for each player at position 1 is populated
            // with their current scores and the position that they will start with next time is
            // incremented by 1.So the position 0 of recent scores does not contain a recent player
            // score value but instead the last position the player added their score to from their last game
                for (int i = 0; i < players.size(); i++) {
                    if(players.get(i).getTotalGames()==0) {
                        players.get(i).setRecentScores(1, players.get(i).getCurrentScore());
                        players.get(i).setRecentScores(0, 2);
                    }
                    else{
                        if (players.get(i).getRecentScores(0) > 5)
                            players.get(i).setRecentScores(0, 1);
                        if(players.get(i).getRecentScores(0) == 0)
                            players.get(i).setRecentScores(0, 1);
                        players.get(i).setRecentScores(players.get(i).getRecentScores(0), players.get(i).getCurrentScore());
                        players.get(i).setRecentScores(0, players.get(i).getRecentScores(0) + 1);
                    }
                }

            // each player's total games is incremented by 1
            // player with the highest point games won is incremented by one
            // if there are more than one player with the highest score, then their draws is incremented by 1
            // then everyone else games lost is incremented by 1
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setTotalGames(players.get(i).getTotalGames() + 1);
                if (players.get(i).getCurrentScore() == high_Score.getCurrentScore() && winner == -1) {
                    players.get(i).setGamesWon(players.get(i).getGamesWon() + 1);
                    winner = i;
                } else if (players.get(i).getCurrentScore() == high_Score.getCurrentScore() && winner != -1) {
                    players.get(i).setTies(players.get(i).getTies() + 1);
                    multiple_Winners = true;
                } else
                    players.get(i).setGamesLost(players.get(i).getGamesLost() + 1);
            }

            if (winner != -1 && multiple_Winners == true) {
                players.get(winner).setTies(players.get(winner).getTies() + 1);
                players.get(winner).setGamesWon(players.get(winner).getGamesWon() - 1);
            }

            // players that were not playing are added back to the totalPlayers ArrayList
            // totalPlayers is saved, scores reset, and user is directed back to HomePage
            for (int i = 0; i < totalPlayers.size(); i++) {
                if (totalPlayers.get(i).getSelectedState() == false) {
                    players.add(i, totalPlayers.get(i));
                }
            }

            // reset user scores since game ended
            for(int i=0; i<players.size(); i++){
                players.get(i).setCurrentScore(0);
            }

            saveGame("Completed");
            saveUserData(players);
            Intent homePage = new Intent(Game, HomePage.class);
            startActivity(homePage);
        }
    }

    // retrieves all player data from textfile
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

    // retrieves players playing data from textfile
    public ArrayList<Player_Info> getUserDataPlaying() {
        ArrayList<Player_Info> savedArrayList = null;

        try {
            FileInputStream inputStream = openFileInput(getString(R.string.user_Data_in_Game));
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

    // retrieves game data from textfile
    public ArrayList<Game_Info> getGameInfo() {
        ArrayList<Game_Info> savedArrayList = new ArrayList<>();

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

    // saves game to textfile
    public void saveGame(){
        pauseOffset = SystemClock.elapsedRealtime();
        games.get(games.size()-1).setPauseOffSet(pauseOffset);
        games.get(games.size()-1).setGame_Duration(game_Duration.getBase());
        games.get(games.size()-1).setGame_Duration_Formatted(game_Duration.getText().toString());
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

    // saves game with current progress to textfile
    public void saveGame(String progress){
        games = getGameInfo();
        if(progress.equals("closing")){
            games.get(games.size() - 1).setPauseOffSet(0);
            games.get(games.size() - 1).setGame_Duration(0);
        }
        else {
            games.get(games.size() - 1).setGame_Satus(progress);
            pauseOffset = SystemClock.elapsedRealtime();
            games.get(games.size() - 1).setPauseOffSet(pauseOffset - game_Duration.getBase());
            games.get(games.size() - 1).setGame_Duration(game_Duration.getBase());
            games.get(games.size() - 1).setGame_Duration_Formatted(game_Duration.getText().toString());
        }

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

    public void saveUserData(ArrayList<Player_Info> arrayList) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(getString(R.string.user_Data), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(arrayList);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserDataInGame(ArrayList<Player_Info> arrayList) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(getString(R.string.user_Data_in_Game), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(arrayList);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end_Game_Dialog(){
        final SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        new MaterialDialog.Builder(Game)
                .title("Ending Game:")
                .content("Are you sure ?")
                .contentGravity(GravityEnum.CENTER)
                .backgroundColor(getResources().getColor(R.color.black))
                .titleColor(getResources().getColor(R.color.orange_red))
                .contentColor(getResources().getColor(R.color.colorPrimary))
                .positiveText("YES")
                .canceledOnTouchOutside(false)
                .negativeText("CANCEL")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeColor(getResources().getColor(R.color.gray))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        endGame();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void outputError(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Handler h =new Handler() ;
        h.postDelayed(new Runnable() {
            public void run() {
                // changes game mode to 0, so that user is not prompted to continue this game
                SharedPreferences sp = getSharedPreferences("Continue Game Mode", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("mode", 0);
                editor.commit();
                saveGame("closing");

                restartApp();
            }

        }, 6000);

        new MaterialDialog.Builder(Game)
                .title("You done messed up!")
                .titleColor(getResources().getColor(R.color.colorPrimary))
                .contentGravity(GravityEnum.CENTER)
                .contentColor(getResources().getColor(R.color.red))
                .content("You can't pause a timer and change the orientation\n" +
                        "so...I am gonna crash\n" +
                        "DON'T WORRY, GAME DATA WILL BE SAVED!")
                .backgroundColor(getResources().getColor(R.color.black))
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }
}