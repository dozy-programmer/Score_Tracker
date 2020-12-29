package com.akapps.scoretrackerv47.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.akapps.scoretrackerv47.Classes.Camera;
import com.akapps.scoretrackerv47.Dialogs.dialog_add_player;
import com.akapps.scoretrackerv47.Dialogs.dialog_edit_player;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.ListView_Adapters.ListViewAdapter_HomePage;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wang.avi.AVLoadingIndicatorView;

public class HomePage extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private ListView player_ListView;
    private FloatingActionMenu actionMenu;
    private FloatingActionButton add_Player, delete_All_Players, reset_All_Players, toggle_Players_Switch;
    private FloatingActionMenu add_Player_Land, delete_All_Players_Land, reset_All_Players_Land, toggle_Players_Switch_Land;
    private AppCompatActivity homePage;
    public  ArrayList<Player_Info> players, playerBuff, players_Copy;
    private ListViewAdapter_HomePage adapter;
    private CircularImageView player_Image;
    public AVLoadingIndicatorView loadingAnimation;
    private int sort_Selection, continue_Game;
    private boolean is_Players_Sorted;
    private Context context;
    private BottomNavigationBar bottomNavigationBar;
    private int lastSelectedPosition;
    private int bottom_Navigation_Color;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private int SELECT_PHOTO = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.akapps.scoretrackerv47.fileprovider";
    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;
    private String tempName, player_Edit_Name;
    private int player_Edit_Text_Color;
    public View view;
    int currentPlayerPosition;

    @Override
    public void onBackPressed() {
        SharedPreferences preference = getSharedPreferences("position", Activity.MODE_PRIVATE);
        int position = preference.getInt("navigation",-1);
        try {
            if(position>-1 && position<3) {
                bottomNavigationBar.setFirstSelectedPosition(position);
                switch (position){
                    case 1:
                        Intent home = new Intent(HomePage.this, View_Games.class);
                        startActivity(home);
                        break;

                    case 2:
                        Intent settings = new Intent(HomePage.this, Settings.class);
                        startActivity(settings);
                        break;
                }
            }
        }catch (Exception e){}
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        homePage = this;
        context = this;

        final int orientation = context.getResources().getConfiguration().orientation;

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preference.getString("theme","-1");

        bottom_Navigation_Color = R.color.black;

        SharedPreferences sp = getSharedPreferences("Game Name Set", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("status", false);
        editor.commit();

        if (theme.equals("Light")) {
            View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            root.setBackgroundColor(getResources().getColor(R.color.white));
            bottom_Navigation_Color = R.color.white;
        }

        player_ListView = findViewById(R.id.players_Listview);
        player_Image = findViewById(R.id.player_Image);
        actionMenu = findViewById(R.id.actions_Menu);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        loadingAnimation = findViewById(R.id.AVLoadingIndicatorView);

        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            add_Player = findViewById(R.id.add_Player);
            delete_All_Players = findViewById(R.id.delete_All);
            reset_All_Players = findViewById(R.id.reset_All);
            toggle_Players_Switch = findViewById(R.id.toggle_All_Players_Switch);
        }
        else if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            add_Player_Land = findViewById(R.id.add_Player);
            delete_All_Players_Land = findViewById(R.id.delete_All);
            reset_All_Players_Land = findViewById(R.id.reset_All);
            toggle_Players_Switch_Land = findViewById(R.id.toggle_All_Players_Switch);
        }

        bottomNavigationBar.setTabSelectedListener(this);
        lastSelectedPosition = 0;

        actionMenu.setClosedOnTouchOutside(true);

        actionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionMenu.isOpened())
                    actionMenu.close(true);
                else
                    actionMenu.open(true);

                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (add_Player_Land.getVisibility() == View.INVISIBLE) {
                        add_Player_Land.setVisibility(View.VISIBLE);
                        delete_All_Players_Land.setVisibility(View.VISIBLE);
                        toggle_Players_Switch_Land.setVisibility(View.VISIBLE);
                        reset_All_Players_Land.setVisibility(View.VISIBLE);
                    } else {
                        add_Player_Land.setVisibility(View.INVISIBLE);
                        delete_All_Players_Land.setVisibility(View.INVISIBLE);
                        toggle_Players_Switch_Land.setVisibility(View.INVISIBLE);
                        reset_All_Players_Land.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    if(add_Player.isHidden()){
                        add_Player.show(true);
                        delete_All_Players.show(true);
                        toggle_Players_Switch.show(true);
                        reset_All_Players.show(true);
                    }
                    else{
                        add_Player.hide(true);
                        delete_All_Players.hide(true);
                        toggle_Players_Switch.hide(true);
                        reset_All_Players.hide(true);
                    }
                }
            }
        });

        bottomNavigationBar
                .setBarBackgroundColor(bottom_Navigation_Color)
                .addItem(new BottomNavigationItem(R.drawable.icon_homepage, "Home").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.icon_view_games, "View Games").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.ic_settings, "Settings").setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

        // sets the space between each item in ListView to be transparent
        // and have a space of 10. Also if List is empty, a text is shown
        player_ListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        player_ListView.setDividerHeight(10);
        player_ListView.setEmptyView(findViewById(R.id.empty));
        is_Players_Sorted = false;

        // get user data and update it for user to see
        players = getUserData();
        updateListview(players);
        reset_Game_Continued();

        player_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!is_Players_Sorted)
                    // prompts user to long click instead of just clicking to edit player
                    notify_User_Via_Snackbar("Long click to edit player!", Snackbar.LENGTH_SHORT, 0);
            }
        });

        // determines if there was a game that did not finish and sets continue_Game to be 1 or 0
        sp = getSharedPreferences("Continue Game Mode", Activity.MODE_PRIVATE);
        continue_Game = sp.getInt("mode", -1);

        // if continue_Game is 1, last game was not completed and prompts user to finish or delete it
        if(continue_Game==1) {
            ArrayList<Player_Info> totalPlayers = getUserData();
            ArrayList<Player_Info> playersPlaying = getUserData(1);

            for (int i = 0; i < totalPlayers.size(); i++) {
                if (totalPlayers.get(i).getSelectedState() == false) {
                    playersPlaying.add(i, totalPlayers.get(i));
                }
            }

            new MaterialDialog.Builder(this)
                    .title("Game in Progress:")
                    .content("Would you like to continue game ?")
                    .contentGravity(GravityEnum.CENTER)
                    .positiveText("CONTINUE")
                    .backgroundColor(getResources().getColor(R.color.black))
                    .titleColor(getResources().getColor(R.color.orange_red))
                    .contentColor(getResources().getColor(R.color.colorPrimary))
                    .canceledOnTouchOutside(false)
                    .negativeText("CANCEL")
                    .positiveColor(getResources().getColor(R.color.green))
                    .negativeColor(getResources().getColor(R.color.gray))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // closes dialog and goes to activity game so user can finish game
                            dialog.dismiss();
                            Intent startGame = new Intent(homePage, Game.class);
                            startActivity(startGame);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // since user doesn't want to finish game, mode is set to 0
                            SharedPreferences sp = getSharedPreferences("Continue Game Mode",
                                    Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("mode", 0);
                            editor.commit();
                            ArrayList<Game_Info> all_Games = getGameInfo();
                            all_Games.get(all_Games.size()-1).setGame_Duration(0);
                            all_Games.get(all_Games.size()-1).setGame_Duration_Formatted("00:00");
                            all_Games.get(all_Games.size()-1).setPauseOffSet(0);
                            resetScores();          // scores from last player game are reset
                            saveUserData(players);  // player data is saved
                            saveGames(all_Games);
                            // notifies user that game was deleted
                            notify_User_Via_Snackbar("Game Ended!",  Snackbar.LENGTH_SHORT, 1);
                            dialog.dismiss();       // dismissed dialog
                        }
                    }).show();
        }

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            // open a dialog for user to enter a players name and once completed, then user is added to list
            add_Player_Land.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    }
                    else{
                        view= v;
                        players = getUserData();
                        // creates dialog_add_player object and initializes it
                        if(players.size()<300) {
                            dialog_add_player addPlayer = new dialog_add_player(homePage, v,
                                    adapter, player_ListView, homePage);
                            // background of dialog set to light gray
                            addPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                            addPlayer.show();               // shows dialog for adding player
                            addPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
                        }
                        else
                            notify_User_Via_Snackbar("Max players is 300!", Snackbar.LENGTH_LONG, 1);
                    }
                    actionMenu.close(true);
                }
            });

            delete_All_Players_Land.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    }
                    else {
                        deleteAllPlayers();
                    }
                }
            });

            reset_All_Players_Land.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    }
                    else {
                        resetAllPlayers();
                    }
                }
            });

            toggle_Players_Switch_Land.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    } else {
                        new MaterialDialog.Builder(context)
                                .title("Toggling All Players...")
                                .titleColor(getResources().getColor(R.color.orange_red))
                                .contentGravity(GravityEnum.CENTER)
                                .backgroundColor(getResources().getColor(R.color.black))
                                .positiveText("ON")
                                .canceledOnTouchOutside(false)
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        toggle_All_Players(true);
                                        updateListview(players);
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        toggle_All_Players(false);
                                        updateListview(players);
                                    }
                                })
                                .neutralText("CLOSE")
                                .negativeText("OFF")
                                .neutralColor(getResources().getColor(R.color.gray))
                                .positiveColor(getResources().getColor(R.color.green))
                                .negativeColor(getResources().getColor(R.color.red))
                                .show();
                    }
                }
            });
        }
        else if(orientation == Configuration.ORIENTATION_PORTRAIT) {

            // open a dialog for user to enter a players name and once completed, then user is added to list
            add_Player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    } else {
                        view = v;
                        players = getUserData();
                        // creates dialog_add_player object and initializes it
                        if(players.size()<300) {
                            dialog_add_player addPlayer = new dialog_add_player(homePage, v,
                                    adapter, player_ListView, homePage);
                            // background of dialog set to light gray
                            addPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                            addPlayer.show();               // shows dialog for adding player
                            addPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
                        }
                        else
                            notify_User_Via_Snackbar("Max players is 300!", Snackbar.LENGTH_LONG, 1);
                    }
                    actionMenu.close(true);
                }
            });

            delete_All_Players.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    }
                    else {
                        deleteAllPlayers();
                    }
                }
            });

            reset_All_Players.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    }
                    else {
                        resetAllPlayers();
                    }
                }
            });

            toggle_Players_Switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_Players_Sorted) {
                        Toast prompt = Toast.makeText(context, "Close toolbar!", Toast.LENGTH_LONG);
                        View toastView = prompt.getView();
                        TextView toast_Text = toastView.findViewById(android.R.id.message);
                        prompt.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast_Text.setTextColor(getResources().getColor(R.color.black));
                        toast_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        toast_Text.setTypeface(null, Typeface.BOLD);
                        toastView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        prompt.show();
                    } else {
                        new MaterialDialog.Builder(context)
                                .title("Toggling All Players...")
                                .contentGravity(GravityEnum.CENTER)
                                .titleColor(getResources().getColor(R.color.orange_red))
                                .backgroundColor(getResources().getColor(R.color.black))
                                .positiveText("ON")
                                .canceledOnTouchOutside(false)
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        toggle_All_Players(true);
                                        updateListview(players);
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        toggle_All_Players(false);
                                        updateListview(players);
                                    }
                                })
                                .neutralText("CLOSE")
                                .negativeText("OFF")
                                .neutralColor(getResources().getColor(R.color.gray))
                                .positiveColor(getResources().getColor(R.color.green))
                                .negativeColor(getResources().getColor(R.color.red))
                                .show();
                    }
                }
            });

        }

        // if a player in the list is long pressed, then a dialog pops up allowing user to edit player
        player_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!is_Players_Sorted) {
                    // creates dialog_edit_player object and initializes it
                    dialog_edit_player editPlayer = new dialog_edit_player
                            (homePage, getWindow().getDecorView().findViewById(android.R.id.content), position,
                                    adapter, homePage, player_ListView, view);
                    // background of dialog set to light gray
                    editPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                    editPlayer.show();               // shows dialog for edit player
                    editPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
                }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(this.getIntent());
            }
            else{
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("icon mode", false);
                editor.commit();
            }
        }
    }

    public void launchCamera(String tempPlayerName, String player_Name, int text_Color) {

        tempName = tempPlayerName;
        player_Edit_Name = player_Name;
        player_Edit_Text_Color = text_Color;

        try{
            int currentPlayer = Integer.valueOf(tempPlayerName);
            if(currentPlayer>=0) {
                currentPlayerPosition = currentPlayer;
                REQUEST_IMAGE_CAPTURE =2;
            }
        }catch (Exception e){}

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = Camera.createTempImageFile(context);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void getImageFromGallery(String currentName){
        SELECT_PHOTO=2;
        tempName = currentName;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setOrientation(boolean status){
        if(status) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void getImageFromGalleryEdit(String currentName, int currentPlayerPosition, boolean takePic, int text_Color){
        player_Edit_Name = currentName;
        player_Edit_Text_Color = text_Color;

        if(takePic) {
            REQUEST_IMAGE_CAPTURE = 2;
        }
        this.currentPlayerPosition = currentPlayerPosition;
        SELECT_PHOTO=2;
        tempName = currentName;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(SELECT_PHOTO == 2 && resultCode == RESULT_OK && REQUEST_IMAGE_CAPTURE==1){
            // choosing image in adding player
            Uri selectedImageURI = data.getData();
            dialog_add_player addPlayer = new dialog_add_player(homePage, view,
                    adapter, player_ListView, homePage,tempName, selectedImageURI.toString());
            // background of dialog set to light gray
            addPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
            addPlayer.show();               // shows dialog for adding player
            addPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
            SELECT_PHOTO=1;
        }
        else if(REQUEST_IMAGE_CAPTURE == 2 && SELECT_PHOTO == 2 && resultCode == RESULT_OK){
            // choosing image in edit player
            Uri selectedImageURI = data.getData();
            dialog_edit_player editPlayer = new dialog_edit_player(homePage, view, currentPlayerPosition,
                    adapter, homePage, player_ListView, selectedImageURI.toString(), view, player_Edit_Name, player_Edit_Text_Color);
            // background of dialog set to light gray
            editPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
            editPlayer.show();               // shows dialog for adding player
            editPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
            REQUEST_IMAGE_CAPTURE = 1;
            SELECT_PHOTO = 1;
        }
        else if (REQUEST_IMAGE_CAPTURE == 1 && resultCode == RESULT_OK) {
            mResultsBitmap = Camera.resamplePic(context, mTempPhotoPath);
            // Delete the temporary image file
            Camera.deleteImageFile(context, mTempPhotoPath);

            Random random = new Random();
            int playerNum = random.nextInt(100000);

            // Save the image
            String full_photoPath = Camera.saveImage(context, mResultsBitmap, String.valueOf(playerNum));
            // creates dialog_add_player object and initializes it
            dialog_add_player addPlayer = new dialog_add_player(homePage, view,
                    adapter, player_ListView, homePage,tempName, full_photoPath);
            // background of dialog set to light gray
            addPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
            addPlayer.show();               // shows dialog for adding player
            addPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
        }
        else if (REQUEST_IMAGE_CAPTURE == 2 && resultCode == RESULT_OK) {
            mResultsBitmap = Camera.resamplePic(context, mTempPhotoPath);
            // Delete the temporary image file
            Camera.deleteImageFile(context, mTempPhotoPath);

            Random random = new Random();
            int playerNum = random.nextInt(100000);

            // Save the image
            String full_photoPath = Camera.saveImage(context, mResultsBitmap, String.valueOf(playerNum));
            // creates dialog_add_player object and initializes it
            dialog_edit_player editPlayer = new dialog_edit_player(homePage, view, currentPlayerPosition,
                    adapter, homePage, player_ListView, full_photoPath, view, player_Edit_Name, player_Edit_Text_Color);
            // background of dialog set to light gray
            editPlayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
            editPlayer.show();               // shows dialog for adding player
            editPlayer.setCancelable(false); // user can touch outside dialog box, but it will not close
            REQUEST_IMAGE_CAPTURE = 1;
        }
    }

    @Override
    public void onTabSelected(int position) {
        bottom_Navigation_Position_Update(lastSelectedPosition);
        lastSelectedPosition = position;

        if(lastSelectedPosition == 0){
        }
        else if(lastSelectedPosition == 1){
            Intent startGame = new Intent(homePage, View_Games.class);
            startActivity(startGame);
        }
        else if(lastSelectedPosition==2){
            Intent startGame = new Intent(homePage, Settings.class);
            startActivity(startGame);
        }
    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {
    }
    // creates a menu in the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.clear(); //clears the current menu
        if (is_Players_Sorted)
            getMenuInflater().inflate(R.menu.menu_apply_changes, menu);
        else
            getMenuInflater().inflate(R.menu.menu_home_page, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // updates player values
        players= getUserData();

        // if user selects start game (play button), then a game is started
        if (item.getItemId() == R.id.action_startGame) {
            // determines how many players user selected to want to play and sets the num to players_Playing
            int players_Playing= playersPlaying(players);
            if (players.size()==0) {
                // if there are no player, notifies user
                notify_User_Via_Snackbar("No Players to play a game! :(",Snackbar.LENGTH_SHORT, 1);
            }
            else if(players_Playing==0)
                // if no players are selected, it notifies user
                notify_User_Via_Snackbar("Add players to play a game!",Snackbar.LENGTH_SHORT, 1);
            else {
                // since there are players selected to play game, then user proceeds to Game activity
                Intent startGame = new Intent(homePage, Game.class);
                startActivity(startGame);
            }
        } else if (item.getItemId() == R.id.by_Highest) {
            // by selecting highest from menu, user gets to sort players by highest wins, losses & draws
            if(players.size()!=0)
                sort_Players_By(1);
            else // user is notified that are no players
                notify_User_Via_Snackbar("Add players to play a game!",Snackbar.LENGTH_SHORT, 1);
        }
        else if (item.getItemId() == R.id.by_Lowest) {
            // by selecting lowest from menu, user gets to sort players by lowest wins, losses & draws
            if(players.size()!=0)
                sort_Players_By(0);
            else  // user is notified that are no players
                notify_User_Via_Snackbar("Add players to play a game!",Snackbar.LENGTH_SHORT, 1);
        }
        // reverts list of players to initial order for user to see
        else if(item.getItemId() == R.id.revert_Original){
            players = getUserData();

            if(players.size()!=0) {// if there are players, then list is reverted
                updateListview(players);
                notify_User_Via_Snackbar("List reverted to original!", Snackbar.LENGTH_LONG, 1);
            }
            else                  // user is notified that are no players
                notify_User_Via_Snackbar("There are no players to revert scores for!",Snackbar.LENGTH_LONG, 1);
        }
        // if user selects recent scores from menu, they are sent to Scores activity to view scores
        else if(item.getItemId() == R.id.action_RecentScores){
            // if there are players, then user can go to Scores activity to view scores
            if(players.size()!=0) {
                Intent seeScores = new Intent(homePage, Scores.class);
                startActivity(seeScores);
            }
            else
                // notifies user if there are no players
                notify_User_Via_Snackbar("Add players to play a game!",Snackbar.LENGTH_SHORT, 1);
        }
        // sort option in menu opens a sub menu allowing user to sort by highest/lowest or revert to original
        else if(item.getItemId() == R.id.action_Sort){
            if(players.size()==0)
                notify_User_Via_Snackbar("There are no players to sort!", Snackbar.LENGTH_LONG, 0);
        }
        else if(item.getItemId() == R.id.action_Instructions){
            new MaterialDialog.Builder(this)
                    .title("Instructions:")
                    .content(getText(R.string.home_gestures))
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
        // takes user to Bugs_Suggestions activity
        else if(item.getItemId() == R.id.action_Suggestions){
            Intent start = new Intent(homePage, Bugs_Suggestions.class);
            startActivity(start);
        }
        else if(item.getItemId() == R.id.action_Info){
            Intent start = new Intent(homePage, AboutPage.class);
            startActivity(start);
        }
        else if(item.getItemId() == R.id.action_Apply){
            saveUserData(playerBuff);
            is_Players_Sorted = false;
            invalidateOptionsMenu();
            notify_User_Via_Snackbar("Order of players saved", Snackbar.LENGTH_LONG, 0);
        }
        else if(item.getItemId() == R.id.action_Cancel){
            is_Players_Sorted = false;
            invalidateOptionsMenu();
            notify_User_Via_Snackbar("Order not saved but updated", Snackbar.LENGTH_LONG, 0);
        }
        return super.onOptionsItemSelected(item);
    }
    // resets all player scores and sets them to 0
    private void resetScores(){
        for(int position=0; position<players.size(); position++){
            players.get(position).setCurrentScore(0);
        }
        adapter.notifyDataSetChanged();
    }
    private void game_Playable_Status(boolean status){
        ArrayList<Game_Info> all_Games = getGameInfo();

        for(int i=0; i<all_Games.size(); i++){
            for(int j=0; j<all_Games.get(i).getPlayers().size(); j++){
                for(int k=0; k<players.size(); k++){
                    if(all_Games.get(i).getPlayers().get(j).getName().equals(players.get(k).getName())) {
                        all_Games.get(i).set_Play_Able(status);
                        saveGames(all_Games);
                    }
                }
            }
        }
    }
    private void reset_Game_Continued(){
        ArrayList<Game_Info> all_Games = getGameInfo();

        for(int i=0; i<all_Games.size(); i++){
            all_Games.get(i).setGame_Continued(false);
        }

        saveGames(all_Games);
    }
    // sorts player by number of wins
    private void sort_Players_By_Wins(int low_or_high) {
        // creates copy of player
        playerBuff = players;
        String message = "";
        // uses Collections object to sort player objects by wins
        if (low_or_high ==0) {
            message = "Sorted by lowest wins...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(one.getGamesWon()).compareTo(two.getGamesWon());
                }
            });
        } else {
            message = "Sorted by highest wins...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(two.getGamesWon()).compareTo(one.getGamesWon());
                }
            });
        }
        is_Players_Sorted = true;
        invalidateOptionsMenu();
        notify_User_Via_Snackbar(message + "Save Order?", Snackbar.LENGTH_INDEFINITE , 1);
        updateListview(playerBuff); // updates list with this copy of sorted players
    }
    // sorts player by number of losses
    private void sort_Players_By_Losses(int low_or_high) {
        // creates copy of player
        playerBuff = players;
        String message = "";
        // uses Collections object to sort player objects by losses
        if (low_or_high ==0) {
            message = "Sorted by lowest losses...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(one.getGamesLost()).compareTo(two.getGamesLost());
                }
            });
        } else {
            message ="Sorted by highest losses...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(two.getGamesLost()).compareTo(one.getGamesLost());
                }
            });
        }
        is_Players_Sorted = true;
        invalidateOptionsMenu();
        notify_User_Via_Snackbar(message +"Save Order?", Snackbar.LENGTH_INDEFINITE , 1);
        updateListview(playerBuff); // updates list with this copy of sorted players
    }
    // sorts player by number of draws
    private void sort_Players_By_Draws(int low_or_high) {
        // creates copy of player
        playerBuff = players;
        String message = "";
        // uses Collections object to sort player objects by draws
        if (low_or_high ==0) {
            message = "Sorted by lowest draws...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(one.getTies()).compareTo(two.getTies());
                }
            });
        } else {
            message = "Sorted by highest draws...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(two.getTies()).compareTo(one.getTies());
                }
            });
        }
        is_Players_Sorted = true;
        invalidateOptionsMenu();
        notify_User_Via_Snackbar(message + "Save Order?", Snackbar.LENGTH_INDEFINITE , 1);
        updateListview(playerBuff); // updates list with this copy of sorted players
    }

    // sorts player by number of wins
    private void sort_Players_By_Games_Played(int low_or_high) {
        // creates copy of player
        playerBuff = players;
        String message = "";
        // uses Collections object to sort player objects by wins
        if (low_or_high ==0) {
            message = "Sorted by lowest number of games played...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(one.getTotalGames()).compareTo(two.getTotalGames());
                }
            });
        } else {
            message = "Sorted by highest number of games played...";
            Collections.sort(playerBuff, new Comparator<Player_Info>() {
                @Override
                public int compare(Player_Info one, Player_Info two) {
                    return Integer.valueOf(two.getTotalGames()).compareTo(one.getTotalGames());
                }
            });
        }
        is_Players_Sorted = true;
        invalidateOptionsMenu();
        notify_User_Via_Snackbar(message + "Save Order?", Snackbar.LENGTH_INDEFINITE , 1);
        updateListview(playerBuff); // updates list with this copy of sorted players
    }

    private void sort_Players_By(final int high_or_low) {
        ArrayList<String> sort_By = new ArrayList();

        sort_By.add("Wins");
        sort_By.add("Losses");
        sort_By.add("Draws");
        sort_By.add("Games Played");

        // dialog allows user to chooses to sort players by wins, losses, or draws
        new MaterialDialog.Builder(this)
                .title("Sorting by:")
                .titleColor(getResources().getColor(R.color.orange_red))
                .items(sort_By)
                .itemsColor(getResources().getColor(R.color.colorPrimary))
                .canceledOnTouchOutside(false)
                .backgroundColor(getResources().getColor(R.color.black))
                .widgetColorRes(R.color.colorPrimary)
                .positiveText("CONFIRM")
                .positiveColor(getResources().getColor(R.color.green))
                .negativeText("CLOSE")
                .negativeColor(getResources().getColor(R.color.gray))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sort_Selection = dialog.getSelectedIndex();
                        if (sort_Selection == 0)
                            sort_Players_By_Wins(high_or_low);
                        else if (sort_Selection == 1)
                            sort_Players_By_Losses(high_or_low);
                        else if(sort_Selection == 2)
                            sort_Players_By_Draws(high_or_low);
                        else
                            sort_Players_By_Games_Played(high_or_low);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // if revert is selected, player is set to initial players positions
                        dialog.dismiss();
                    }
                })
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        dialog.setSelectedIndex(which);
                        if (text.equals("Wins"))
                            sort_Selection = 0;
                        else if (text.equals("Losses"))
                            sort_Selection = 1;
                        else if (text.equals("Draws"))
                            sort_Selection = 2;
                        else
                            sort_Selection = 3;
                        return true;
                    }
                })
                .show();
    }
    // method resets all player data
    private ArrayList<Player_Info> reset_All_Player_Data(ArrayList<Player_Info> players){
        for(int position=0; position<players.size(); position++) {
            players.get(position).setGamesWon(0);
            players.get(position).setGamesLost(0);
            players.get(position).setTies(0);
            players.get(position).setTotalGames(0);
            players.get(position).setRecentScores(new int[6]);
        }
        return players;
    }

    private void toggle_All_Players(boolean state) {

        if (players.size() == 0)
            notify_User_Via_Snackbar("No Players !", Snackbar.LENGTH_LONG, 1);
        else{
            for(int position=0; position<players.size(); position++)
                players.get(position).setSelectedState(state);
        }
        saveUserData(players);
    }

    private void deleteAllPlayers() {
        // if there are no players, notify user
        if (players.size() == 0)
            notify_User_Via_Snackbar("No Players to delete!", Snackbar.LENGTH_LONG, 1);
        else {
            // dialog verifies user wants to delete player
            new MaterialDialog.Builder(this)
                    .title("Deleting All Players...")
                    .content("Are you sure ?\n\n*Players deleted cannot continue a previous game\n...unless they are re-added!*")
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
                            try {
                                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        + "/ScoreTracker_Players/";
                                File dir = new File(root);
                                deleteRecursive(dir);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                            }catch (Exception c){}
                            new DeletePlayers().execute();
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

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    private void resetAllPlayers(){
        // if there are no players, notify user
        if(players.size()==0)
            notify_User_Via_Snackbar("There are no scores to reset!", Snackbar.LENGTH_LONG, 1);
        else {
            // dialog verifies user wants to reset player
            new MaterialDialog.Builder(this)
                    .title("Resetting Scores...")
                    .content("Are you sure ?")
                    .contentGravity(GravityEnum.CENTER)
                    .backgroundColor(getResources().getColor(R.color.black))
                    .positiveText("RESET")
                    .canceledOnTouchOutside(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new resetPlayers().execute();
                        }
                    })
                    .negativeText("CLOSE")
                    .contentColor(getResources().getColor(R.color.colorPrimary))
                    .titleColor(getResources().getColor(R.color.orange_red))
                    .positiveColor(getResources().getColor(R.color.red))
                    .negativeColor(getResources().getColor(R.color.gray))
                    .show();
        }
    }

    // updates list of players for user to see
    public void updateListview(ArrayList<Player_Info> players){
        adapter = new ListViewAdapter_HomePage(homePage, R.layout.homepage_player_format, players);
        player_ListView.setAdapter(adapter);
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

    // retrieves player data objects from inside txt file
    public ArrayList<Player_Info> getUserData(int game) {
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

    private ArrayList<Game_Info> getGameInfo() {
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
    // determines how many players are selected to play
    private int playersPlaying(ArrayList<Player_Info> totalPlayers){
        int numberOfPlayers=0;
        for(int position=0; position < totalPlayers.size(); position++) {
            if(totalPlayers.get(position).getSelectedState()==true)
                numberOfPlayers++;
        }
        return numberOfPlayers;
    }
    // notifies user via message
    public void notify_User_Via_Snackbar(String message, int message_Duration ,int top_of_Screen){
        if(top_of_Screen==1) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message,
                    message_Duration).setAction("Action", null);
            View snackbarView = snackbar.getView();
            TextView snack_Text = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snack_Text.setTextColor(getResources().getColor(R.color.black));
            snack_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            snack_Text.setTypeface(null, Typeface.BOLD);
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbarView.setLayoutParams(params);
            snackbar.show();
        }
        if(top_of_Screen==0){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message,
                    message_Duration).setAction("Action", null);
            View snackBarView = snackbar.getView();
            TextView snack_Text = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snack_Text.setTextColor(getResources().getColor(R.color.black));
            snack_Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            snack_Text.setTypeface(null, Typeface.BOLD);
            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();
        }
    }
    private class DeletePlayers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            actionMenu.close(true);
            loadingAnimation.smoothToShow();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // deletes all players and saves in it a copy ArrayList
            // in case user wants to undo action and updates it for user to see
            players = getUserData();
            players_Copy = players;
            game_Playable_Status(false);
            players = new ArrayList<>();
            saveUserData(players);
            Snackbar.make(findViewById(android.R.id.content),
                    "All Players Deleted!", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new undoDeletePlayers().execute();
                }
            }).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new ListViewAdapter_HomePage(homePage, R.layout.homepage_player_format, players);
            player_ListView.setAdapter(adapter);
            loadingAnimation.smoothToHide();
        }
    }
    private class undoDeletePlayers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            actionMenu.close(true);
            loadingAnimation.smoothToShow();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // undoes the deletion of all players, saves it, and updates it for user to see
            saveUserData(players_Copy);
            players = players_Copy;
            game_Playable_Status(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new ListViewAdapter_HomePage(homePage, R.layout.homepage_player_format, players_Copy);
            player_ListView.setAdapter(adapter);
            loadingAnimation.smoothToHide();
            Snackbar.make(findViewById(android.R.id.content), "Players not Deleted!",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    private class resetPlayers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            actionMenu.close(true);
            loadingAnimation.smoothToShow();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // a copy of player data is set to ArrayList player_Copy
            players_Copy = getUserData();

            // then copy of players scores are reset, saved, and updated for user to see
            players_Copy = reset_All_Player_Data(players_Copy);
            saveUserData(players_Copy);
            Snackbar.make(findViewById(android.R.id.content),
                    "All Players Reset!", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new undoresetPlayers().execute();
                }
            }).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players_Copy);
            player_ListView.setAdapter(adapter);
            loadingAnimation.smoothToHide();
        }
    }
    private class undoresetPlayers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            actionMenu.close(true);
            loadingAnimation.smoothToShow();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // notifies user that player scores were redo and give them option to undo
            saveUserData(players);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players);
            player_ListView.setAdapter(adapter);
            loadingAnimation.smoothToHide();
            notify_User_Via_Snackbar("Players not reset!", Snackbar.LENGTH_SHORT, 1);
        }
    }
}



