package com.lupinemoon.boilerplate.data.storage.prefs;

import android.graphics.Bitmap;

import com.lupinemoon.boilerplate.data.storage.interfaces.BitmapCache;
import com.lupinemoon.boilerplate.data.storage.interfaces.Storage;

@SuppressWarnings("unused")
public class TransitionBitmapCache implements BitmapCache {

    private Storage storage;
    private static final String KEY = "transition_bitmap_key";

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public static String getKey() {
        return KEY;
    }

    public TransitionBitmapCache(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void putBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            storage.putCache(KEY, bitmap);
        }
    }

    @Override
    public Bitmap getBitmap() {
        return (Bitmap) storage.getCached(KEY);
    }

    @Override
    public void delBitmap() {
        storage.delCached(KEY);
    }
}
