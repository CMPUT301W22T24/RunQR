package com.example.runqr;

import java.io.Serializable;

/**
 * This class represents a Player object in the RunQR game.
 * Each player in the game has 3 attributes: Account, PLayerStats and QRLibrary objects.
 * This class handles the 2 main tasks of a player: adding a QRCode and deleting an added QRCode.
 *
 */

public class Player implements Serializable {
    private Account playerAccount;
    private PlayerStats playerStats;
    private QRLibrary playerQRLibrary;

    public Player(Account playerAccount, PlayerStats playerStats, QRLibrary playerQRLibrary) {
        this.playerAccount = playerAccount;
        this.playerStats = playerStats;
        this.playerQRLibrary = playerQRLibrary;

    }

    public Player(){

        this.playerAccount = new Account();
        this.playerStats = new PlayerStats();
        this.playerQRLibrary = new QRLibrary();
    }

    public Player(Account playerAccount) {

        this.playerAccount = playerAccount;
    }

    /**
     * This method adds a QRCode scanned by the player to their QRLibrary by calling QRLibrary's addQRCode() method.
     * @param QRCodeToAdd
     *      The QRCode to add to player's QRLibrary.
     */

    public void addQRCode(QRCode QRCodeToAdd){

        this.playerQRLibrary.addQRCode(QRCodeToAdd);
        //Log.d("QR Code Hash: ", QRCodeToAdd.getHash());
    }

    /**
     * This method deletes a QRCode scanned by the player from their QRLibrary by calling QRLibrary's deleteQRCode() method.
     * @param QRCodeToDelete
     *      The QRCode to delete from player's QRLibrary.
     */
    public void deleteQRCode(QRCode QRCodeToDelete) {
        this.playerQRLibrary.deleteQRCode(QRCodeToDelete);
    }

    /**
     * This method gets the player's account.
     * @return
     *      The Account object associated with given player.
     */
    public Account getPlayerAccount(){
        return this.playerAccount;
    }

    /**
     * This method gets the player's statistics.
     * @return
     *      The PlayerStats object associated with given player.
     */
    public PlayerStats getPlayerStats() {
        return this.playerStats;
    }

    /**
     * This method gets the player's library of added QRCodes.
     * @return
     *      The QRLibrary object associated with given player. Stores the player's added QRCodes.
     */
    public QRLibrary getPlayerQRLibrary() {
        return this.playerQRLibrary;
    }


 // DO WE NEED THIS FUNCTION??
    public void scanQR(){

    }







}
