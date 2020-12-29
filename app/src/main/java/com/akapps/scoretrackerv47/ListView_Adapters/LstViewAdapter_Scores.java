package com.akapps.scoretrackerv47.ListView_Adapters;

// populates ListView Scores activity in Activities folder

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import java.util.ArrayList;

public class LstViewAdapter_Scores extends ArrayAdapter<Player_Info> {
    private ArrayList<Player_Info> players;
    private int[] recent_Scores;
    private Context context;
    private int groupid;

    public LstViewAdapter_Scores(Context context, int vg, ArrayList<Player_Info> players){
        super(context,vg, players);
        this.context   = context;
        this.groupid = vg;
        this.players = players;
    }


    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        private TextView player_Name, score_1, score_2, score_3, score_4, score_5;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // Inflates layout if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.player_Name= rowView.findViewById(R.id.score_Name);
            viewHolder.score_1 = rowView.findViewById(R.id.score_1);
            viewHolder.score_2 = rowView.findViewById(R.id.score_2);
            viewHolder.score_3 = rowView.findViewById(R.id.score_3);
            viewHolder.score_4 = rowView.findViewById(R.id.score_4);
            viewHolder.score_5 = rowView.findViewById(R.id.score_5);
            rowView.setTag(viewHolder);
        }
        // Set text to each TextView of ListView item
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        // gets current player and their recent scores
        Player_Info player= players.get(position);
        recent_Scores = player.getRecentScores();

        // formats first column to be the header
        String column;
        if(player.getName().equals("Game"))
            column = "#";
        else
            column = "";

        // sets all the TextViews to be the players 5 last scores and sets the color to be orange
        holder.player_Name.setText(player.getName());
        holder.score_1.setText(column + recent_Scores[1]);
        holder.score_2.setText(column + recent_Scores[2]);
        holder.score_3.setText(column + recent_Scores[3]);
        holder.score_4.setText(column + recent_Scores[4]);
        holder.score_5.setText(column + recent_Scores[5]);

        if(player.getName().equals("Game")){
            //sets column to be green to stand out
            holder.player_Name.setTextColor(getContext().getResources().getColor(R.color.light_green));
            holder.score_1.setTextColor(getContext().getResources().getColor(R.color.light_green));
            holder.score_2.setTextColor(context.getResources().getColor(R.color.light_green));
            holder.score_3.setTextColor(context.getResources().getColor(R.color.light_green));
            holder.score_4.setTextColor(context.getResources().getColor(R.color.light_green));
            holder.score_5.setTextColor(context.getResources().getColor(R.color.light_green));
        }
        else {
            // if user has not chosen a different color for player name, then it
            // will be set to orange be default
            if (player.getPlayer_Text_Color() == 0) {
                holder.player_Name.setTextColor(getContext().getResources().getColor(R.color.orange));
                holder.score_1.setTextColor(getContext().getResources().getColor(R.color.orange));
                holder.score_2.setTextColor(context.getResources().getColor(R.color.orange));
                holder.score_3.setTextColor(context.getResources().getColor(R.color.orange));
                holder.score_4.setTextColor(context.getResources().getColor(R.color.orange));
                holder.score_5.setTextColor(context.getResources().getColor(R.color.orange));
            }
            // player name and scores are set to color chosen by user
            else {
                holder.player_Name.setTextColor(player.getPlayer_Text_Color());
                holder.score_1.setTextColor(player.getPlayer_Text_Color());
                holder.score_2.setTextColor(player.getPlayer_Text_Color());
                holder.score_3.setTextColor(player.getPlayer_Text_Color());
                holder.score_4.setTextColor(player.getPlayer_Text_Color());
                holder.score_5.setTextColor(player.getPlayer_Text_Color());
            }
        }
        return rowView;
    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
