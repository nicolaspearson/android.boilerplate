package com.lupinemoon.boilerplate.data.storage.interfaces;

import android.graphics.Bitmap;

@SuppressWarnings("unused")
public interface BitmapCache {

    void putBitmap(Bitmap bitmap);

    Bitmap getBitmap();

    void delBitmap();

}
