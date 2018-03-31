package com.lupinemoon.boilerplate.presentation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.lupinemoon.boilerplate.MainApplication;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.data.storage.interfaces.Storage;

import timber.log.Timber;

public class AndroidUtils {

    public static boolean checkPlayServices(final Activity activity, boolean showErrorDialog) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (showErrorDialog) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(
                            activity,
                            resultCode,
                            Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Storage storage = MainApplication.getStorage(activity.getApplicationContext());
                    boolean mustShowPlayServicesRequest = storage.getBoolean(
                            Constants.KEY_USER_RESPONSE_SHOW_PLAY_SERVICES_REQUIRED,
                            true);

                    if (mustShowPlayServicesRequest) {
                        DialogUtils.showCustomAlertDialog(
                                activity,
                                activity.getString(R.string.title_play_services_not_found),
                                activity.getString(R.string.message_play_services_not_found),
                                activity.getString(R.string.dont_show_again),
                                Constants.KEY_USER_RESPONSE_SHOW_PLAY_SERVICES_REQUIRED,
                                R.string.ok,
                                R.string.not_now,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (com.lupinemoon.boilerplate.presentation.utils.AndroidUtils.isPackageInstalled(
                                                activity,
                                                Constants.PLAY_SERVICES_PACKAGE_NAME)) {
                                            activity.startActivity(new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(Constants.URI_PLAY_STORE_PLAY_SERVICES)));
                                        } else {
                                            activity.startActivity(new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(Constants.URI_WEB_PLAY_SERVICES)));
                                        }

                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Auto Dismissed
                                    }
                                },
                                DialogUtils.AlertType.NONE);
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static boolean checkPlayServicesNoDialogs(final Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    public static String getDeviceId(Context context) {
        String deviceId = MainApplication.getStorage(context).getString(Constants.KEY_GCM_DEVICE_ID);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = com.lupinemoon.boilerplate.presentation.utils.AndroidUtils.getSecureDeviceId(context);
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = StringUtils.getUniqueUUID().substring(0, 15);
            }
            MainApplication.getStorage(context).putString(Constants.KEY_GCM_DEVICE_ID, deviceId);
        }
        return deviceId;
    }

    public static boolean isLollipopAndGreaterBuild() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static int getDeviceScreenDensity(int density) {

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                Timber.d("Device Screen Density: LDPI (%d)", density);
                break;

            case DisplayMetrics.DENSITY_MEDIUM:
                Timber.d("Device Screen Density: MDPI (%d)", density);
                break;

            case DisplayMetrics.DENSITY_HIGH:
                Timber.d("Device Screen Density: HDPI (%d)", density);
                break;

            case DisplayMetrics.DENSITY_XHIGH:
                Timber.d("Device Screen Density: XHDPI (%d)", density);
                break;

            case DisplayMetrics.DENSITY_XXHIGH:
                Timber.d("Device Screen Density: XXHDPI (%d)", density);
                break;

            case DisplayMetrics.DENSITY_XXXHIGH:
                Timber.d("Device Screen Density: XXXHDPI (%d)", density);
                break;

            case DisplayMetrics.DENSITY_560:

            default:
                Timber.d("Device Screen Density: Unknown (%d)", density);
                break;
        }

        return density;
    }

    public static boolean isPackageInstalled(Activity activity, String packageName) {
        boolean isPackageInstalled;
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(packageName, 0);
            isPackageInstalled = (info.packageName.equals(packageName));
        } catch (PackageManager.NameNotFoundException e) {
            //application doesn't exist
            isPackageInstalled = false;
        }
        return isPackageInstalled;
    }

    public static String getSecureDeviceId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String deviceName = Build.MODEL;
        String deviceManufacturer = Build.MANUFACTURER;
        return String.format("%s %s", deviceManufacturer, deviceName);
    }
}
