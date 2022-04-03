package com.example.runqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

/**
 * This class represents a ProfileActivity object in the RunQR game.
 * This class displays all Player Stats such as high score QR, number of scanned codes, etc
 *
 */

public class ProfileActivity extends AppCompatActivity {

    ListView profileList;
    ExtendedFloatingActionButton viewQRCodesButton;
    ArrayAdapter<ProfileItem> profileAdapter;
    ArrayList<ProfileItem> profileDataList;
    String[] items;
    String[] values;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // To retrieve object in second Activity
        Player player = (Player) getIntent().getSerializableExtra("Player");
        PlayerStats playerStats = player.getPlayerStats();



        profileList = findViewById(R.id.profile_list);
        viewQRCodesButton = findViewById(R.id.view_qrcodes_button);

        String highQrString = "N/A";
        String lowQrString = "N/A";

        if (playerStats.getHighQr() != null) {
            highQrString = String.valueOf(playerStats.getHighQr().getScore());
        }

        if (playerStats.getLowQr() != null) {
            lowQrString = String.valueOf(playerStats.getLowQr().getScore());
        }

        String[] items = {"Scanned QR Codes: ", "Total Score: ", "Rank (number of codes): ", "Rank (player): ", "Rank (highest scoring code): ", "Highest Scoring: ", "Lowest Scoring: "};
        String[] values = {String.valueOf(playerStats.getNumOfScanned()), String.valueOf(playerStats.getSumOfScores()), String.valueOf(playerStats.getRankNumOfScanned()), String.valueOf(playerStats.getRankSumOfScores()), String.valueOf(playerStats.getRankHighQr()), highQrString, lowQrString};

        profileDataList = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            profileDataList.add((new ProfileItem(items[i], values[i])));
        }

        profileAdapter = new ProfileCustomList(this, profileDataList);
        profileList.setAdapter(profileAdapter);


        viewQRCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the player's qr library
                Intent intent = new Intent(ProfileActivity.this, QRLibraryActivity.class);
                intent.putExtra("Player QRLibraryActivity", player);
                intent.putExtra("Allow Deletion?", false);
                startActivity(intent);
            }
        });

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ProfileActivity.super.onBackPressed();
                //finish();
            }

        });




    }



}
