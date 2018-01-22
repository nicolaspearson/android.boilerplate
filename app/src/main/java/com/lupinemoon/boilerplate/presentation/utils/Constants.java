package com.lupinemoon.boilerplate.presentation.utils;

@SuppressWarnings("WeakerAccess")
public class Constants {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final int BACK_PRESSED_EXIT_TIME_INTERVAL = 2000;

    public static final String KEY_NO_AUTH_TOKEN = "no_auth_token";

    // PREFS
    public static final String KEY_AUTH_TOKEN = "auth_token";
    public static final String KEY_LAUNCH_COUNT = "launch_count";
    public static final String KEY_GCM_DEVICE_ID = "gcm_device_id";
    public static final String KEY_PLAY_SERVICES_VERSION = "play_services_version";
    public static final String KEY_ANDROID_OS_VERSION = "android_os_version";

    // CRASHLYTICS LOGGING VALUES
    public static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    public static final String CRASHLYTICS_KEY_TAG = "tag";
    public static final String CRASHLYTICS_KEY_MESSAGE = "message";

    // PLAY SERVICES USER INTERACTION
    public static final String KEY_USER_RESPONSE_SHOW_PLAY_SERVICES_REQUIRED = "user_play_services_response";
    public static final String PLAY_SERVICES_PACKAGE_NAME = "com.google.android.gms";
    public static final String URI_PLAY_STORE_PLAY_SERVICES = "market://details?id=com.google.android.gms";
    public static final String URI_WEB_PLAY_SERVICES = "https://play.google.com/store/apps/details?id=com.google.android.gms";

    public static final int DEFAULT_SELECTOR_DELAY = 150;

}
