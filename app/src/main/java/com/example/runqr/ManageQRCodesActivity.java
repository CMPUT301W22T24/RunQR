package com.example.runqr;

import static android.content.ContentValues.TAG;

import static com.example.runqr.MainActivity.collectionReference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.runqr.placeholder.LeaderboardItemComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ManageQRCodesActivity extends AppCompatActivity {

    private ListView codeList;
    private ArrayAdapter<String> codeAdapter;
    private ArrayList<String> dataList;
    private ArrayList<Player> scannedByList = new ArrayList<Player>();
    private boolean confirmClicked = false;
    FirebaseFirestore db;
    Integer countSuccess = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_qrcodes);


        db = FirebaseFirestore.getInstance();
        final CollectionReference ref = db.collection("Accounts");// asynchronously retrieve all documents

        Intent intent = getIntent();
        dataList = intent.getStringArrayListExtra("list of QRCodes");


        codeList = findViewById(R.id.codes_list);


        codeAdapter = new ArrayAdapter<>(this, R.layout.content, dataList);

        codeList.setAdapter(codeAdapter);
        /** handles the deleting feature **/
        codeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                confirmClicked = true;
                //dataList.remove(position);
                String codeHash = dataList.get(position);
                final Button button = (Button) findViewById(R.id.confirm_button);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if(confirmClicked){
                            db.collection("QR Codes").document(codeHash.toString())
                                    .collection("Players")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    scannedByList.add(document.toObject(Player.class));

                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });


                            for(int i =0; i< scannedByList.size();i++){
                                Player player = scannedByList.get(i);
                                player.getPlayerQRLibrary().deleteQRCodeWithHash(codeHash);
                                updateStats(player);
                                //CALL BAILEYS FUNCTION TO UPDATE PLAYER STATS
                            }
                            if(countSuccess == scannedByList.size()){
                                updateQrCodesInDB();
                            }
                            countSuccess = 0;

                            db.collection("QR Codes").document(codeHash.toString())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });




                            dataList.remove(position);
                            codeList.setAdapter(codeAdapter);
                        }
                        confirmClicked = false;


                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //intent.putExtra("Player QRLibrary", playerQRLibrary);
        intent.putExtra("CodeList updated", dataList);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void updateStats(Player player){
        QRLibrary qrLibrary = player.getPlayerQRLibrary();
        ArrayList<QRCode> qrcodeList = qrLibrary.getQRCodeList();
        int maxScore = 0;
        int minScore = 0;
        int sumOfScores = 0;
        int numOfScanned = 0;
        QRCode highQr = null;
        QRCode lowQr = null;
        for(int i=0;i<qrcodeList.size(); i++) {
            numOfScanned = numOfScanned + 1;
            QRCode qrcode = qrcodeList.get(i);
            sumOfScores = sumOfScores + qrcode.getScore();
            if (minScore == 0 && maxScore == 0) {
                maxScore = qrcode.getScore();
                minScore = qrcode.getScore();
                highQr = qrcode;
                lowQr = qrcode;
            } else if (qrcode.getScore() > maxScore) {
                maxScore = qrcode.getScore();
                highQr = qrcode;
            } else if (qrcode.getScore() < minScore) {
                minScore = qrcode.getScore();
                lowQr = qrcode;
            }
        }
        player.getPlayerStats().setHighQr(highQr);
        player.getPlayerStats().setLowQr(lowQr);
        player.getPlayerStats().setSumOfScores(sumOfScores);
        player.getPlayerStats().setNumOfScanned(numOfScanned);
        savePlayerToDatabase(player);
    }

    void savePlayerToDatabase(Player player) {
        String usernameData = player.getPlayerAccount().getUsername();
        HashMap<String, Player> data = new HashMap<>();
        data.put("playerInfo", player);
        collectionReference
                .document(usernameData)
                .set(player)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data has been added successfully!");
                        countSuccess += 1;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }



    void updateQrCodesInDB(){
        ArrayList<LeaderboardItem> mostValuableQRDataList;
        ArrayList<String> mostValuableQRPlayers;
        ArrayList<String> mostValuableQRScores;
        mostValuableQRPlayers = new ArrayList<String>();
        mostValuableQRScores = new ArrayList<String>();
        mostValuableQRDataList = new ArrayList<LeaderboardItem>();
        ArrayList<LeaderboardItem> mostQRDataList;
        ArrayList<String> mostQRPlayers;
        ArrayList<String> mostQRScores;
        mostQRPlayers = new ArrayList<String>();
        mostQRScores = new ArrayList<String>();
        mostQRDataList = new ArrayList<LeaderboardItem>();
        ArrayList<LeaderboardItem> highestPointDataList;
        ArrayList<String> highestPointPlayers;
        ArrayList<String> highestPointScores;
        highestPointPlayers = new ArrayList<String>();
        highestPointScores = new ArrayList<String>();
        highestPointDataList = new ArrayList<LeaderboardItem>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String finalScoreStringNumOfScanned = "playerStats.numOfScanned";
        String finalScoreStringSumOfScores = "playerStats.sumOfScores";
        String finalScoreStringHighestQR = "playerStats.highQr.score";
        db.collection("Accounts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String thisPlayer = (String) document.get("playerAccount.username");
                        String highestQRScore = String.valueOf(document.get(finalScoreStringHighestQR));
                        String  sumOfScoresScore= String.valueOf(document.get(finalScoreStringSumOfScores));
                        String numOfScannedScore = String.valueOf(document.get(finalScoreStringNumOfScanned));
                        mostValuableQRPlayers.add(thisPlayer);
                        mostValuableQRScores.add(highestQRScore);
                            //scoreDataList.add(new LeaderboardItem(thisPlayer, thisScore));
                        highestPointPlayers.add(thisPlayer);
                        highestPointScores.add(sumOfScoresScore);
                        mostQRPlayers.add(thisPlayer);
                        mostQRScores.add(numOfScannedScore);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                for (int i = 0; i<mostQRScores.size();i++){
                    mostQRDataList.add((new LeaderboardItem(mostQRPlayers.get(i), mostQRScores.get(i))));
                }
                for (int i = 0; i<highestPointScores.size();i++){
                    highestPointDataList.add((new LeaderboardItem(highestPointPlayers.get(i), highestPointScores.get(i))));
                }
                for (int i = 0; i<mostValuableQRScores.size();i++){
                    mostValuableQRDataList.add((new LeaderboardItem(mostValuableQRPlayers.get(i), mostValuableQRScores.get(i))));
                }
                LeaderboardItemComparator leaderboardItemComparator = new LeaderboardItemComparator();
                Collections.sort(mostQRDataList, leaderboardItemComparator);
                Collections.sort(highestPointDataList, leaderboardItemComparator);
                Collections.sort(mostValuableQRDataList, leaderboardItemComparator);
                for (int i = 0; i<mostQRScores.size();i++){
                    String player = mostQRDataList.get(i).getPlayer();
                    int rankMostQR = i+1;
                    int rankHighestPoint;
                    int rankMostValuable;
                    for(int j=0;j<highestPointScores.size();j++){
                        if(player == highestPointDataList.get(j).getPlayer()){
                            rankHighestPoint = j + 1;
                            for(int k=0;k<highestPointScores.size();k++){
                                if(player == mostValuableQRDataList.get(k).getPlayer()){
                                    rankMostValuable = k + 1;
                                    loadAndStorePlayer(player, rankMostQR, rankHighestPoint, rankMostValuable);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    void loadAndStorePlayer(String username, int rankMostQR, int rankHighestPoint, int rankMostValuable){
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Accounts").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Player player = document.toObject(Player.class);
                        //currentPlayer = objectMapper.convertValue(doc1, Player.class);
                        player.getPlayerStats().setRankHighQr(rankMostValuable);
                        player.getPlayerStats().setRankSumOfScores(rankHighestPoint);
                        player.getPlayerStats().setRankNumOfScanned(rankMostQR);
                        savePlayerToDatabase(player);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

