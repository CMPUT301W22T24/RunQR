package com.example.runqr;

public class Location{
    private double x;
    private double y;

    /*
    public Location(String provider) {
        super(provider);
    }
    */

    /*
    public Location(android.location.Location l) {
        super(l);
    }
     */

    public Location (double x, double y) {
        this.x = x;
        this.y =y;
    }



    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {

        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}