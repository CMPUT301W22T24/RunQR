package com.example.runqr;

import java.io.Serializable;
import java.util.ArrayList;

public class QRLibrary implements Serializable {

    //private ArrayList<QRCode> QRLibrary;
    private ArrayList<QRCode> QRCodeList;
    private int QRLibraryScore;

    public QRLibrary(){
        //this.QRLibrary = new ArrayList<QRCode>();
        this.QRCodeList = new ArrayList<QRCode>();
        this.QRLibraryScore = 0;
    }

    public void addQRCode(QRCode newQRCode){

        //QRLibrary.add(newQRCode);
        QRCodeList.add(newQRCode);
        QRLibraryScore += newQRCode.getScore();
    }

    //public ArrayList<QRCode> getQRLibrary(){
    public ArrayList<QRCode> getQRCodeList(){
        //return this.QRLibrary;
        return this.QRCodeList;
    }

    public int getQRLibraryScore() {
        return this.QRLibraryScore;
    }
    public void deleteQRCode(QRCode removeQRCode) {
        //QRLibrary.remove(removeQRCode);
        QRCodeList.remove(removeQRCode);
        QRLibraryScore -= removeQRCode.getScore();
    }

    public QRCode getQRCode(int position) {
        //return QRLibrary.get(position);
        return QRCodeList.get(position);
    }

}
