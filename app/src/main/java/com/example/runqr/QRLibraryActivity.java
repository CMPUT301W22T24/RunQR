package com.example.runqr;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class QRLibraryActivity extends AppCompatActivity {

    ListView QRList;
    ArrayAdapter<QRCode> QRAdapter;
    //QRLibrary QRDataList;
    ArrayList<QRCode> QRDataList;
    Account playerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlibrary);

        // get intent
        // To retrieve object in second Activity
        QRLibrary playerQRLibrary = (QRLibrary) getIntent().getSerializableExtra("Player QRLibrary");

        QRList = findViewById(R.id.qrlibrary_list);

        //QRDataList = new ArrayList<QRCode>(); //convert string list to arraylist
        //QRDataList = playerQRLibrary;


        QRDataList = playerQRLibrary.getQRCodeList();

        /*
        QRDataList = new ArrayList<QRCode>();
        QRCode testcode = new QRCode("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
        QRDataList.add(testcode);

         */

        QRAdapter = new QRList(this, QRDataList);

        QRList.setAdapter(QRAdapter);


        // Code below borrows from: https://stackoverflow.com/questions/4709870/setonitemclicklistener-on-custom-listview
        QRList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

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

            }


        });

    }






    }
