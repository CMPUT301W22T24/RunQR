package com.example.runqr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.runqr.placeholder.LeaderboardItemComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    ListView scoreList;
    ArrayAdapter<LeaderboardItem> scoreAdapter;
    ArrayList<LeaderboardItem> scoreDataList;
    ArrayList<String> players;
    ArrayList<String> scores;
    int mode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Player player = (Player) getIntent().getSerializableExtra("Player");
        PlayerStats playerStats = player.getPlayerStats();



        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LeaderboardActivity.super.onBackPressed();
                //finish();
            }

        });


        Button mostValuableButton = (Button) findViewById(R.id.button_most_valuable);
        mostValuableButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                refreshListView("playerInfo.playerStats.highQr.score");
            }
        });

        Button mostScannedButton = (Button) findViewById(R.id.button_most_scanned);
        mostScannedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                refreshListView("playerInfo.playerStats.numOfScanned");
            }
        });

        Button highestSumButton = (Button) findViewById(R.id.button_sum);
        highestSumButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                refreshListView("playerInfo.playerStats.sumOfScores");
            }
        });

        /* default view is most valuable*/
        refreshListView("playerInfo.playerStats.highQr.score");
    }

    public void refreshListView(String scoreString) {

        players = new ArrayList<String>();
        scores = new ArrayList<String>();

        scoreList = findViewById(R.id.leaderboard_list);
        scoreDataList = new ArrayList<LeaderboardItem>();
        scoreAdapter = new LeaderboardCustomList(this, scoreDataList);
        scoreList.setAdapter(scoreAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String finalScoreString = scoreString;
        db.collection("Accounts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String thisPlayer = (String) document.get("playerInfo.playerAccount.username");
                        String thisScore = String.valueOf(document.get(finalScoreString));
                        if (thisScore != "null") {
                            //scoreDataList.add(new LeaderboardItem(thisPlayer, thisScore));
                            players.add(thisPlayer);
                            scores.add(thisScore);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Log.d(TAG, "player is " + thisPlayer + " and score is " + thisScore);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                for (int i = 0; i<scores.size();i++){
                    scoreDataList.add((new LeaderboardItem(players.get(i), scores.get(i))));
                    scoreAdapter.notifyDataSetChanged();
                }
                LeaderboardItemComparator leaderboardItemComparator = new LeaderboardItemComparator();
                Collections.sort(scoreDataList, leaderboardItemComparator);
                scoreAdapter.notifyDataSetChanged();
            }
        });
    }


}
