package com.example.runqr;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java. util. Map;

import java.lang.Math;


public class QRCode implements Serializable {

    private int score;
    private Location location;
    private String hash;


    public QRCode(String hash){

        this.hash = hash;
        this.score = scoreQRCode(hash);

    }

    public QRCode(int score, String hash, Location location) {
        this.score = score;
        this.hash = hash;
        this.location = location;
    }

    public int getScore() {
        return score;
    }

    public String getHash() {
        return hash;
    }



    public Location getLocation(){
        return this.location;
    }

    public int scoreQRCode(String hash){
        int QRScore = 0;

        ArrayList<Character> hashCharArray = new ArrayList<Character>();
        // Creating array of string length
        char[] ch = new char[hash.length()];

        // Copy character by character into array
        for (int i = 0; i < hash.length(); i++) {
            ch[i] = hash.charAt(i);
        }

        // Printing content of array
        for (char c : ch) {
            //System.out.println(c);
            hashCharArray.add(c);
        }

        groupElements(hashCharArray, hashCharArray.size());

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

        return QRScore;
    }

    static HashMap<Character, Integer>  characterCount(String inputString)
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

        ///*
        // Printing the charCountMap
        for (Map.Entry entry : charCountMap.entrySet()) {
            Log.d("PRINT1", entry.getKey() + " " + entry.getValue());
        }
        //*/

        return charCountMap;
    }



    // A simple method to group all occurrences of individual elements
    static void groupElements(ArrayList<Character> arr, int n) {

        // Initialize all elements as not visited
        boolean visited[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            visited[i] = false;
        }

        // Traverse all elements
        for (int i = 0; i < n; i++) {

            // Check if this is first occurrence
            if (!visited[i]) {

                // If yes, print it and all
                // subsequent occurrences
                Log.d("PRINT1", arr.get(i) + " ");
                for (int j = i + 1; j < n; j++) {
                    if (arr.get(i) == arr.get(j)) {
                        Log.d("PRINT2",arr.get(i) + " ");
                        visited[j] = true;
                    }
                }
            }
        }
    }


}
