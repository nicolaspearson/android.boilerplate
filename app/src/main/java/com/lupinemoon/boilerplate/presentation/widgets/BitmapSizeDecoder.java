package com.lupinemoon.boilerplate.presentation.widgets;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;

import java.io.File;
import java.io.IOException;

/**
 * Class to extract bitmap metadata without creating the actual bitmap
 */
public class BitmapSizeDecoder implements ResourceDecoder<File, BitmapFactory.Options> {

    @Override
    public boolean handles(
            @NonNull File source, @NonNull Options options) throws IOException {
        return false;
    }

    @Nullable
    @Override
    public Resource<BitmapFactory.Options> decode(
            @NonNull File source,
            int width,
            int height,
            @NonNull Options options) throws IOException {
        BitmapFactory.Options bimapOptions = new BitmapFactory.Options();
        bimapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), bimapOptions);
        return new SimpleResource<>(bimapOptions);
    }
}