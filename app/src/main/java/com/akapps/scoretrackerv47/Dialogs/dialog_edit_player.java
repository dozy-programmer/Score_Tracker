package com.akapps.scoretrackerv47.Dialogs;

// this dialog is used in HomePage activity in Activities folder

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akapps.scoretrackerv47.Activities.HomePage;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.ListView_Adapters.ListViewAdapter_HomePage;
import com.akapps.scoretrackerv47.R;
import com.bumptech.glide.Glide;
import com.github.naz013.colorslider.ColorSlider;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class dialog_edit_player extends Dialog {

    private Activity activity;
    private TextView close, enter, delete_Player, reset_Player, number_Of_Letters;
    private String currentPlayer_Name, newPlayer_Name;
    private EditText player_Name_Edit;
    private ImageView player_Icon;
    private ArrayList<Player_Info> players;
    private Player_Info player_Object;
    private View currentView;
    private int position;
    private Context context;
    private ListViewAdapter_HomePage adapter;
    private ListView player_ListView;
    private ColorSlider colorSlider;
    private Glide glide;
    private boolean player_Text_Color_Changed, player_Name_Changed;
    private String photo_Path= "";
    private boolean photo_Taken;
    private View view;
    private int selected_Icon, current_Color;

    public dialog_edit_player(Activity a, View v, int position, ListViewAdapter_HomePage adapter, Context context, ListView listView, View view) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
        this.currentView= v;
        this.view = view;
        this.position= position;
        this.adapter= adapter;
        this.context= context;
        this.player_ListView= listView;
        photo_Taken = false;
    }

    public dialog_edit_player(Activity a, View v, int position, ListViewAdapter_HomePage adapter, Context context, ListView listView, String photo_Path, View view, String player_Name, int player_Text_Color) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
        this.currentView= v;
        this.position= position;
        this.adapter= adapter;
        this.view = view;
        this.context= context;
        this.player_ListView= listView;
        this.photo_Path = photo_Path;
        this.currentPlayer_Name = player_Name;
        this.current_Color = player_Text_Color;
        photo_Taken = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_dialog);

        // uses method to get current user data
        players= getUserInfo();

        // initializes TextViews and EditTexts and color slider
        delete_Player = findViewById(R.id.delete_Player);
        reset_Player= findViewById(R.id.reset_Player);
        enter = findViewById(R.id.enter_dialog);
        close = findViewById(R.id.close_dialog);
        player_Name_Edit= findViewById(R.id.name_edit);
        number_Of_Letters = findViewById(R.id.letters_Entered_Edit_Player);
        colorSlider = findViewById(R.id.color_slider);
        player_Icon = findViewById(R.id.player_Icon_Edit);

        // focuses user on EditText
        player_Name_Edit.requestFocus();
        player_Text_Color_Changed = player_Name_Changed = false;

        // gets player and respective information for user to see
        player_Object = players.get(position);

        if(photo_Taken == true){
            if(photo_Path!=null){
                player_Name_Edit.setText(currentPlayer_Name);
                player_Name_Edit.setTextColor(current_Color);
                player_Text_Color_Changed= true;

                if(photo_Path.contains("com.android"))
                    Glide.with(context).load(Uri.parse(photo_Path)).dontTransform().into(player_Icon);
                else
                    Glide.with(context).load(new File(photo_Path)).dontTransform().into(player_Icon);
            }
        }
        else{
            currentPlayer_Name= players.get(position).getName();
            player_Name_Edit.setText(currentPlayer_Name);
            // if user does not have a color chosen by him/her, then default color
            // for player's text is orange
            if(player_Object.getPlayer_Text_Color()==0)
                player_Name_Edit.setTextColor(getContext().getResources().getColor(R.color.orange));
            else
                player_Name_Edit.setTextColor(player_Object.getPlayer_Text_Color());

            if(player_Object.getPlayer_Image_Path()!=null){
                if(player_Object.getPlayer_Image_Path().contains("com.android"))
                    Glide.with(context).load(Uri.parse(player_Object.getPlayer_Image_Path())).dontTransform().fallback(R.drawable.icon_player_blue).into(player_Icon);
                else if(player_Object.getPlayer_Image_Path().length()==1){
                    int current_Icon =  Integer.valueOf(player_Object.getPlayer_Image_Path());
                    if(current_Icon==1) {
                        Glide.with(context).load(R.drawable.boy).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==2) {
                        Glide.with(context).load(R.drawable.girl).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==3) {
                        Glide.with(context).load(R.drawable.boy_1).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==4) {
                        Glide.with(context).load(R.drawable.girl_1).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==5) {
                        Glide.with(context).load(R.drawable.man).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==6) {
                        Glide.with(context).load(R.drawable.man_4).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==7) {
                        Glide.with(context).load(R.drawable.man_1).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==8) {
                        Glide.with(context).load(R.drawable.man_2).dontTransform().into(player_Icon);
                    }
                    else if(current_Icon==9) {
                        Glide.with(context).load(R.drawable.man_3).dontTransform().into(player_Icon);
                    }
                }
                else
                    Glide.with(context).load(new File(player_Object.getPlayer_Image_Path())).dontTransform().fallback(R.drawable.icon_player_blue).into(player_Icon);
            }
        }

        player_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Choosing Image Type:")
                        .titleColor(context.getResources().getColor(R.color.orange_red))
                        .canceledOnTouchOutside(true)
                        .backgroundColor(context.getResources().getColor(R.color.black))
                        .widgetColorRes(R.color.colorPrimary)
                        .positiveText("Photo")
                        .positiveColor(context.getResources().getColor(R.color.colorPrimary))
                        .negativeText("From Gallery")
                        .negativeColor(context.getResources().getColor(R.color.green))
                        .neutralText("Avatars")
                        .neutralColor(context.getResources().getColor(R.color.orange))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((HomePage) context).setOrientation(true);
                                String current_Name= player_Name_Edit.getText().toString();
                                int text_Color = player_Name_Edit.getCurrentTextColor();
                                ((HomePage) context).launchCamera(String.valueOf(position), current_Name, text_Color);
                                dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((HomePage) context).setOrientation(true);
                                String name = player_Name_Edit.getText().toString();
                                int text_Color = player_Name_Edit.getCurrentTextColor();
                                ((HomePage) context).getImageFromGalleryEdit(name ,position, true, text_Color);
                                dismiss();
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                final Dialog icon_chooser = new Dialog(activity);
                                icon_chooser.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                icon_chooser.setCancelable(false);
                                icon_chooser.setContentView(R.layout.icon_chooser);
                                selected_Icon = 0;

                                // selects the avatar
                                final TextView select_icon = icon_chooser.findViewById(R.id.select_icon);
                                TextView close_icon_chooser = icon_chooser.findViewById(R.id.close_icon);
                                final ImageView icon_1 = icon_chooser.findViewById(R.id.icon_1);
                                final ImageView icon_2 = icon_chooser.findViewById(R.id.icon_2);
                                final ImageView icon_3 = icon_chooser.findViewById(R.id.icon_3);
                                final ImageView icon_4 = icon_chooser.findViewById(R.id.icon_4);
                                final ImageView icon_5 = icon_chooser.findViewById(R.id.icon_5);
                                final ImageView icon_6 = icon_chooser.findViewById(R.id.icon_6);
                                final ImageView icon_7 = icon_chooser.findViewById(R.id.icon_7);
                                final ImageView icon_8 = icon_chooser.findViewById(R.id.icon_8);
                                final ImageView icon_9 = icon_chooser.findViewById(R.id.icon_9);

                                icon_1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 1;
                                        icon_1.setImageAlpha(50);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 2;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(50);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 3;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(50);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 4;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(50);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 5;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(50);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_6.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 6;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(50);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_7.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 7;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(50);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_8.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 8;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(50);
                                        icon_9.setImageAlpha(250);
                                    }
                                });

                                icon_9.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        selected_Icon = 9;
                                        icon_1.setImageAlpha(250);
                                        icon_2.setImageAlpha(250);
                                        icon_3.setImageAlpha(250);
                                        icon_4.setImageAlpha(250);
                                        icon_5.setImageAlpha(250);
                                        icon_6.setImageAlpha(250);
                                        icon_7.setImageAlpha(250);
                                        icon_8.setImageAlpha(250);
                                        icon_9.setImageAlpha(50);
                                    }
                                });

                                close_icon_chooser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        icon_chooser.dismiss();
                                    }
                                });

                                select_icon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(selected_Icon==1) {
                                            Glide.with(context).load(R.drawable.boy).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==2) {
                                            Glide.with(context).load(R.drawable.girl).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==3) {
                                            Glide.with(context).load(R.drawable.boy_1).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==4) {
                                            Glide.with(context).load(R.drawable.girl_1).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==5) {
                                            Glide.with(context).load(R.drawable.man).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==6) {
                                            Glide.with(context).load(R.drawable.man_4).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==7) {
                                            Glide.with(context).load(R.drawable.man_1).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==8) {
                                            Glide.with(context).load(R.drawable.man_2).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==9) {
                                            Glide.with(context).load(R.drawable.man_3).dontTransform().into(player_Icon);
                                            photo_Path = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else{
                                            Snackbar.make(view, "Select Avatar", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                        }
                                    }
                                });

                                icon_chooser.show();
                            }
                        })
                        .show();
            }
        });

        player_Name_Edit.addTextChangedListener(mTextEditorWatcher);
        number_Of_Letters.setText(player_Name_Edit.length() + "/ 12");

        // changes color of the player name EditTextView for user to see how name looks with new color
        colorSlider.setListener(new ColorSlider.OnColorSelectedListener() {
            @Override
            public void onColorChanged(int position, int color) {
                player_Text_Color_Changed = true;
                player_Name_Edit.setTextColor(color);
            }
        });

        // resets current player's data to 0 and saves it
        reset_Player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player_Object.setTotalGames(0);
                player_Object.setGamesWon(0);
                player_Object.setGamesLost(0);
                player_Object.setTies(0);
                player_Object.setRecentScores(new int[6]);
                ((HomePage) context).saveUserData(players);
                dismiss();        // dialog is dismissed
                // notifies current player's data has been reset
                updateListView(3, false); // ListView is updated for user to see
                Snackbar.make(v, players.get(position).getName() + "'s scores have been reset!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        // deletes user
        delete_Player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();

                updateListView(1, true);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        game_Playable_Status(false);
                        players.remove(position); // user at current position is deleted
                        ((HomePage)context).saveUserData(players); // information is saved
                        ((HomePage)context).updateListview(players);
                        updateListView(3, false);
                        Snackbar.make(currentView, currentPlayer_Name + " has been deleted!", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // if user pressed undo, then player is re-added and saved
                                        players.add(position, player_Object);
                                        game_Playable_Status(true);
                                        ((HomePage)context).saveUserData(players);
                                        updateListView(2, true);
                                        // notifies user that player has not been deleted
                                        Snackbar.make(v, "Player " + currentPlayer_Name + " not deleted!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    }
                                }).show();
                    }
                }, 750);
            }
        });

        // if entered is pressed, then some things need to occur to save changes and dismiss dialog
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomePage) context).setOrientation(false);
                if (player_Name_Edit.getText().toString().length() == 0)
                    player_Name_Edit.setError("Name?");
                else if (player_Name_Edit.getText().length() > 0 && player_Name_Edit.getText().length() < 13) {
                    newPlayer_Name = player_Name_Edit.getText().toString().substring(0, 1).toUpperCase() +
                            player_Name_Edit.getText().toString().substring(1, player_Name_Edit.getText().length()).toLowerCase();
                    if ((player_Name_Changed && newPlayer_Name!=null) || player_Text_Color_Changed) {
                        boolean unique = uniqueName(newPlayer_Name.toLowerCase());
                        if (player_Text_Color_Changed) {
                            // changes player text color to the color picked by user
                            player_Object.setPlayer_Text_Color(player_Name_Edit.getCurrentTextColor());
                        }
                        if (!unique && player_Name_Changed)
                            player_Name_Edit.setError("Name Taken!");
                        else {
                            dismiss(); // closes dialog
                            // if player name is changed, then it is updated for user to see and saved
                            game_Playable_Status(false);
                            players.get(position).setName(newPlayer_Name);
                            if (photo_Taken || selected_Icon> 0) {
                                if (photo_Path != null) {
                                    players.get(position).setPlayer_Image_Path(photo_Path);
                                }
                            }
                            game_Playable_Status(true);
                            ((HomePage) context).saveUserData(players);
                            ((HomePage) context).updateListview(players);
                            // closes keyboard
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        }
                    }
                    else if (photo_Taken || selected_Icon>0) {
                        if (photo_Path != null) {
                            players.get(position).setPlayer_Image_Path(photo_Path);
                            game_Playable_Status(true);
                            ((HomePage) context).saveUserData(players);
                            ((HomePage) context).updateListview(players);
                            dismiss();
                        }
                    }
                }
                else{
                    player_Name_Edit.setError("Sup?");
                }
            }
        });

        // closes dialog
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void game_Playable_Status(boolean status){
        ArrayList<Game_Info> all_Games = getGameInfo();

        for(int i=0; i<all_Games.size(); i++){
            for(int j=0; j<all_Games.get(i).getPlayers().size(); j++){
                if(all_Games.get(i).getPlayers().get(j).getName().equals(players.get(position).getName())) {
                    all_Games.get(i).getPlayers().get(j).setPlay_Able(status);
                    boolean playable = gamePlayable(all_Games.get(i));
                    all_Games.get(i).set_Play_Able(playable);
                    saveGames(all_Games);
                }
            }
        } // end of for loop
    }

    private boolean gamePlayable(Game_Info game){
        for(int i=0; i<game.getPlayers().size(); i++){
            if (game.getPlayers().get(i).get_Play_Able()==false)
                return false;
        }
        return true;
    }

    // checks to see if name of user is unique from all the players in ArrayList
    public boolean uniqueName(String name) {
        for(int position=0; position < players.size(); position++){
            String currentName= players.get(position).getName().toLowerCase();
            if(currentName.equals(name))
                return false;
        }
        return true;
    }

    // when user is inputting name in EditText, this method keeps track of the string length
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        // as long as EditText length is less than 13, it will keep updating length
        // in addition, if user edits player name, then player_Name_Changed is set to true
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            player_Name_Changed = true;
            if(s.length() < 13)
                number_Of_Letters.setText(s.length() + " / 12");
        }

        public void afterTextChanged(Editable s) { }
    };

    // updates ListView for user to see changes
    private void updateListView(int action, boolean undo){
        if(action==2) {
            ((HomePage)context).players = players;
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players, 2, position);
            player_ListView.setAdapter(adapter);

        }
        else if(action==1){
            ((HomePage)context).players = players;
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players, 1, position);
            player_ListView.setAdapter(adapter);
        }
        else if(action==3){
            ((HomePage)context).players = players;
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players);
            player_ListView.setAdapter(adapter);
        }
        if(undo) {
            player_ListView.post(new Runnable() {
                @Override
                public void run() {
                    player_ListView.setSelection(position);
                }
            });
        }
    }

    private ArrayList<Player_Info> getUserInfo() {
        ArrayList<Player_Info> savedArrayList = null;

        try {
            FileInputStream inputStream = getContext().openFileInput(context.getResources().getString(R.string.user_Data));
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
            FileInputStream inputStream = getContext().openFileInput(context.getResources().getString(R.string.game_Data));
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
            FileOutputStream fileOutputStream = getContext().openFileOutput(context.getResources().getString(R.string.game_Data), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(games);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}