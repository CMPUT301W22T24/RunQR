package com.example.runqr;

import android.graphics.Bitmap;

public class Photo extends Camera{
    private static Bitmap image;
    //private Bitmap image;
    //private byte image;

    public static Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
