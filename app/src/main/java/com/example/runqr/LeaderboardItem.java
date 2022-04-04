package com.example.runqr;

public class LeaderboardItem implements Comparable<LeaderboardItem>{
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

    public int getScoreInt() { if (score == "null") return 0; else return Integer.parseInt(score); }

    public void setScore(String score) { this.score = score; }

    @Override
    public int compareTo(LeaderboardItem otherLeaderboardItem) {
        return -(Integer.compare(getScoreInt(), otherLeaderboardItem.getScoreInt()));
    }
}
