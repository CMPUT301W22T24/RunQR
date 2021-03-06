package com.example.runqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

// This class is the activity that displays a list of QRCode items from a player's QRLibrary in a ListView format.
// This activity has a delete button which allows players to delete QRCodes from their QRLibrary.

public class QRLibraryActivity extends AppCompatActivity {

    ListView QRList;
    ArrayAdapter<QRCode> QRAdapter;
    //QRLibrary QRDataList;
    ArrayList<QRCode> QRDataList;
    Boolean deleteCode = false;
    Boolean allowDeletion;
    QRCode QRCodeToShow;
    QRCode QRCodeToDelete;
    Integer clickedPos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlibrary);

        /*
        // Get intent that started this activity to retrieve player's QRLibrary object which contains QRCodes to display in this activity
        QRLibrary playerQRLibrary = (QRLibrary) getIntent().getSerializableExtra("Player QRLibrary");

         */

        Player currentPlayer = (Player) getIntent().getSerializableExtra("Player QRLibraryActivity");
        allowDeletion = (Boolean) getIntent().getSerializableExtra("Allow Deletion?");
        QRList = findViewById(R.id.qrlibrary_list);

        //QRDataList = new ArrayList<QRCode>(); //convert string list to arraylist
        //QRDataList = playerQRLibrary;
        QRLibrary playerQRLibrary = currentPlayer.getPlayerQRLibrary();
        QRDataList = playerQRLibrary.getQRCodeList();

        /*
        QRDataList = new ArrayList<QRCode>();
        QRCode testcode = new QRCode("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
        QRDataList.add(testcode);

         */

        QRAdapter = new QRList(this, QRDataList);
        QRList.setAdapter(QRAdapter);

        FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.delete_qr_button);

        if (!allowDeletion){
            deleteButton.setVisibility(View.GONE);
        }


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCode = true;

            }});



        QRList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                if (deleteCode) {

                    QRCodeToDelete = QRDataList.get(position);
                    if (currentPlayer.getPlayerStats().getNumOfScanned() <= 1) {
                        currentPlayer.getPlayerStats().setHighQr(null);
                        currentPlayer.getPlayerStats().setLowQr(null);
                        Log.d("if statement", "reached size 1");
                    } else {

                        if (QRCodeToDelete.getScore() == currentPlayer.getPlayerStats().getHighQr().getScore()) {
                            //find next highest
                            int currentHighestScore = 0;
                            int i;
                            QRCode currentHighestQR = null;
                            for (i = 0; i < playerQRLibrary.getQRCodeList().size(); i++) {
                                QRCode currentQR = playerQRLibrary.getQRCodeList().get(i);
                                if (QRCodeToDelete != currentQR) {
                                    if (currentQR.getScore() > currentHighestScore) {
                                        currentHighestQR = currentQR;
                                        currentHighestScore = currentHighestQR.getScore();

                                    }
                                }
                            }

                            currentPlayer.getPlayerStats().setHighQr(currentHighestQR);

                        } else if (QRCodeToDelete.getScore() == currentPlayer.getPlayerStats().getLowQr().getScore()) {
                            //find next lowest
                            int currentLowestScore = 99999;
                            int i;
                            QRCode currentLowestQR = null;
                            for (i = 0; i < playerQRLibrary.getQRCodeList().size(); i++) {
                                QRCode currentQR = playerQRLibrary.getQRCodeList().get(i);
                                if (QRCodeToDelete != currentQR) {
                                    if (currentQR.getScore() < currentLowestScore) {
                                        currentLowestQR = currentQR;
                                        currentLowestScore = currentLowestQR.getScore();

                                    }
                                }
                            }
                            currentPlayer.getPlayerStats().setLowQr(currentLowestQR);

                        }
                        Log.d("else statement", "reached larger size "+ currentPlayer.getPlayerStats().getNumOfScanned());
                    }
                    /*change general playerstats*/
                    int currentNumberScanned = currentPlayer.getPlayerStats().getNumOfScanned();
                    currentPlayer.getPlayerStats().setNumOfScanned(currentNumberScanned-1);
                    int currentSum = currentPlayer.getPlayerStats().getSumOfScores();
                    currentPlayer.getPlayerStats().setSumOfScores(currentSum - QRCodeToDelete.getScore());


                    //QRDataList.deleteQRCode(QRCodeToDelete);
                    playerQRLibrary.deleteQRCode(QRCodeToDelete);
                    //QRDataList.remove(QRCodeToDelete);
                    //QRList.setAdapter(QRAdapter);
                    QRAdapter.notifyDataSetChanged();


                    deleteCode = false;
                }

                else {
                    // Open DisplayQRCode activity to display details of clicked QRCode object, pass QRCode object to DisplayQRCodeActivity through intent

                    QRCodeToShow = QRDataList.get(position);
                    Intent intent = new Intent(QRLibraryActivity.this, DisplayQRCodeActivity.class);
                    intent.putExtra("QRCode to display", (Serializable) QRCodeToShow);
                    intent.putExtra("Position of QRCodeClicked", position);
                    startActivityForResult(intent, 2);


                    /*
                    FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.delete_qr_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //QRCode QRCodeToDelete = QRDataList.getQRCode(position);
                            QRCode QRCodeToDelete = QRDataList.get(position);
                            //QRDataList.deleteQRCode(QRCodeToDelete);
                            playerQRLibrary.deleteQRCode(QRCodeToDelete);
                            //QRList.setAdapter(QRAdapter);
                            QRAdapter.notifyDataSetChanged();

                        }
                    });

                 */
                }

            }

        });

        final FloatingActionButton backButton = (FloatingActionButton)  findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //intent.putExtra("Player QRLibrary", playerQRLibrary);
                intent.putExtra("Player QRLibrary Updated", currentPlayer);
                setResult(RESULT_OK, intent);
                //QRLibraryActivity.super.onBackPressed();
                finish();
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                //QRLibrary updatedQRLibrary = (QRLibrary) data.getSerializableExtra("Player QRLibrary");
                //Player updatedCurrentPlayer = (Player) data.getSerializableExtra("Player QRLibrary Updated");
                QRCode updatedQRCode = (QRCode) data.getSerializableExtra("QRCode updated with comments");
                clickedPos = (Integer) data.getSerializableExtra("Clicked Pos");
                //currentPlayer.setPlayerQRLibrary(updatedQRLibrary);
                //currentPlayer = updatedCurrentPlayer;
                QRDataList.set(clickedPos, updatedQRCode);
                QRAdapter.notifyDataSetChanged();
            }
        }
    }



}
