package com.akapps.scoretrackerv47.Classes;

import android.net.Uri;
import java.io.Serializable;
import java.util.Arrays;

/**
 * This is a player object containing values such as name, games won, lost, tied, current score
 * in-game, total games played, their player text color if they are selected by player to play,
 * there name and there recent 5 scores from their last 5 games.
 */

public class Player_Info implements Serializable{
    private String name;
    private int gamesWon;
    private int gamesLost;
    private int currentScore;
    private int lastScore;
    private int totalGames;
    private int ties;
    private boolean selected;
    private int[] recentScores;
    private int player_Text_Color;
    private boolean play_Able;
    private String player_Image_Path;

    public Player_Info(String name, int gamesWon, int gamesLost, int ties, int currentScore, int totalGames, boolean selected, int[] recentScores,
                       int player_Text_Color ,String player_Image_Path){
        this.name= name;
        this.gamesWon= gamesWon;
        this.gamesLost= gamesLost;
        this.ties= ties;
        this.currentScore= currentScore;
        this.totalGames= totalGames;
        this.selected= selected;
        this.recentScores = recentScores;
        this.player_Text_Color = player_Text_Color;
        this.lastScore = -1;
        this.play_Able = true;
        this.player_Image_Path = player_Image_Path;
    }
    public Player_Info(String name, int gamesWon, int gamesLost, int ties, int currentScore, int totalGames, boolean selected, int[] recentScores,
                       int player_Text_Color ,Uri uri_Image_Path){
        this.name= name;
        this.gamesWon= gamesWon;
        this.gamesLost= gamesLost;
        this.ties= ties;
        this.currentScore= currentScore;
        this.totalGames= totalGames;
        this.selected= selected;
        this.recentScores = recentScores;
        this.player_Text_Color = player_Text_Color;
        this.lastScore = -1;
        this.play_Able = true;
        this.player_Image_Path = null;
    }
    // ***************************** Setters ************************************************
    // set name of player
    public void setName(String name) {
        this.name = name;
    }
    // set number of games won
    public void setGamesWon(int gamesWon){
        this.gamesWon= gamesWon;
    }
    // set number of games lost
    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }
    // set the current score of player
    public void setCurrentScore(int currentScore) {
        this.lastScore = this.currentScore;
        this.currentScore = currentScore;
    }
    // sets the total games played
    public void setTotalGames(int totalGames){
        this.totalGames= totalGames;
    }
    // sets the total games tied
    public void setTies(int ties){
        this.ties= ties;
    }
    // sets the current state of player, is player playing or not
    public void setSelectedState(boolean state) {
        this.selected= state;
    }
    // sets the recent score at a position
    public void setRecentScores(int position, int value) {
        this.recentScores[position] = value;
    }
    // sets an array to be the recent scores
    public void setRecentScores(int[] recentScores) {
        this.recentScores= recentScores;
    }
    // sets a players text color
    public void setPlayer_Text_Color(int player_Text_Color) {
        this.player_Text_Color = player_Text_Color;
    }
    // sets the player's last score
    public void setLastScore(int score){
        this.lastScore = score;
    }
    // sets the player to be playable in continue game
    public void setPlay_Able(boolean play_Able) {
        this.play_Able = play_Able;
    }
    // sets the path of the location of player image
    public void setPlayer_Image_Path(String image_path){
        player_Image_Path = image_path;
    }

    // ***************************** Getters ************************************************
    // returns player name
    public String getName() {
        return name;
    }
    // returns games lost
    public int getGamesLost() {
        return gamesLost;
    }
    // returns games won
    public int getGamesWon() {
        return gamesWon;
    }
    // returns games tied
    public int getTies(){
        return ties;
    }
    // returns current score
    public int getCurrentScore() {
        return currentScore;
    }
    // returns total games
    public int getTotalGames(){
        return  totalGames;
    }
    // returns the state of the switch telling if user is playing or not
    public boolean getSelectedState(){
        return selected;
    }
    // returns array of all recent player scores
    public int[] getRecentScores(){
        return recentScores;
    }
    // returns one score of player at a certain position
    public int getRecentScores(int position){
        return recentScores[position];
    }
    // returns current player text color
    public int getPlayer_Text_Color() {
        return player_Text_Color;
    }
    // returns player last score...which is prior to current score
    public int getLastScore() {
        return lastScore;
    }
    // returns if a player is play able so that a game can be continued
    public boolean get_Play_Able() {
        return play_Able;
    }
    // returns the path of the location of player Image
    public String getPlayer_Image_Path(){
        return player_Image_Path;
    }
    // returns the uri path of the location of player Image

    @Override
    public String toString() {
        return "Player_Info{" +
                "name='" + name + '\'' +
                ", gamesWon=" + gamesWon +
                ", gamesLost=" + gamesLost +
                ", currentScore=" + currentScore +
                ", lastScore=" + lastScore +
                ", totalGames=" + totalGames +
                ", ties=" + ties +
                ", selected=" + selected +
                ", recentScores=" + Arrays.toString(recentScores) +
                ", player_Text_Color=" + player_Text_Color +
                ", play_Able=" + play_Able +
                ", player_Image_Path='" + player_Image_Path + '\'' +
                '}';
    }
}