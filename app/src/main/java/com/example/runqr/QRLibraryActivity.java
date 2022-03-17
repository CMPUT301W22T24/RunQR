package com.example.runqr;

import android.content.Intent;
import android.os.Bundle;
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
    Boolean delete_code = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlibrary);

        /*
        // Get intent that started this activity to retrieve player's QRLibrary object which contains QRCodes to display in this activity
        QRLibrary playerQRLibrary = (QRLibrary) getIntent().getSerializableExtra("Player QRLibrary");

         */

        Player currentPlayer = (Player) getIntent().getSerializableExtra("Player QRLibraryActivity");
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
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_code = true;

            }});



        QRList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (delete_code) {
                    //QRCode QRCodeToDelete = QRDataList.getQRCode(position);
                    QRCode QRCodeToDelete = QRDataList.get(position);


                    // BELOW NEED TO FINISH IMPLEMENTING UPDATING OF SCORES SHOWN IN PROFILE ACTIVITY //
                    // SO FAR: total number of scanned codes is updated properly upon deletion but not actual scores

                    /*updating playerstats*/
                    /*get highest and lowest*/


                        int numberScanned = currentPlayer.getPlayerStats().getNumOfScanned();
                        currentPlayer.getPlayerStats().setNumOfScanned(numberScanned-1);

                        if (QRCodeToDelete.getScore() == currentPlayer.getPlayerStats().getHighQr()) {
                            //find next highest
                            //needs updating
                        }
                        else if (QRCodeToDelete.getScore() == currentPlayer.getPlayerStats().getLowQr()) {
                            //find next lowest
                            //needs updating
                        }

                    //QRDataList.deleteQRCode(QRCodeToDelete);
                    playerQRLibrary.deleteQRCode(QRCodeToDelete);
                    //QRList.setAdapter(QRAdapter);
                    QRAdapter.notifyDataSetChanged();
                    delete_code = false;
                }

                else {
                    // Open DisplayQRCode activity to display details of clicked QRCode object, pass QRCode object to DisplayQRCodeActivity through intent
                    QRCode codeToShow = QRDataList.get(position);
                    Intent intent = new Intent(QRLibraryActivity.this, DisplayQRCodeActivity.class);
                    intent.putExtra("QRCode to display", (Serializable) codeToShow);
                    startActivity(intent);


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




}
