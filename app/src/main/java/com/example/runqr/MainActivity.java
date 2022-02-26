package com.example.runqr;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button addQR = findViewById(R.id.add_qr_button);
        addQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AddQRFragment addQRFragment = new AddQRFragment();
                fragmentTransaction.add(R.id.addQRFragment_container,addQRFragment);
                fragmentTransaction.commit();

                //getSupportFragmentManager().beginTransaction().add(R.id.container, new AddQRFragment()).commit();

                //final View addQR = findViewById(R.id.fragment_container_view);
                //addQR.setVisibility(View.VISIBLE);



                //AddQRFragment addQRFragment = new AddQRFragment();
                //FragmentManager manager = getFragmentManager();
                //FragmentTransaction transaction = manager.beginTransaction();
                //transaction.add(R.id.fragment_container_view,AddQRFragment.class,"OPEN_SCANNER");
                //transaction.replace(R.id.container,);
                //transaction.addToBackStack(null);
                //transaction.commit();





            }



        });


    }

}