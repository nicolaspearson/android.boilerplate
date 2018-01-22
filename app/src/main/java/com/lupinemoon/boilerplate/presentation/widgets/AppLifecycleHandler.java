package com.lupinemoon.boilerplate.presentation.widgets;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Class to track the application lifecycle state
 * Within MainApplication call the register method passing in this class.
 */
public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    private static String visibleActivity;

    /**
     * Returns data about the started/stopped state of the application.
     * @return boolean is application started or stopped
     */
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    /**
     * Returns data about the resumes/paused state of the application.
     * @return boolean is application started or stopped
     */
    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

    /**
     * Returns the name of the last resumed activity
     * @return String name of last resumed activity
     */
    public static String getVisibleActivity() {
        return visibleActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        if (activity != null) {
            visibleActivity = activity.getClass().getSimpleName();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        Timber.w("Application is in foreground: %b", (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        Timber.w("Application is visible: %b", (started > stopped));
    }

}