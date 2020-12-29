package com.akapps.scoretrackerv47.Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Game_Info implements Serializable{

    private String name_of_Game;
    private String date_of_Game;
    private int number_of_Players;
    private String game_Status;
    private ArrayList<Player_Info> players;
    private boolean play_Able;
    private boolean game_Continued;
    private long game_Duration, pauseOffSet;
    private String game_Duration_Formatted;

    public Game_Info(String name_of_Game, String date_of_Game, int number_of_Players, String game_Status, ArrayList<Player_Info> players){
        this.name_of_Game = name_of_Game;
        this.date_of_Game = date_of_Game;
        this.number_of_Players = number_of_Players;
        this.game_Status = game_Status;
        this.players = players;
        this.play_Able = true;
    }

    public long getPauseOffSet() {
        return pauseOffSet;
    }

    public void setPauseOffSet(long pauseOffSet) {
        this.pauseOffSet = pauseOffSet;
    }

    public String getGame_Satus() {
        return game_Status;
    }

    public void setGame_Satus(String game_Satus) {
        this.game_Status = game_Satus;
    }

    public String getName_of_Game() {
        return name_of_Game;
    }

    public void setName_of_Game(String name_of_Game) {
        this.name_of_Game = name_of_Game;
    }

    public String getDate_of_Game() {
        return date_of_Game;
    }

    public int getNumber_of_Players() {
        return number_of_Players;
    }

    public ArrayList<Player_Info> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player_Info> players) {
        this.players = players;
    }

    public boolean get_Play_Able() {
        return play_Able;
    }

    public void set_Play_Able(boolean play_Able) {
        this.play_Able = play_Able;
    }

    public boolean isGame_Continued() {
        return game_Continued;
    }

    public void setGame_Continued(boolean game_Continued) {
        this.game_Continued = game_Continued;
    }

    public long getGame_Duration() {
        return game_Duration;
    }

    public void setGame_Duration(long game_Duration) {
        this.game_Duration = game_Duration;
    }

    public String getGame_Duration_Formatted() {
        return game_Duration_Formatted;
    }

    public void setGame_Duration_Formatted(String game_Duration_Formatted) {
        this.game_Duration_Formatted = game_Duration_Formatted;
    }

    @Override
    public String toString() {
        return "Game_Info{" +
                "name_of_Game='" + name_of_Game + '\'' +
                ", date_of_Game='" + date_of_Game + '\'' +
                ", number_of_Players=" + number_of_Players +
                ", game_Status='" + game_Status + '\'' +
                ", players=" + players +
                ", play_Able=" + play_Able +
                ", game_Continued=" + game_Continued +
                ", game_Duration=" + game_Duration +
                ", pauseOffSet=" + pauseOffSet +
                ", game_Duration_Formatted='" + game_Duration_Formatted + '\'' +
                '}';
    }
}
