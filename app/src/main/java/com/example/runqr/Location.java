package com.example.runqr;

import android.location.LocationListener;

import androidx.annotation.NonNull;

public class Location extends android.location.Location  {
    private int x;
    private int y;

    public Location(String provider) {
        super(provider);
    }

    public Location(android.location.Location l) {
        super(l);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {

        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
