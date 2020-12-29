package com.akapps.scoretrackerv47.ListView_Adapters;

// populates ListView for View_Games activity in Activities folder

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akapps.scoretrackerv47.Classes.Game_Info;
import com.akapps.scoretrackerv47.Classes.Player_Info;
import com.akapps.scoretrackerv47.R;
import java.util.ArrayList;

public class ListViewAdapter_View_All_Games extends ArrayAdapter<Game_Info> {

    private ArrayList<Game_Info> game_List;
    private Context context;
    private int groupid;

    public ListViewAdapter_View_All_Games(Context context, int vg, ArrayList<Game_Info> game_List){
        super(context,vg, game_List);
        this.context   = context;
        this.groupid   = vg;
        this.game_List = game_List;
    }


    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        private TextView game_Name, number_of_Players, date_started, game_Status, quick_View;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View rowView = convertView;
        // Inflate layout if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.game_Name= rowView.findViewById(R.id.game_Name);
            viewHolder.number_of_Players= rowView.findViewById(R.id.number_Of_Players);
            viewHolder.date_started= rowView.findViewById(R.id.date_Of_Game);
            viewHolder.game_Status = rowView.findViewById(R.id.game_Status);
            viewHolder.quick_View = rowView.findViewById(R.id.quick_View);
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();

        // initializes TextViews to each player's data
        final Game_Info game= game_List.get(position);

        holder.game_Name.setText(game.getName_of_Game());
        holder.game_Name.setTextColor(getContext().getResources().getColor(R.color.orange_red));
        holder.date_started.setText("Date Started:  " + game.getDate_of_Game());
        holder.date_started.setTextColor(getContext().getResources().getColor(R.color.light_gold));
        holder.number_of_Players.setText("Players: " + game.getNumber_of_Players());
        holder.number_of_Players.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        holder.game_Status.setText(game.getGame_Satus());
        holder.quick_View.setTextColor(getContext().getResources().getColor(R.color.bluish));

        // if a game is completed, then it is set to green. Otherwise, it is orange
        if(game.getGame_Satus().equals("Completed"))
            holder.game_Status.setTextColor(getContext().getResources().getColor(R.color.dark_green));
        else
            holder.game_Status.setTextColor(getContext().getResources().getColor(R.color.orange));

        // if quick-view text-view is clicked, then dialog opens showing clicked game's players and scores
        holder.quick_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ArrayList<Player_Info> players = game_List.get(position).getPlayers();
                    String all_Players_Info = "Player Name     Score\n\n";
                    for (int i = 0; i < game_List.get(position).getPlayers().size(); i++) {
                        all_Players_Info += players.get(i).getName() + "     " + players.get(i).getCurrentScore() + "\n\n";
                    }

                    new MaterialDialog.Builder(context)
                            .title(game_List.get(position).getName_of_Game())
                            .contentGravity(GravityEnum.CENTER)
                            .content(all_Players_Info)
                            .contentColor(context.getResources().getColor(R.color.colorPrimary))
                            .backgroundColor(context.getResources().getColor(R.color.black))
                            .positiveText("CLOSE")
                            .canceledOnTouchOutside(false)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .titleColor(getContext().getResources().getColor(R.color.orange_red))
                            .positiveColor(getContext().getResources().getColor(R.color.orange_red))
                            .show();
            }
        });

        return rowView;
    }
}
