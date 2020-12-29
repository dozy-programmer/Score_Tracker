package com.akapps.scoretrackerv47.ListView_Adapters;

// populates ListView HomePage activity in Activities folder

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.akapps.scoretrackerv47.Activities.HomePage;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ListViewAdapter_HomePage extends ArrayAdapter<Player_Info> {

    private ArrayList<Player_Info> players;
    private Context context;
    private int groupid;
    private Glide glide;
    int playerAction, atPosition = 0;
    private boolean refresh =  true;

    public ListViewAdapter_HomePage(Context context, int vg, ArrayList<Player_Info> players) {
        super(context, vg, players);
        this.context = context;
        this.groupid = vg;
        this.players = players;
    }

    public ListViewAdapter_HomePage(Context context, int vg, ArrayList<Player_Info> players, int playerAction, int atPosition) {
        super(context, vg, players);
        this.context = context;
        this.groupid = vg;
        this.players = players;
        this.playerAction = playerAction;
        this.atPosition = atPosition;
    }

    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        private TextView player_Name, game_Status, wins, losses, draws, totalGames;
        private Switch player_Status_Switch;
        private CircularImageView circularImageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Inflate layout if convertView is null
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder_Empty = new ViewHolder();
            viewHolder_Empty.player_Name = convertView.findViewById(R.id.player_Name);
            viewHolder_Empty.wins = convertView.findViewById(R.id.wins_Text);
            viewHolder_Empty.losses = convertView.findViewById(R.id.loss_Text);
            viewHolder_Empty.draws = convertView.findViewById(R.id.draw_Text);
            viewHolder_Empty.totalGames = convertView.findViewById(R.id.total_Text);
            viewHolder_Empty.circularImageView = convertView.findViewById(R.id.player_Image_Game);
            viewHolder_Empty.player_Status_Switch = convertView.findViewById(R.id.player_Status_Switch);
            viewHolder_Empty.game_Status = convertView.findViewById(R.id.game_Status);
            convertView.setTag(viewHolder_Empty);
        }

        // playerAction determines if there needs to be an animation (for deleting and adding player)
        if (playerAction == 2 && refresh) {
            // player is added animation
            if (position == atPosition) {
                Animation animation = AnimationUtils
                        .loadAnimation(context, R.anim.left_to_right);
                convertView.startAnimation(animation);
                animation.setDuration(750);
                refresh = false;
            }
        }
        else if (playerAction == 1 && refresh) {
            // player is deleted animation
            if (position == atPosition) {
                final Animation animation = AnimationUtils
                        .loadAnimation(context, R.anim.right_to_left);
                animation.setDuration(750);
                convertView.startAnimation(animation);
            }
        }

        // Gets player data for this position
        // Set text to each TextView of ListView item
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.player_Name = convertView.findViewById(R.id.player_Name);
        holder.wins = convertView.findViewById(R.id.wins_Text);
        holder.losses = convertView.findViewById(R.id.loss_Text);
        holder.draws = convertView.findViewById(R.id.draw_Text);
        holder.totalGames = convertView.findViewById(R.id.total_Text);
        holder.player_Status_Switch = convertView.findViewById(R.id.player_Status_Switch);
        holder.circularImageView = convertView.findViewById(R.id.player_Image);
        holder.circularImageView.invalidate();

        // gets player data and initializes it in TextViews
        final Player_Info player = players.get(position);
        holder.player_Name.setText(player.getName());
        int total_Games = player.getTotalGames();

        // determines if a player has an image and sets it
        // if path contains words, then it is an image
        // if path is 1, then they have an avatar as profile picture
        // anything else, sets it to default icon
        if(player.getPlayer_Image_Path()!=null){
            if(!(player.getPlayer_Image_Path().equals("")) ) {
                if(player.getPlayer_Image_Path().contains("com.android"))
                    Glide.with(context)
                            .load(Uri.parse(player.getPlayer_Image_Path()))
                            .fallback(R.drawable.icon_player_blue)
                            .dontTransform()
                            .into(holder.circularImageView);
                else if (player.getPlayer_Image_Path().length()>1)
                    Glide.with(context)
                            .load(new File(player.getPlayer_Image_Path())).
                            fallback(R.drawable.icon_player_blue).
                            dontTransform().
                            into(holder.circularImageView);
                else if(player.getPlayer_Image_Path().length()==1){
                    int current_Icon =  Integer.valueOf(player.getPlayer_Image_Path());
                    if(current_Icon==1) {
                        Glide.with(context).load(R.drawable.boy).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==2) {
                        Glide.with(context).load(R.drawable.girl).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==3) {
                        Glide.with(context).load(R.drawable.boy_1).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==4) {
                        Glide.with(context).load(R.drawable.girl_1).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==5) {
                        Glide.with(context).load(R.drawable.man).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==6) {
                        Glide.with(context).load(R.drawable.man_4).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==7) {
                        Glide.with(context).load(R.drawable.man_1).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==8) {
                        Glide.with(context).load(R.drawable.man_2).dontTransform().into(holder.circularImageView);
                    }
                    else if(current_Icon==9) {
                        Glide.with(context).load(R.drawable.man_3).dontTransform().into(holder.circularImageView);
                    }
                }
            }
        }

        // depending on size of screen and orientation, the text words are changed
        // example, instead of just "Games", it is set to "Total Games"
        // this is done to make the text fit the screen
        int orientation = getContext().getResources().getConfiguration().orientation;
        boolean tabletSize = getContext().getResources().getBoolean(R.bool.isTab);
        if (!tabletSize && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            holder.wins.setText("Wins: " + player.getGamesWon());
            holder.losses.setText("Losses: " + player.getGamesLost());
            holder.draws.setText("Draws: " + player.getTies());
            holder.totalGames.setText("Games: " + total_Games);
        } else {
            if(tabletSize){
                if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    holder.wins.setText("Wins: " + player.getGamesWon());
                    holder.losses.setText("Losses: " + player.getGamesLost());
                    holder.draws.setText("Draws: " + player.getTies());
                    holder.totalGames.setText("Total Games: " + total_Games);
                }
                else{
                    holder.wins.setText("Wins: \n" + player.getGamesWon());
                    holder.losses.setText("Losses: \n" + player.getGamesLost());
                    holder.draws.setText("Draws: \n" + player.getTies());
                    holder.totalGames.setText("Games: \n" + total_Games);
                }
            }
            else {
                holder.wins.setText("Wins:\n " + player.getGamesWon());
                holder.losses.setText("Losses: \n" + player.getGamesLost());
                holder.draws.setText("Draws: \n" + player.getTies());
                holder.totalGames.setText("Total: \n" + total_Games);
            }
        }

        // if switch is pressed, then switch state changes and it is saved for each player
        holder.player_Status_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()) {
                    if (isChecked) {
                        player.setSelectedState(isChecked);
                        holder.player_Status_Switch.setChecked(isChecked);
                        holder.game_Status.setText("Playing...");
                        holder.game_Status.setTextColor(getContext().getResources().getColor(R.color.light_green)); // text color set to green
                    } else {
                        player.setSelectedState(false);
                        holder.player_Status_Switch.setChecked(false);
                        holder.game_Status.setText("Playing...");
                        holder.game_Status.setTextColor(Color.RED);   // text color set to red
                    }

                    // saves player data using method in HomePage activity
                    save_User_Switch_State();
                }
            }
        });

        // sets the correct toggle state of each player
        holder.player_Status_Switch.setChecked(players.get(position).getSelectedState());

        if (holder.player_Status_Switch.isChecked()) {
            holder.game_Status.setText("Playing...");
            holder.game_Status.setTextColor(getContext().getResources().getColor(R.color.light_green)); // text color set to green
        }
        else{
            holder.game_Status.setText("Playing...");
            holder.game_Status.setTextColor(Color.RED);   // text color set to red
        }

        // if user does not have a color chosen by him/her, then default color
        // for player's text is orange
        if (player.getPlayer_Text_Color() == 0)
            holder.player_Name.setTextColor(getContext().getResources().getColor(R.color.orange));
        else
            holder.player_Name.setTextColor(player.getPlayer_Text_Color());

        return convertView;
    }
    // if user changes state of a player,then the players states from txt file is updated.
    // this is done because if user sorts players by wins, losses, draws, or game played...
    // it does not save those new list positions and keeps only original list positions
    // in addition, if player list is sorted and player state is changed, this method changes
    // based on player names and not their positions
    private void save_User_Switch_State(){
        ArrayList<Player_Info> updatedPlayers = ((HomePage) context).getUserData();

        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.size(); j++) {
                if (updatedPlayers.get(i).getName().equals(players.get(j).getName())) {
                    updatedPlayers.get(i).setSelectedState(players.get(j).getSelectedState());
                    continue;
                }
            }
        }

        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput(getContext().getString(R.string.user_Data), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(updatedPlayers);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override

    public int getViewTypeCount() {
        int count;
        if (players.size() > 0) {
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