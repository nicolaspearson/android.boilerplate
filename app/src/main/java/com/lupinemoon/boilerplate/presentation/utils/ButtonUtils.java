package com.lupinemoon.boilerplate.presentation.utils;

public class ButtonUtils {

    public static long updateActionInterval(long lastActionTime) {
        return updateActionInterval(lastActionTime, 1000);
    }

    public static long updateActionInterval(long lastActionTime, int timeout) {
        long t = System.currentTimeMillis();
        if ((t - lastActionTime) > timeout) {
            return t;
        } else {
            return lastActionTime;
        }
    }
}
