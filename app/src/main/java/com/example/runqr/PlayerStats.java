package com.example.runqr;

public class PlayerStats {
    private Player player;

    /*stats*/
    private QRCode high_qr;
    private QRCode low_qr;
    private int sum_of_scores;
    private int num_of_scanned;

    /* in activity, string of rank will be displayed according to int stored in PlayerStats object*/
    /*according to range, will be Platinum, Gold, etc*/
    private int rank_num_of_scanned;
    private int rank_high_qr;
    private int rank_sum_of_scores;

    /*
    public PlayerStats(Player player) {
        this.player = player;
    }
     */
}
