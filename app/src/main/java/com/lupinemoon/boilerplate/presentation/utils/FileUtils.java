package com.lupinemoon.boilerplate.presentation.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class FileUtils {

    /**
     * Uses an asset manager to return an input stream for the file name passed in
     * Returns null if file exception occurs.
     *
     * @param assetManager
     * @param fileName
     * @return String or Null if an error occurred
     */
    public static InputStream getInputStreamFromAsset(
            AssetManager assetManager,
            String fileName) {
        try {
            return assetManager.open(fileName);
        } catch (IOException e) {
            Timber.e("Error accessing and returning asset input stream");
            e.printStackTrace();
        } catch (Exception ex) {
            Timber.e("An un-foreseen exception occurred");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Uses an input stream for a file to extract, convert and return the string representation of that file's data
     *
     * @param inputStream
     * @return String or Null if an error occurred
     */
    public static String inputStreamToString(InputStream inputStream) {

        InputStreamReader sReader = new InputStreamReader(inputStream);

        int ch = 0;
        StringBuffer sBuff = new StringBuffer();
        try {
            while ((ch = sReader.read()) != -1) {
                sBuff.append((char) ch);
            }
            return sBuff.toString();
        } catch (IOException e) {
            Timber.e("Error reading string data from file input stream");
            e.printStackTrace();
        } catch (Exception e) {
            Timber.e("An un-expected exception occurred reading data from file input stream");
            e.printStackTrace();
        }
        return null;
    }

    public static String getTextFileFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        String text = null;
        try {
            inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            text = new String(buffer);
        } catch (IOException e) {
            Timber.d("Error loading " + fileName + " from assets: " + e);
        }

        return text;
    }

}
