package com.akapps.scoretrackerv47.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.Dialogs.dialog_custom_Interval;
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
import com.afollestad.materialdialogs.MaterialDialog;
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

public class View_Game_Scores extends AppCompatActivity {
    private ArrayList<Game_Info> games;
    private ArrayList<Player_Info> players;
    private int view_Game_Mode, view_Game_Position;
    private LstViewAdapter_Game adapter;
    private ListView player_ListView;
    private FloatingActionButton one_Interval, two_Interval;
    public  FloatingActionButton custom_Interval;
    private FloatingActionMenu interval_Menu;
    private Chronometer game_Duration;
    private long pauseOffset;
    private boolean running;
    private com.google.android.material.floatingactionbutton.FloatingActionButton endGame_Button;
    private Context context;
    private AppCompatActivity View_Game_Scores;
    public int interval;
    private Toolbar myToolbar;
    private boolean orentationChange;

    @Override
    public void onBackPressed() {
        if(view_Game_Mode==2) {
            // if user pressed back, a dialog pops up to verify the action
            end_Game_Dialog();
        }
        else {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("ChronoTime", game_Duration.getBase());
        savedInstanceState.putBoolean("timer state", running);
        savedInstanceState.putInt("point interval", interval);
        savedInstanceState.putInt("view game mode", view_Game_Mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = this;
        View_Game_Scores = this;
        orentationChange = false;

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setTitle("Game Time");
        myToolbar.inflateMenu(R.menu.menu_game_page);

        game_Duration = findViewById(R.id.game_Duration_Chronometer);

        game_Duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view_Game_Mode==2) {
                    new MaterialDialog.Builder(View_Game_Scores)
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
            }
        });

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");
        if (theme.equals("Light")) {
            View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
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

        Intent mIntent = getIntent();
        view_Game_Mode = mIntent.getIntExtra("View Scores Mode", 0);
        view_Game_Position = mIntent.getIntExtra("Game Position", 0);

        games = getGameInfo();

        players = games.get(view_Game_Position).getPlayers();
        setTitle(games.get(view_Game_Position).getName_of_Game());
        resetPlayersLastPlay();
        adapter = new LstViewAdapter_Game(this, R.layout.game_player_format, games.get
                (view_Game_Position).getPlayers(), -1, view_Game_Position);
        player_ListView.setAdapter(adapter);
        endGame_Button.hide();

        if(savedInstanceState != null)
            view_Game_Mode = savedInstanceState.getInt("view game mode");

        if(view_Game_Mode==1){
            String current_Timer = games.get(view_Game_Position).getGame_Duration_Formatted();
            game_Duration.setText(current_Timer);
            running = false;
        }
        else if(savedInstanceState != null){

            if(games.get(view_Game_Position).isGame_Continued()==true)
                startGameView();

            interval = savedInstanceState.getInt("point interval");

            if(interval>2) {
                updateInterval(interval);
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

            orentationChange = true;
            running = savedInstanceState.getBoolean("timer state");
            game_Duration.setBase(savedInstanceState.getLong("ChronoTime"));

            if(running)
                game_Duration.start();
            else if(view_Game_Mode!=1){
                saveGames(games, "closing");
                Handler h =new Handler() ;
                h.postDelayed(new Runnable() {
                    public void run() {
                        finishAffinity();
                        System.exit(0);
                    }

                }, 6000);

                savedInstanceState.putBoolean("timer state", false);

                new MaterialDialog.Builder(View_Game_Scores)
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
                if(view_Game_Mode==2) {
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

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(score_Enter, InputMethodManager.SHOW_IMPLICIT);

                    //closes snackbar and shows end game button again
                    close_Snack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                            endGame_Button.show();
                        }
                    });

                    setToAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final int score = getInputtedScore(score_Enter);
                            if(score!=-1) {
                                new MaterialDialog.Builder(View_Game_Scores)
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
                                                    games = getGameInfo();
                                                    games.get(games.size()-1).setPlayers(players);
                                                    saveGames(games, "In progress");
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
                            int score = 0;
                            if (score_Enter.getText().toString().equals(""))
                                score_Enter.setError("Empty");
                            else {
                                score = Integer.valueOf(score_Enter.getText().toString());

                                if (score > -1 && score < 1000000) {
                                    players.get(position).setCurrentScore(score);
                                    games.get(view_Game_Position).setPlayers(players);
                                    saveGames(games, "In progress");
                                    adapter.notifyDataSetChanged();
                                    snackbar.dismiss();
                                    endGame_Button.show();
                                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(View_Game_Scores.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                } else
                                    Toast.makeText(View_Game_Scores, "Out of bounds (3 - 100,000)", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    layout.addView(snackView, 0);
                    snackbar.show();
                }
            }
        });


        player_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                endGame_Button.show();
                if(players.get(position).getLastScore()!=-1) {
                    new MaterialDialog.Builder(View_Game_Scores)
                            .title("Undo last play")
                            .content("Are you sure ?")
                            .contentGravity(GravityEnum.CENTER)
                            .backgroundColor(Color.LTGRAY)
                            .positiveText("UNDO")
                            .canceledOnTouchOutside(false)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    undoLastPlay(position);
                                }
                            })
                            .negativeText("CLOSE")
                            .positiveColor(getResources().getColor(R.color.colorPrimary))
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
                    interval = 1;
                    one_Interval.setColorNormal(getResources().getColor(R.color.gray));
                    two_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                    custom_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                    custom_Interval.setLabelText("#");
                    updateInterval(interval);
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
                    updateInterval(interval);
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
                one_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                two_Interval.setColorNormal(getResources().getColor(R.color.dark_black));
                custom_Interval.setColorNormal(getResources().getColor(R.color.black));
                dialog_custom_Interval dialogCustom_interval = new dialog_custom_Interval(View_Game_Scores, getWindow().getDecorView().findViewById(android.R.id.content), View_Game_Scores, 1, view_Game_Position);
                dialogCustom_interval.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialogCustom_interval.show();
                dialogCustom_interval.setCancelable(false);
            }
        });
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
        menu.clear(); //clears the current menu
        if(view_Game_Mode==1)
            getMenuInflater().inflate(R.menu.menu_view_page, menu);
        else
            getMenuInflater().inflate(R.menu.menu_game_page, menu);
        return true;
    }

    // gives action to menu items in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        // if user selects to reset scores, then a dialog pops up to verify action and resets if agreed
        else if(item.getItemId() == R.id.action_reset_Scores){
            new MaterialDialog.Builder(this)
                    .title("Reset Scores")
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
                    }
                }, 100);
            }
            else {
                closeButtonMenu();
            }
        }
        else if(item.getItemId() == R.id.action_startGame_View){
            startGameView();
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

    // resets player scores to zero
    private void resetScores(){
        for(int i=0; i<games.get(view_Game_Position).getPlayers().size(); i++){
            games.get(view_Game_Position).getPlayers().get(i).setCurrentScore(0);
        }
        adapter.notifyDataSetChanged();
    }

    private void undoLastPlay(int position){
        players.get(position).setCurrentScore(players.get(position).getLastScore());
        adapter.notifyDataSetChanged();
    }

    private void resetPlayersLastPlay(){
        for(int i=0; i<players.size(); i++){
            players.get(i).setLastScore(-1);
        }
    }

    // updates the interval in the adapter to be whatever is chosen by user and 1 by default
    public void updateInterval(int interval) {
        adapter = new LstViewAdapter_Game(View_Game_Scores, R.layout.game_player_format, players, interval, view_Game_Position);
        player_ListView.setAdapter(adapter);
    }

    private void startGameView() {
        if(games.get(view_Game_Position).get_Play_Able()==false){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            new MaterialDialog.Builder(this)
                    .title("Error:")
                    .content(R.string.contine_game_error)
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
        else {
            view_Game_Mode = 2;
            running = true;
            games.get(view_Game_Position).setGame_Continued(true);
            saveGames(games, "");
            invalidateOptionsMenu();
            adapter = new LstViewAdapter_Game(this, R.layout.game_player_format, games.get
                    (view_Game_Position).getPlayers(), interval, view_Game_Position);
            player_ListView.setAdapter(adapter);
            endGame_Button.show();

            if(running && !orentationChange) {
                pauseOffset = games.get(view_Game_Position).getPauseOffSet();
                game_Duration.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                game_Duration.start();
            }

        }
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

    public void end_Game_Dialog(){
        new MaterialDialog.Builder(View_Game_Scores)
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

    // method ends the game by changing game mode to 0 and by determining the winner, losers,
    // and the players with draws
    private void endGame(){
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
            games.get(view_Game_Position).setPlayers(players);
            games.get(view_Game_Position).setGame_Continued(false);
            saveGames(games, "Completed");
            Intent homePage= new Intent(View_Game_Scores, View_Games.class);
            startActivity(homePage);
        }

        else {

            ArrayList<Player_Info> updatePlayers = getUserData();

            // each player's total games is incremented by 1
            // player with the highest point games won is incremeted by one
            // if there are more than one player with the highest score, then their draws is incremented by 1
            // then everyone else games lost is incremented by 1
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setTotalGames(players.get(i).getTotalGames() + 1);
                updatePlayers.get(i).setTotalGames(updatePlayers.get(i).getTotalGames()+1);

                if (players.get(i).getCurrentScore() == high_Score.getCurrentScore() && winner == -1) {
                    players.get(i).setGamesWon(players.get(i).getGamesWon() + 1);
                    for(int j=0; j<updatePlayers.size(); j++){
                        if(updatePlayers.get(j).getName().equals(players.get(i).getName())){
                            updatePlayers.get(j).setGamesWon(updatePlayers.get(j).getGamesWon()+1);
                        }
                    }
                    winner = i;
                }

                else if (players.get(i).getCurrentScore() == high_Score.getCurrentScore() && winner != -1) {
                    players.get(i).setTies(players.get(i).getTies() + 1);
                    for(int j=0; j<updatePlayers.size(); j++){
                        if(updatePlayers.get(j).getName().equals(players.get(i).getName())){
                            updatePlayers.get(j).setTies(updatePlayers.get(j).getTies()+1);
                        }
                    }
                    multiple_Winners = true;
                }

                else {
                    players.get(i).setGamesLost(players.get(i).getGamesLost() + 1);
                    for(int j=0; j<updatePlayers.size(); j++){
                        if(updatePlayers.get(j).getName().equals(players.get(i).getName())){
                            updatePlayers.get(j).setGamesLost(updatePlayers.get(j).getGamesLost()+1);
                        }
                    }
                }
            }

            if (winner != -1 && multiple_Winners == true) {
                players.get(winner).setTies(players.get(winner).getTies() + 1);
                players.get(winner).setGamesWon(players.get(winner).getGamesWon() - 1);
                for(int j=0; j<updatePlayers.size(); j++){
                    if(updatePlayers.get(j).getName().equals(players.get(winner).getName())){
                        updatePlayers.get(j).setTies(updatePlayers.get(j).getTies()+1);
                        updatePlayers.get(j).setGamesWon(updatePlayers.get(j).getGamesWon()-1);
                    }
                }
            }

            // if this is the first game, then recent scores for each player at position 1 is populated
            // with their current scores and the position that they will start with next time is
            // incremented by 1.so the position 0 of recent scores does not contain a recent player
            // score value but instead the last position the player added their score to from their last game
            if (num_Of_Games.getTotalGames() == 0) {
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).setRecentScores(1, players.get(i).getCurrentScore());
                    players.get(i).setRecentScores(0, 2);
                    for(int j=0; j<updatePlayers.size(); j++){
                        if(updatePlayers.get(j).getName().equals(players.get(i).getName())){
                            updatePlayers.get(j).setRecentScores(1, players.get(i).getCurrentScore());
                            updatePlayers.get(j).setRecentScores(0, 2);
                        }
                    }
                }
            } else {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getRecentScores(0) > 5) {
                        players.get(i).setRecentScores(0, 1);
                        for(int j=0; j<updatePlayers.size(); j++){
                            if(updatePlayers.get(j).getName().equals(players.get(i).getName())){
                                if(updatePlayers.get(j).getRecentScores(0) > 5) {
                                    updatePlayers.get(j).setRecentScores(0, 1);
                                }
                                break;
                            }
                        }
                    }
                    players.get(i).setRecentScores(players.get(i).getRecentScores(0), players.get(i).getCurrentScore());
                    players.get(i).setRecentScores(0, players.get(i).getRecentScores(0 )+ 1);
                    for(int j=0; j<updatePlayers.size(); j++){
                        if (players.get(i).getRecentScores(0) > 5)
                            players.get(i).setRecentScores(0, 1);
                        if(updatePlayers.get(j).getName().equals(players.get(i).getName())){
                            if (updatePlayers.get(i).getRecentScores(0) > 5)
                                updatePlayers.get(i).setRecentScores(0, 1);
                            if(updatePlayers.get(i).getRecentScores(0)==0)
                                updatePlayers.get(i).setRecentScores(0,1);
                            updatePlayers.get(j).setRecentScores(updatePlayers.get(i).getRecentScores(0), players.get(i).getCurrentScore());
                            updatePlayers.get(j).setRecentScores(0, updatePlayers.get(j).getRecentScores(0) + 1);
                        }
                    }
                } // end of for loop
            }

            saveUserData(updatePlayers);
            games.get(view_Game_Position).setPlayers(players);
            games.get(view_Game_Position).setGame_Continued(false);
            saveGames(games, "Completed");
            Intent homePage= new Intent(View_Game_Scores, View_Games.class);
            startActivity(homePage);
        }
    }

    private void setScoreForAllPlayers(final int score) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                for(int position=0; position<players.size(); position++)
                    players.get(position).setCurrentScore(score);
                saveUserData(players);
                adapter.notifyDataSetChanged();
            }
        }, 50);
    }

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
                Toast.makeText(View_Game_Scores, "Out of bounds (3 - 100,000)", Toast.LENGTH_LONG).show();
        }
        return -1;
    }

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

    // retrieves player data objects from inside txt file
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
    // saves player data in objects inside txt file
    public void saveUserData(ArrayList<Player_Info> updatedPlayers) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(getString(R.string.user_Data), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(updatedPlayers);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGames(ArrayList<Game_Info> games, String gameStatus){

        if(gameStatus.equals("closing")){
            games.get(view_Game_Position).setPauseOffSet(0);
            games.get(view_Game_Position).setGame_Duration(0);

        }
        else if(!gameStatus.equals("")){
            games.get(view_Game_Position).setPauseOffSet(SystemClock.elapsedRealtime() - game_Duration.getBase());
            games.get(view_Game_Position).setGame_Duration(game_Duration.getBase());
            games.get(view_Game_Position).setGame_Duration_Formatted(game_Duration.getText().toString());
        }

        if(gameStatus.equals("Completed"))
            games.get(view_Game_Position).setGame_Satus("Completed");

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
