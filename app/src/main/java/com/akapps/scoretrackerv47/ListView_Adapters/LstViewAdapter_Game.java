package com.akapps.scoretrackerv47.ListView_Adapters;

// populates ListView Game activity in Activities folder

import android.content.Context;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Vibrator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import static android.content.Context.VIBRATOR_SERVICE;


public class LstViewAdapter_Game extends ArrayAdapter<Player_Info> {

    private ArrayList<Player_Info> player_List;
    private ArrayList<Game_Info> games;
    private Context context;
    private int groupid, interval, current_Player_Score, current_Game_Position;
    private Vibrator vibrate;
    private Glide glide;

    public LstViewAdapter_Game(Context context, int vg, ArrayList<Player_Info> player_List, int interval){
        super(context,vg, player_List);
        this.context   = context;
        this.groupid   = vg;
        this.player_List = player_List;
        this.interval  = interval;
        this.current_Game_Position=-1;
     }

    public LstViewAdapter_Game(Context context, int vg, ArrayList<Player_Info> player_List, int interval, int current_Game_Position){
        super(context,vg, player_List);
        this.context   = context;
        this.groupid   = vg;
        this.player_List = player_List;
        this.interval  = interval;
        this.current_Game_Position = current_Game_Position;
    }


    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        private TextView  player_Name;
        private TextView  current_Score_Text;
        private CircularImageView player_Image;
        private Button    minus, plus;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View rowView = convertView;
        // Inflate the game_player_format.xml if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.player_Name= rowView.findViewById(R.id.player_Name_Game);
            viewHolder.current_Score_Text= rowView.findViewById(R.id.current_Score);
            viewHolder.minus= rowView.findViewById(R.id.minus);
            viewHolder.plus= rowView.findViewById(R.id.plus);
            viewHolder.player_Image= rowView.findViewById(R.id.player_Image_Game);
            rowView.setTag(viewHolder);
        }

        // Set text to each TextView of ListView item
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        if (context.getSystemService(VIBRATOR_SERVICE) != null ) {
            vibrate = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        }
        SharedPreferences sharedp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean minusButtonInvisible = sharedp.getBoolean("minus button invisible",false);

        // initializes TextViews to each player's data
        final Player_Info player= player_List.get(position);

        int orientation = context.getResources().getConfiguration().orientation;
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTab);

        // determines if a player has an image and sets it
        // if path contains words, then it is an image
        // if path is 1, then they have an avatar as profile picture
        // anything else, sets it to default icon
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            holder.player_Image.invalidate();
            if(!(player.getPlayer_Image_Path().equals(""))) {
                if(player.getPlayer_Image_Path().contains("com.android"))
                    Glide.with(context).load(Uri.parse(player.getPlayer_Image_Path())).dontTransform().into(holder.player_Image);
                else if (player.getPlayer_Image_Path().length()>1)
                    Glide.with(context).load(new File(player.getPlayer_Image_Path())).dontTransform().into(holder.player_Image);
                else if(player.getPlayer_Image_Path().length()==1){
                    int current_Icon =  Integer.valueOf(player.getPlayer_Image_Path());
                    if(current_Icon==1) {
                        Glide.with(context).load(R.drawable.boy).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==2) {
                        Glide.with(context).load(R.drawable.girl).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==3) {
                        Glide.with(context).load(R.drawable.boy_1).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==4) {
                        Glide.with(context).load(R.drawable.girl_1).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==5) {
                        Glide.with(context).load(R.drawable.man).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==6) {
                        Glide.with(context).load(R.drawable.man_4).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==7) {
                        Glide.with(context).load(R.drawable.man_1).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==8) {
                        Glide.with(context).load(R.drawable.man_2).dontTransform().into(holder.player_Image);
                    }
                    else if(current_Icon==9) {
                        Glide.with(context).load(R.drawable.man_3).dontTransform().into(holder.player_Image);
                    }
                }
            }
        }

        holder.player_Name.setText(player.getName());
        holder.current_Score_Text.setText(Integer.toString(player.getCurrentScore()));

        // retrieves game data and determines the current game position
        games = get_All_Games();

        if(current_Game_Position==-1) {
            if(games.isEmpty())
                current_Game_Position = 0;
            else
                current_Game_Position = games.size();
        }

        // if user does not have a color chosen by him/her, then default color
        // for player's text is orange and their score color is set to a gold-ish color
        if(player.getPlayer_Text_Color()==0){
            holder.player_Name.setTextColor(getContext().getResources().getColor(R.color.orange));
            holder.current_Score_Text.setTextColor(getContext().getResources().getColor(R.color.yellow_darker));
        }
        else{
            holder.player_Name.setTextColor(player.getPlayer_Text_Color());
            holder.current_Score_Text.setTextColor(player.getPlayer_Text_Color());
        }
        // if there currently is no interval since by default it is 0,then it is set to 1
        if(interval==0)
            interval=1;
        // sets the minus button to be invisible during a game
        else if(interval==-1){
            holder.minus.setVisibility(View.INVISIBLE);
            holder.plus.setVisibility(View.INVISIBLE);
        }
        // sets the minus button to be visible during a game
        else if(interval<=-2){
            holder.minus.setVisibility(View.VISIBLE);
            holder.plus.setVisibility(View.VISIBLE);
            interval=1;
        }

        // sets minus button to be invisible and changes size of plus button depending
        // on orientation and size of device
        if(minusButtonInvisible){
            if(orientation == Configuration.ORIENTATION_PORTRAIT && !tabletSize) {
                holder.minus.setVisibility(View.GONE);
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (100 * Resources.getSystem().getDisplayMetrics().density), (int) (65 * Resources.getSystem().getDisplayMetrics().density));
                holder.plus.setLayoutParams(lp);
            }
            else if(orientation == Configuration.ORIENTATION_LANDSCAPE && !tabletSize) {
                holder.minus.setVisibility(View.GONE);
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (240 * Resources.getSystem().getDisplayMetrics().density), (int) (65 * Resources.getSystem().getDisplayMetrics().density));
                holder.plus.setLayoutParams(lp);
            }
            else if(orientation == Configuration.ORIENTATION_PORTRAIT && tabletSize) {
                holder.minus.setVisibility(View.GONE);
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (200 * Resources.getSystem().getDisplayMetrics().density), (int) (100 * Resources.getSystem().getDisplayMetrics().density));
                holder.plus.setLayoutParams(lp);
            }
            else if(orientation == Configuration.ORIENTATION_LANDSCAPE && tabletSize) {
                holder.minus.setVisibility(View.GONE);
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (300 * Resources.getSystem().getDisplayMetrics().density), (int) (150 * Resources.getSystem().getDisplayMetrics().density));
                holder.plus.setLayoutParams(lp);
            }
        }

        // if user pressed minus button for a player, then some stuff happens
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retrieves a player's current score
                current_Player_Score = player_List.get(position).getCurrentScore();
                // if interval menu is open, then it is closed

                // if subtracting an interval goes under 0, then error message
                if(current_Player_Score-interval <0)
                    Snackbar.make(v, "Can't go under 0!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                // current score minus interval is saved as new score and updated for user to see
                else{
                    if(context.getSystemService(VIBRATOR_SERVICE) != null && vibration_Mode()) {
                        vibrate.vibrate(50);
                    }
                    player.setLastScore(current_Player_Score);
                    current_Player_Score-= interval;
                    player.setCurrentScore(current_Player_Score);
                    holder.current_Score_Text.setText(Integer.toString(current_Player_Score));

                    saveUserData(player_List);

                    if(current_Game_Position == games.size())
                        games.get(current_Game_Position-1).setPlayers(player_List);
                    else
                        games.get(current_Game_Position).setPlayers(player_List);
                    saveGames();
                }
            }
        });

        // if user pressed add button for a player, then some stuff happens
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retrieves a player's current score
                current_Player_Score = player_List.get(position).getCurrentScore();
                // if interval menu is open, then it is closed

                // if adding an interval goes over 1 mil, then error message
                if(current_Player_Score+interval > 999999)
                    Snackbar.make(v, "Can't go over 1 million!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                // current score plus interval is saved as new score and updated for user to see
                else {
                    if(context.getSystemService(VIBRATOR_SERVICE) != null && vibration_Mode()) {
                        vibrate.vibrate(50);
                    }
                    player.setLastScore(current_Player_Score);
                    current_Player_Score+= interval;
                    player.setCurrentScore(current_Player_Score);
                    holder.current_Score_Text.setText(Integer.toString(current_Player_Score));

                    saveUserData(player_List);
                    if(current_Game_Position == games.size())
                        games.get(current_Game_Position-1).setPlayers(player_List);
                    else
                        games.get(current_Game_Position).setPlayers(player_List);
                    saveGames();
                }
            }
        });
        return rowView;
    }

    // method to save player objects in txt file
    private void saveUserData(ArrayList<Player_Info> arrayList) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(context.getResources().getString(R.string.user_Data_in_Game), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(arrayList);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // retrieves game data
    private ArrayList<Game_Info> get_All_Games() {
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

    // saves game data to a txt file
    public void saveGames(){

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

    // returns state of vibration setting
    private boolean vibration_Mode(){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getBoolean("vibration",false);
    }

    public int getViewTypeCount() {
        int count;
        if (player_List.size() > 0) {
            count = getCount();
        } else {
            count = 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
}