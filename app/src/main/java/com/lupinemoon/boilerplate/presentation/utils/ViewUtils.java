package com.lupinemoon.boilerplate.presentation.utils;

import android.view.View;

public class ViewUtils {

    public static boolean isAttachedToWindow(View v) {
        return ((v != null) && (v.getWindowToken() != null));
    }

}
