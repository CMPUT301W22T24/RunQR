package com.example.runqr;

import java.io.Serializable;
import java.util.ArrayList;

public class Account  implements Serializable {

    private String username;
    private String contactEmail;



    //public ArrayList<>

    public void Account(){
        this.username = "test_username";
    }


    public String getUsername(){
        return this.username;
    }






}
