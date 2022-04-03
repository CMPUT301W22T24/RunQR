package com.example.runqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class represents a PlayerStats object in the RunQR game.
 * Each Player object has an associated PlayerStats object
 * This class handles PlayerStats attributes such has high score QR code, number of scanned codes, etc
 * These attributes are displayed in ProfileActivity and can be modified in other activities
 * This class will also be used later for the Leaderboard Activity
 *
 */

public class PlayerStats implements Serializable {

    public String username;

    /*stats*/
    //private int highQr;
    //private int lowQr;
    private QRCode highQr;
    private QRCode lowQr;
    private int sumOfScores;
    private int numOfScanned;

    /* in activity, string of rank will be displayed according to int stored in PlayerStats object*/
    /*according to range, will be Platinum, Gold, etc*/
    private int rankNumOfScanned;
    private int rankHighQr;
    private int rankSumOfScores;

    public PlayerStats() {
    }

    /**
     * This method creates a new PlayerStats object within the app and populates it with 0's
     * @param username
     *      given to identify which player this is for
     * @param highQr
     *      initialized to null
     * @param lowQr
     *      initialized to null
     * @param sumScores
     *      initialized to 0
     * @param numScanned
     *      initialized to 0
     * @param rankHighQr
     *      initialized to 0
     * @param rankNumScanned
     *      initialized to 0
     * @param rankSumScores
     *      initialized to 0
     *
     */

    public PlayerStats(String username, QRCode highQr, QRCode lowQr, int sumScores, int numScanned, int rankHighQr, int rankNumScanned, int rankSumScores) {
        highQr = highQr;
        lowQr = lowQr;
        sumOfScores = sumScores;
        numOfScanned = numScanned;
        rankHighQr = rankHighQr;
        rankNumOfScanned = rankNumScanned;
        rankSumOfScores = rankSumScores;
        this.username = username;

    }

    /**
     * This method creates a new PlayerStats activity and connects it with the firebase object based on username
     * @param username
     *      given to identify which player this is for
     */
    public PlayerStats(String username) {
        this.username = username;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Accounts").document(username);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        rankHighQr = Integer.parseInt((String) document.get("highScoreRanking"));
                        rankNumOfScanned = Integer.parseInt((String) document.get("numberOfScannedRanking"));
                        rankSumOfScores = Integer.parseInt((String) document.get("totalScoreRanking"));
                        numOfScanned = Integer.parseInt((String) document.get("numberOfScanned"));
                        sumOfScores = Integer.parseInt((String) document.get("totalScore"));
                        String highQrString = (String) document.get("highScore");
                        String lowQrString = (String) document.get("lowScore");

                    }
                }
            }
        });
    }

    /**
     * This method is called each time an attribute of PlayerStats is changed using one of the class's setters
     * @param field
     *      given to identify which attribute is being altered
     * @param value
     *      new value of this field
     */
    public void updatePlayerStats(String field, String value) {
        final String TAG = "Error Message: ";
        HashMap<String, String> data = new HashMap<>();
        data.put(field, value);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Accounts").document(this.username);
        docRef.update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });;
    }


    public QRCode getHighQr() {
        return highQr;
    }

    public void setHighQr(QRCode highQr) {
        this.highQr = highQr;

        /*if (highQr != null) {
            updatePlayerStats("playerInfo.playerStats.highQr.hash", String.valueOf(highQr.getHash()));
            updatePlayerStats("playerInfo.playerStats.highQr.location", String.valueOf(highQr.getLocation()));
            updatePlayerStats("playerInfo.playerStats.highQr.photo", String.valueOf(highQr.getPhoto()));
            updatePlayerStats("playerInfo.playerStats.highQr.score", String.valueOf(highQr.getScore()));
        } else {
            updatePlayerStats("playerInfo.playerStats.highQr.hash", "N/A");
            updatePlayerStats("playerInfo.playerStats.highQr.location", "N/A");
            updatePlayerStats("playerInfo.playerStats.highQr.photo", "N/A");
            updatePlayerStats("playerInfo.playerStats.highQr.score", "N/A");
        }*/
    }

    public QRCode getLowQr() {
        return lowQr;
    }

    public void setLowQr(QRCode lowQr) {
        this.lowQr = lowQr;
        /*if (lowQr != null) {
            updatePlayerStats("playerInfo.playerStats.lowQr.hash", String.valueOf(lowQr.getHash()));
            updatePlayerStats("playerInfo.playerStats.lowQr.location", String.valueOf(lowQr.getLocation()));
            updatePlayerStats("playerInfo.playerStats.lowQr.photo", String.valueOf(lowQr.getPhoto()));
            updatePlayerStats("playerInfo.playerStats.lowQr.score", String.valueOf(lowQr.getScore()));
        } else {
            updatePlayerStats("playerInfo.playerStats.lowQr.hash", "N/A");
            updatePlayerStats("playerInfo.playerStats.lowQr.location", "N/A");
            updatePlayerStats("playerInfo.playerStats.lowQr.photo", "N/A");
            updatePlayerStats("playerInfo.playerStats.lowQr.score", "N/A");
        }*/

    }

    public int getSumOfScores() {
        return sumOfScores;
    }

    public void setSumOfScores(int sumOfScores) {
        this.sumOfScores = sumOfScores;
        //updatePlayerStats("playerInfo.playerStats.sumOfScores", String.valueOf(sumOfScores));
    }

    public int getNumOfScanned() {
        return numOfScanned;
    }

    public void setNumOfScanned(int numOfScanned) {
        this.numOfScanned = numOfScanned;
        //updatePlayerStats("playerInfo.playerStats.numOfScanned", String.valueOf(numOfScanned));
    }

    public int getRankNumOfScanned() {
        return rankNumOfScanned;
    }

    public void setRankNumOfScanned(int rankNumOfScanned) {
        this.rankNumOfScanned = rankNumOfScanned;
        //updatePlayerStats("playerInfo.playerStats.rankNumOfScanned", String.valueOf(numOfScanned));
    }

    public int getRankHighQr() {
        return rankHighQr;
    }

    public void setRankHighQr(int rankHighQr) {
        this.rankHighQr = rankHighQr;
        //updatePlayerStats("playerInfo.playerStats.rankHighQr", String.valueOf(rankHighQr));
    }

    public int getRankSumOfScores() {
        return rankSumOfScores;
    }

    public void setRankSumOfScores(int rankSumOfScores) {
        this.rankSumOfScores = rankSumOfScores;
        //updatePlayerStats("playerInfo.playerStats.rankSumOfScores", String.valueOf(rankSumOfScores));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
