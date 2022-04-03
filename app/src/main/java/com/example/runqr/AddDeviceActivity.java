package com.example.runqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.lang.reflect.Type;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class AddDeviceActivity extends AppCompatActivity {
    String hashUsername;
    ImageView qrCode;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        qrCode = findViewById(R.id.transferQrcode);
        Player currentPlayer = (Player) getIntent().getSerializableExtra("Player AddDeviceActivity");
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(hashUsername, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
            for (int x = 0; x<200; x++){
                for (int y=0; y<200; y++){
                    bitmap.setPixel(x,y,bitMatrix.get(x,y)? Color.BLACK : Color.WHITE);
                }
            }
            qrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // creating a variable for point which
        // is to

    }
    void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("hash username", null);
        Type type = new TypeToken<String>(){}.getType();
        hashUsername = gson.fromJson(json, type);
        if(hashUsername == null){
            hashUsername = "";
        }
    }
}