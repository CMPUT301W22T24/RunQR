package com.example.runqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    ListView scoreList;
    ArrayAdapter<LeaderboardItem> scoreAdapter;
    ArrayList<LeaderboardItem> scoreDataList;
    String[] players;
    String[] scores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        scoreList = findViewById(R.id.leaderboard_list);

        String[] players = {"Player1", "Player2"};
        String[] scores = {"1","2"};

        scoreDataList = new ArrayList<>();
        for (int i = 0; i<scores.length;i++){
            scoreDataList.add((new LeaderboardItem(players[i], scores[i])));
        }

        scoreAdapter = new LeaderboardCustomList(this, scoreDataList);
        scoreList.setAdapter(scoreAdapter);
    }


}