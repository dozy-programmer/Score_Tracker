package com.akapps.scoretrackerv47.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Activities.HomePage;
import com.akapps.scoretrackerv47.ListView_Adapters.ListViewAdapter_HomePage;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class dialog_add_player extends Dialog {

    private Activity activity;
    private TextView enter, cancel;
    private EditText player_Name;
    private ImageView add_Multiple_Players, edit_Profile_Pic, player_Icon;
    private TextView letters_Entered, generic_Name;
    private ArrayList<Player_Info> players;
    private Context context;
    private Player_Info person;
    private View view;
    private ListView player_ListView;
    private ListViewAdapter_HomePage adapter;
    private int selection;
    private Bitmap mResultsBitmap;
    private boolean picture_Taken = false;
    private String retrievePlayerName, retrievePhotoPath;
    private int number_of_Players;
    private int selected_Icon;

    public dialog_add_player(Activity a, View v, ListViewAdapter_HomePage adapter, ListView listView, Context context) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
        this.adapter = adapter;
        this.player_ListView = listView;
        this.view= v;
        this.context= context;
    }

    public dialog_add_player(Activity a, View v, ListViewAdapter_HomePage adapter, ListView listView, Context context, String playerName, String photo_Path) {
        super(a);
        // TODO Auto-generated constructor stub
        this.activity = a;
        this.view= v;
        this.adapter = adapter;
        this.player_ListView = listView;
        this.context= context;
        picture_Taken = true;
        retrievePlayerName = playerName;
        retrievePhotoPath = photo_Path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player_dialog);
        context= activity;

        // uses method to get current user data
        players = getUserInfo();

        // initializes TextViews and EditTexts
        letters_Entered= findViewById(R.id.letters_Entered_Length);
        enter = findViewById(R.id.enter);
        cancel = findViewById(R.id.close);
        player_Name= findViewById(R.id.name_editText);
        generic_Name = findViewById(R.id.generic_Name);
        add_Multiple_Players = findViewById(R.id.add_mutiple_players);
        edit_Profile_Pic = findViewById(R.id.edit);
        player_Icon = findViewById(R.id.player_Icon);


        player_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title("Choosing Image Type:")
                        .titleColor(context.getResources().getColor(R.color.orange_red))
                        .canceledOnTouchOutside(false)
                        .backgroundColor(context.getResources().getColor(R.color.black))
                        .widgetColorRes(R.color.colorPrimary)
                        .positiveText("Photo")
                        .positiveColor(context.getResources().getColor(R.color.colorPrimary))
                        .negativeText("From Gallery")
                        .negativeColor(context.getResources().getColor(R.color.green))
                        .neutralText("Avatar")
                        .neutralColor(context.getResources().getColor(R.color.orange))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((HomePage) context).setOrientation(true);
                                String currentName = player_Name.getText().toString();
                                ((HomePage) context).launchCamera(currentName, "", -1);
                                dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((HomePage) context).setOrientation(true);
                                String currentName = player_Name.getText().toString();
                                ((HomePage) context).getImageFromGallery(currentName);
                                dismiss();
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                final Dialog icon_chooser = new Dialog(activity);
                                icon_chooser.setTitle("Choose Icon:");
                                icon_chooser.setCancelable(false);
                                icon_chooser.setContentView(R.layout.icon_chooser);
                                selected_Icon = 0;

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
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==2) {
                                            Glide.with(context).load(R.drawable.girl).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==3) {
                                            Glide.with(context).load(R.drawable.boy_1).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==4) {
                                            Glide.with(context).load(R.drawable.girl_1).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==5) {
                                            Glide.with(context).load(R.drawable.man).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==6) {
                                            Glide.with(context).load(R.drawable.man_4).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==7) {
                                            Glide.with(context).load(R.drawable.man_1).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==8) {
                                            Glide.with(context).load(R.drawable.man_2).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
                                            icon_chooser.dismiss();
                                        }
                                        else if(selected_Icon==9) {
                                            Glide.with(context).load(R.drawable.man_3).dontTransform().into(player_Icon);
                                            retrievePhotoPath = Integer.toString(selected_Icon);
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


        // focuses user attention to EditText box and opens keyboard for them
        player_Name.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // when EditText is changed, this method is called
        player_Name.addTextChangedListener(mTextEditorWatcher);

        if(picture_Taken == true){
            if(retrievePhotoPath!=null){
                player_Name.setText(retrievePlayerName);

                if(retrievePhotoPath.contains("com.android"))
                    Glide.with(context).load(Uri.parse(retrievePhotoPath)).fallback(R.drawable.icon_player_blue).dontTransform().into(player_Icon);
                else
                    Glide.with(context).load(new File(retrievePhotoPath)).fallback(R.drawable.icon_player_blue).dontTransform().into(player_Icon);
            }
        }

        generic_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String generic_Name = "Player " + (players.size() + 1);
                if(!player_Name.equals(generic_Name)) {
                    player_Name.setText(generic_Name);
                }
            }
        });

        add_Multiple_Players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> number_of_Players_Selection = new ArrayList();

                number_of_Players_Selection.add("3");
                number_of_Players_Selection.add("4");
                number_of_Players_Selection.add("5");
                number_of_Players_Selection.add("6");
                number_of_Players_Selection.add("7");
                number_of_Players_Selection.add("8");
                number_of_Players_Selection.add("9");
                number_of_Players_Selection.add("10");
                number_of_Players_Selection.add("15");
                number_of_Players_Selection.add("20");

                // dialog allows user to chooses to sort players by wins, losses, or draws
                new MaterialDialog.Builder(context)
                        .title("Number of players desired:")
                        .titleColor(context.getResources().getColor(R.color.orange_red))
                        .items(number_of_Players_Selection)
                        .itemsColor(context.getResources().getColor(R.color.orange))
                        .canceledOnTouchOutside(false)
                        .backgroundColor(context.getResources().getColor(R.color.black))
                        .widgetColorRes(R.color.colorPrimary)
                        .positiveText("CONFIRM")
                        .positiveColor(context.getResources().getColor(R.color.colorPrimary))
                        .negativeText("CLOSE")
                        .negativeColor(context.getResources().getColor(R.color.gray))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                selection = dialog.getSelectedIndex();
                                if (selection == 0)
                                    addMultiplePlayers(3);
                                else if (selection == 1)
                                    addMultiplePlayers(4);
                                else if(selection == 2)
                                    addMultiplePlayers(5);
                                else if (selection == 3)
                                    addMultiplePlayers(6);
                                else if(selection == 4)
                                    addMultiplePlayers(7);
                                else if (selection == 5)
                                    addMultiplePlayers(8);
                                else if(selection == 6)
                                    addMultiplePlayers(9);
                                else if (selection == 7)
                                    addMultiplePlayers(10);
                                else if(selection == 8)
                                    addMultiplePlayers(15);
                                else if(selection==9)
                                    addMultiplePlayers(20);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog.setSelectedIndex(which);
                                if (text.equals("3"))
                                    selection = 0;
                                else if (text.equals("4"))
                                    selection = 1;
                                else if (text.equals("5"))
                                    selection = 2;
                                else if (text.equals("6"))
                                    selection = 3;
                                else if (text.equals("7"))
                                    selection = 4;
                                else if (text.equals("8"))
                                    selection = 5;
                                else if (text.equals("9"))
                                    selection = 6;
                                else if (text.equals("10"))
                                    selection = 7;
                                else if (text.equals("15"))
                                    selection = 8;
                                else if(text.equals("20"))
                                    selection = 9;
                                return true;
                            }
                        })
                        .show();
            }
        });

        // if entered is pressed, then some things need to occur to add player and dismiss dialog
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // takes the name of the player and capitalizes first letter and rest are lower case

                // if length of name is between 1-12
                if(player_Name.getText().equals("") || player_Name.getText().length()==0)
                    player_Name.setError("Name?");
                else if(player_Name.getText().length()>0 && player_Name.getText().length()<13){
                    String name = player_Name.getText().toString().substring(0,1).toUpperCase() +
                            player_Name.getText().toString().substring( 1, player_Name.getText().length()).toLowerCase();
                    // checks to see if inputted name is unique from the other users
                    boolean unique= uniqueName(name.toLowerCase());
                    if(unique==true) {
                        dismiss(); // closes dialog
                        if(selected_Icon>0){
                            // new person object is created, added to ArrayList,
                            // saved, and updated in ListView
                            person = new Player_Info(name, 0, 0, 0,
                                    0,  0, true, new int[6], 0, retrievePhotoPath);
                        }
                        else if(retrievePhotoPath!=null && !retrievePhotoPath.equals("")){
                            // new person object is created, added to ArrayList,
                            // saved, and updated in ListView
                            person = new Player_Info(name, 0, 0, 0,
                                    0,  0, true, new int[6], 0, retrievePhotoPath);
                        }
                        else{
                            // new person object is created, added to ArrayList,
                            // saved, and updated in ListView
                            person = new Player_Info(name, 0, 0, 0,
                                    0,  0, true, new int[6], 0,"");
                        }

                        players.add(person);
                        game_Playable_Status(true, person);
                        // uses methods in Homepage activity to save data and update for user to see
                        ((HomePage)context).saveUserData(players);
                        ((HomePage) context).setOrientation(false);
                        ((HomePage)context).updateListview(players);
                        updateListView(2, true);
                        Snackbar snackbar = Snackbar.make(view, "Player " + name + " has been added!", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // user has option to undo and delete player created
                                        Snackbar.make(v, "Player " + player_Name.getText() + " has not been added!",
                                                Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                        updateListView(1, true);

                                        // data is saved
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                players.remove(person);
                                                adapter.notifyDataSetChanged();
                                                ((HomePage)context).saveUserData(players);
                                                game_Playable_Status(false, person);
                                            }
                                        }, 750);
                                    }
                                });
                        snackbar.show();
                    }
                    else {
                        String name_Buffer= player_Name.getText().toString();
                        if (name_Buffer.toLowerCase().contains("player")){
                            player_Name.setError("Name taken, try this instead!");
                            player_Name.setText("Player " + new Random().nextInt(players.size()*3));
                        }
                        else
                            player_Name.setError("Name taken, try another!");
                    }
                }
            }
        });

        // dismisses dialog
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void addMultiplePlayers(final int number_of_Players){
        this.number_of_Players = number_of_Players;
        new addMultiplePlayers().execute();
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

    private void game_Playable_Status(boolean status, Player_Info player){
        ArrayList<Game_Info> all_Games = getGameInfo();

        for(int i=0; i<all_Games.size(); i++){
            for(int j=0; j<all_Games.get(i).getPlayers().size(); j++){
                if(all_Games.get(i).getPlayers().get(j).getName().equals(player.getName())) {
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

    // updates ListView for user to see changes
    private void updateListView(int action, boolean undo){
        if(action==2) {
            ((HomePage)context).players = players;
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players, 2, players.size()-1);
            player_ListView.setAdapter(adapter);

        }
        else if(action==1){
            ((HomePage)context).players = players;
            adapter = new ListViewAdapter_HomePage(context, R.layout.homepage_player_format, players, 1, players.size()-1);
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
                    player_ListView.setSelection(players.size()-1);
                }
            });
        }
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

    private ArrayList<Player_Info> getUserInfo() {
        ArrayList<Player_Info> savedArrayList = new ArrayList<>();

        try {
            FileInputStream inputStream = getContext().openFileInput(context.getResources().getString(R.string.user_Data));
            ObjectInputStream in = new ObjectInputStream(inputStream);
            savedArrayList = (ArrayList<Player_Info>) in.readObject();
            in.close();
            inputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
    private class addMultiplePlayers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismiss();
            ((HomePage)context).loadingAnimation.smoothToShow();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String default_Name= "";
            int players_Added = 0;
            for(int i=0; i<(players.size()*2) + number_of_Players; i++) {
                default_Name = "Player " + i;
                if(uniqueName(default_Name.toLowerCase())) {
                    // new person object is created, added to ArrayList,
                    // saved, and updated in ListView
                    person = new Player_Info(default_Name, 0, 0, 0,
                            0, 0, true, new int[6], 0, "");
                    players.add(person);
                    game_Playable_Status(true, person);
                    players_Added++;
                }
                if(players_Added == number_of_Players)
                    break;
            }
            // uses methods in Homepage activity to save data and update for user to see
            ((HomePage)context).saveUserData(players);
            dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ((HomePage)context).loadingAnimation.smoothToHide();
            updateListView(3, false);
            ((HomePage)context).notify_User_Via_Snackbar("Players added", Snackbar.LENGTH_SHORT,
                    1);
        }
    }
}
