package com.example.runqr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the QRLibrary of a Player object which is an ArrayList of QRCode objects.
 * QRLibrary stores the QRCodes scanned by a player in the game and adjusts the total score accordingly.
 * Allows for adding and removing of QRCodes.
 * This class abstracts ArrayList<QRCode> with additional QRLibraryScore attribute.
 */
public class QRLibrary implements Serializable {


    private ArrayList<QRCode> QRCodeList;
    private int QRLibraryScore;

    public QRLibrary(){

       // this.QRCodeList = new ArrayList<QRCode>();
        //this.QRLibraryScore = 0;
    }

    public QRLibrary(ArrayList<QRCode> QRCodeList, int score){

        this.QRCodeList = QRCodeList;
        this.QRLibraryScore = score;
    }

    /**
     * This method adds a new QRCode to player's QRLibrary and increments total score.
     *
     * @param newQRCode
     *      The QRCode object to add to QRLibrary.
     */
    public void addQRCode(QRCode newQRCode){

        QRCodeList.add(newQRCode);
        QRLibraryScore += newQRCode.getScore();
    }

    /**
     * This method deletes a QRCode from QRLibrary and decrements total score.
     * @param removeQRCode
     *      The QRCode to be removed from QRLibrary.
     */
    public void deleteQRCode(QRCode removeQRCode) {
        QRCodeList.remove(removeQRCode);
        QRLibraryScore -= removeQRCode.getScore();
    }

    /**
     * This method gets the ArrayList of QRCode objects contained in QRLibrary.
     * @return
     *      ArrayList of QRCode objects in QRLibrary.
     */
    public ArrayList<QRCode> getQRCodeList(){
        return this.QRCodeList;
    }

    /**
     * This method gets the total score of QRCodes in QRLibrary.
     * @return
     *      Integer representing total score across all QRCodes.
     */
    public int getQRLibraryScore() {
        return this.QRLibraryScore;
    }


    /**
     * This method sets the ArrayList of QRCode objects contained in QRLibrary to QRCodeList.
     * @param QRCodeList
     *      ArrayList of QRCode objects to set QRCodeList to.
     */
    public void setQRCodeList(ArrayList<QRCode> QRCodeList) {
        this.QRCodeList = QRCodeList;
    }

    /**
     * This method sets the score of the QRLibrary.
     * @param QRLibraryScore
     *      The int to set the QRLibraryScore to.
     */
    public void setQRLibraryScore(int QRLibraryScore) {
        this.QRLibraryScore = QRLibraryScore;
    }


    /**
     * This method gets the QRCode at a given position in the QRLibrary's ArrayList.
     * @param position
     *      The index at which the QRCode to be returned is in the list.
     * @return
     *      The QRCode at index position in the ArrayList.
     */
    public QRCode getQRCode(int position) {
        return QRCodeList.get(position);
    }

    /**
     * This method gets the size (number of QRCode objects) of the QRLibrary's data list QRCodeList
     * @return
     *      An int representing number of QRCodes stored in QRLibrary.
     */
    public int ReturnSize(){
        return this.QRCodeList.size();
    }



}
