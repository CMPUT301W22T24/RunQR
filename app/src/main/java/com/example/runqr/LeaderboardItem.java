package com.example.runqr;

public class LeaderboardItem {
    private String player;
    private String score;

    public LeaderboardItem(String player, String score) {
        this.player = player;
        this.score = score;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
