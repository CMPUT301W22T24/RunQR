package com.example.runqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    ListView scoreList;
    ArrayAdapter<LeaderboardItem> scoreAdapter;
    ArrayList<LeaderboardItem> scoreDataList;
    String[] players;
    String[] scores;
    int mode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        scoreList = findViewById(R.id.leaderboard_list);

        Player player = (Player) getIntent().getSerializableExtra("Player");
        //PlayerStats playerStats = player.getPlayerStats();

        String[] players = {"Player1", "Player2"};
        String[] scores = {"1","2"};

        scoreDataList = new ArrayList<>();
        for (int i = 0; i<scores.length;i++){
            scoreDataList.add((new LeaderboardItem(players[i], scores[i])));
        }

        scoreAdapter = new LeaderboardCustomList(this, scoreDataList);
        scoreList.setAdapter(scoreAdapter);


        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LeaderboardActivity.super.onBackPressed();
                //finish();
            }

        });
    }


}