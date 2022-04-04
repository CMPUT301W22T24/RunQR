package com.example.runqr;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a QRCode object that is scanned by a player via a Scanner object and added to player's QRLibrary.
 * QRCodes have 4 main attributes: score, hash, location and photo.
 * Location and photo may be null if user declines the option.
 * Hash and score can not be null.
 */

public class QRCode implements Serializable {

    private int score;
    private Location location;
    private String hash;
    private Bitmap photo;
    private CommentLibrary commentLibrary;
    private ArrayList<Player> scannedByList = new ArrayList<Player>();


    // If both location and photo are denied
    public QRCode(String hash){
        this.hash = hash;
        this.score = scoreQRCode(hash);
        this.commentLibrary = new CommentLibrary();

    }

    // If location is allowed and photo is denied
    public QRCode(String hash, Location location) {
        this.score = scoreQRCode(hash);
        this.hash = hash;
        this.location = location;
        this.commentLibrary = new CommentLibrary();

    }

    /*public void setScore(int score) {
        this.score = score;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }*/



    // If location is denied and photo is allowed
    public QRCode(String hash, Bitmap photo) {
        this.score = scoreQRCode(hash);
        this.hash = hash;
        this.photo = photo;
        this.commentLibrary = new CommentLibrary();
    }

    // If both location and photo are allowed
    public QRCode(String hash, Location location, Bitmap photo) {
        this.score = scoreQRCode(hash);
        this.hash = hash;
        this.location = location;
        this.photo = photo;
        this.commentLibrary = new CommentLibrary();
    }



    public QRCode() {
    }


    /**
     * This sets the score of the QRCode.
     * @param score
     *      An int to set the score of the code to.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * This sets the location of the QRCode.
     * @param location
     *      A Location to set the location to.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * This sets the hash of the code.
     * @param hash
     *      A String representing the new hash to set hash to.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * This sets the photo of the code.
     * @param photo
     *      A Photo object to set the photo to.
     */
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    /**
     * This sets the comment library of the code.
     * @param commentLibrary
     *      A CommentLibrary object to set the commentLibrary to.
     */
    public void setCommentLibrary(CommentLibrary commentLibrary) {
        this.commentLibrary = commentLibrary;
    }



    /**
     * This method gets the QRCode's score.
     * @return
     *      An int representing the score of the QRCode.
     */
    public int getScore() {
        return this.score;

    }

    /**
     * This method gets the QRCode's hash.
     * @return
     *      String representing the hash (SHA-256 Hash) of the QRCode's contents.
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * This method gets the photo associated with the QRCode.
     * @return
     *      Photo object representing an image of the object on which the QRCode exists.
     */

    public Bitmap getPhoto() {
        return this.photo;
    }


    /**
     * This method gets the location associated with the QRCode.
     * @return
     *      Location object representing the geolocation coordinates (X,Y) at which the QRCode was found.
     */
    public Location getLocation(){
        return this.location;
    }

    /**
     * This method gets the commentLibrary associated with the QRCode which contains all of its comments.
     * @return
     *      The CommentLibrary object associated with the given QRCode.
     */
    public CommentLibrary getCommentLibrary(){ return this.commentLibrary; }


    /**
     * This method calculates the score of the QRCode from the hash using the following scoring algorithm:
     * number of occurrences, n, of each hexadecimal digit in the hash is counted and adds to score in the following way: score += digit ^ (n-1).
     * After incrementing the score for each digit and its corresponding occurrence, the final score returned is: floor(log(score_after_sum)) where score_after_sum is score after incrementing for each digit.
     * @param hash
     *      The String representing the hash of the QRCode from which the score is calculated.
     * @return
     *      An int representing the score of the QRCode after applying above scoring algorithm.
     */
    public int scoreQRCode(String hash){
        int QRScore = 0;

        //DO SCORING
        HashMap<Character, Integer> charCount = characterCount(hash);

        ArrayList<Character> charDomain = new ArrayList<Character>(Arrays.asList('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'));

        ArrayList<Integer> scoreDomain = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        for (Map.Entry entry : charCount.entrySet()) {

            for (int i=0; i < charDomain.size(); i +=1){
                if( entry.getKey() == charDomain.get(i)) {
                    int numOccurrences = Integer.parseInt(entry.getValue().toString());
                    //int b = Integer.parseInt(a.toString());
                    QRScore += Math.pow(scoreDomain.get(i), (double) numOccurrences - 1);
                }
            }

        }

        return (int) Math.floor(Math.log(QRScore));
    }



    /**
     * This is a method borrowed from GeeksForGeeks which counts the number of occurrences for each character in a given string.
     * The information is then stored in the form of HashMap<Character, Integer>.
     * Source URL: https://www.geeksforgeeks.org/java-program-to-count-the-occurrence-of-each-character-in-a-string-using-hashmap/.
     * @param inputString
     *      The String argument for which character occurrences are counted.
     * @return
     *      The HashMap<Character, Integer> which stores the number of occurrences for each character in inputString.
     */
    public HashMap<Character, Integer> characterCount(String inputString)
    {
        // Creating a HashMap containing char
        // as a key and occurrences as  a value
        HashMap<Character, Integer> charCountMap
                = new HashMap<Character, Integer>();

        // Converting given string to char array

        char[] strArray = inputString.toCharArray();

        // checking each char of strArray
        for (char c : strArray) {
            if (charCountMap.containsKey(c)) {

                // If char is present in charCountMap,
                // incrementing it's count by 1
                charCountMap.put(c, charCountMap.get(c) + 1);
            }
            else {

                // If char is not present in charCountMap,
                // putting this char to charCountMap with 1 as it's value
                charCountMap.put(c, 1);
            }
        }

        return charCountMap;
    }




}
