package com.muno.photoalbum.ImagesManagement;

import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

/**
 * Created by InX on 14/10/2015.
 */
public class ImagesExif {


    public ImagesExif() {

    }

    //First rotate images
    public int getRotationAngle(String fileString) {
        int rotation = 0;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileString);
            //int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

            Log.d("trolo", "Orientation:" + orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                default:
                    rotation = 0;
            }

            Log.d("trolo", "Rotation: "+rotation);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.d("trolo", " -- Orientation:" +rotation);

        return rotation;
    }

    public String getImageOrientation(String fileImage) {
        String imageOrientation = "";

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileImage);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);


            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageOrientation = "landscape";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageOrientation = "portrait";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageOrientation = "landscape";
                    break;
                default:
                    imageOrientation = "portrait";
            }
        }catch (IOException e) {

        }

        return imageOrientation;
    }
}
