package com.example.runqr;

import android.graphics.Bitmap;

public class Photo {
    private Bitmap image;
    private Photo photo;
    /*
    public Photo(){
        this.photo = photo;
    }

     */

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
