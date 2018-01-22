package com.lupinemoon.boilerplate.presentation.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

public class DrawableUtils {

    public static Drawable getTintedVectorDrawable(Context context, int vecRes, int colorRes) {
        Drawable vectorDrawable = VectorDrawableCompat.create(context.getResources(), vecRes, null);
        if (vectorDrawable != null) {
            vectorDrawable = DrawableCompat.wrap(vectorDrawable);
            DrawableCompat.setTint(vectorDrawable, ContextCompat.getColor(context, colorRes));
        }
        return vectorDrawable;
    }
}
