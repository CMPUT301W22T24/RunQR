package com.example.runqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class QRList extends ArrayAdapter<QRCode> {

    private ArrayList<QRCode> QRCodes;
    private Context context;

    public QRList(Context context, ArrayList<QRCode> QRCodes){
        super(context,0, QRCodes);
        this.QRCodes = QRCodes;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.activity_qrlibrary, parent,false);
        }

        QRCode QRcode = QRCodes.get(position);

        TextView QRCode = view.findViewById(R.id.qrcode_text);
        TextView QRCodeHash = view.findViewById(R.id.qrcode_hash_text);

        QRCode.setText("QR Code");
        QRCodeHash.setText(QRcode.getHash());

        return view;

    }





}
