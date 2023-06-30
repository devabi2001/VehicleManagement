package com.thirumalaivasa.vehiclemanagement.Models;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class ImageData {
    static Map<String,Bitmap> imageList = new HashMap<>();

    public static void setImage(String key, Bitmap image){
        imageList.put(key, image);
    }

    public static Bitmap getImage(String key){
        return imageList.get(key);
    }

}
