package com.example.runqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class QRLibraryActivity extends AppCompatActivity {

    ListView QRList;
    ArrayAdapter<QRCode> QRAdapter;
    ArrayList<QRCode> QRDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlibrary);

        QRList = findViewById(R.id.qrlibrary_list);

        QRDataList = new ArrayList<QRCode>(); //convert string list to arraylist



        QRAdapter = new QRList(this, QRDataList);

        QRList.setAdapter(QRAdapter);


        // Code below borrows from: https://stackoverflow.com/questions/4709870/setonitemclicklistener-on-custom-listview
        QRList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Button deleteButton = (Button) findViewById(R.id.button3);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Object listCity = cityList.getItemAtPosition(position);
                        dataList.remove(position);
                        cityList.setAdapter(cityAdapter);


                    }
                });

            }


        });

    }






    }
}