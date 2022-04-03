package com.example.runqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a custom ArrayAdapter<QRCode> class to display player's QRCodeLibrary in the form of a ListView in QRLibraryActivity.
 * This class allows the hash and score of the QRCode to be displayed in QRLibraryActivity's ListView.
 */

public class QRList extends ArrayAdapter<QRCode> implements Serializable {

    //private QRLibrary QRCodes;
    private ArrayList<QRCode> QRCodes;
    private Context context;

    //public QRList(Context context, QRLibrary QRCodes){
    public QRList(Context context, ArrayList<QRCode> QRCodes){
        super(context,0, (List<QRCode>) QRCodes);
        this.QRCodes = QRCodes;
        this.context = context;
    }



    /**
     * This method gets the view of the content of an item in the QRLibraryActivity's ListView.
     * @param position
     *      An int of the position on item in the ListView.
     * @param convertView
     *      A view object for an item in the ListView.
     * @param parent
     *      A ViewGroup which is parent for the ListView item's content view.
     * @return
     *      A view object for an item in the ListView whose contents are QRCode score and hash.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.qrlibrary_content, parent,false);
        }

        //QRCode QRcode = QRCodes.getQRCode(position);
        QRCode QRcode = QRCodes.get(position);


        TextView QRCodeScore = view.findViewById(R.id.qrcode_score_text);
        TextView QRCodeHash = view.findViewById(R.id.qrcode_hash_text);

        QRCodeScore.setText("Score: "+ QRcode.getScore());
        QRCodeHash.setText("Hash: " + QRcode.getHash());

        return view;

    }

    public void setQRCodes(ArrayList<QRCode> QRCodes) {
        this.QRCodes = QRCodes;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
