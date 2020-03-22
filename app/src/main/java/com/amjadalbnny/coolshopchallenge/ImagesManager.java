package com.amjadalbnny.coolshopchallenge;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImagesManager {

    public static Bitmap applyFilter(Bitmap bitmap){

        // copying to newBitmap for manipulation
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        // height and width of Image
        int imageHeight = newBitmap.getHeight();
        int imageWidth = newBitmap.getWidth();

        // traversing each pixel in Image as an 2D Array
        for (int i = 0; i < imageWidth; i++) {

            for (int j = 0; j < imageHeight; j++) {

                // getting each pixel
                int oldPixel = bitmap.getPixel(i, j);

                // each pixel is made from RED_BLUE_GREEN_ALPHA
                // so, getting current values of pixel
                int oldRed = Color.red(oldPixel);
                int oldBlue = Color.blue(oldPixel);
                int oldGreen = Color.green(oldPixel);
                int oldAlpha = Color.alpha(oldPixel);


                // write your Algorithm for getting new values
                // after calculation of filter

                // Algorithm for getting new values after calculation of filter
                // Algorithm for SEPIA FILTER
                int newRed = (int) (0.393 * oldRed + 0.769 * oldGreen + 0.189 * oldBlue);
                int newGreen = (int) (0.349 * oldRed + 0.686 * oldGreen + 0.168 * oldBlue);
                int newBlue = (int) (0.272 * oldRed + 0.534 * oldGreen + 0.131 * oldBlue);

                // applying new pixel values from above to newBitmap
                int newPixel = Color.argb(oldAlpha, newRed, newGreen, newBlue);
                newBitmap.setPixel(i, j, newPixel);
            }
        }

        return newBitmap;
    }

}
