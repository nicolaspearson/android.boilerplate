package com.lupinemoon.boilerplate.presentation.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import timber.log.Timber;

public class NetworkUtils {

    public static String getUrlParameter(String separator, String requiredKey, String url) {
        try {
            String[] urlParts = url.split(separator);
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String pair[] = param.split("=", 2);
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value;
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                        if (requiredKey.equals(key)) {
                            return value;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, e.toString());
        }
        return "";
    }


    public static boolean hasActiveNetworkConnection(Context context) {
        if (context != null) {
            try {
                NetworkInfo networkInfo = getNetworkInfo(context);
                return networkInfo != null && networkInfo.isConnectedOrConnecting();
            } catch (Exception e) {
                Timber.e(e, "No connection to connectivity service.");
            }
        }
        return false;
    }

    /**
     * Get the network info
     * @param context The application context
     * @return NetworkInfo
     */
    private static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     * @param context The application context
     * @return True if connected to a network
     */
    public static boolean isConnected(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     * @param context The application context
     * @return True if connected to wifi
     */
    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     * @param context The application context
     * @return True if connected to a mobile network
     */
    public static boolean isConnectedMobile(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     * @param context The application context
     * @return True if connected to a fast network
     */
    public static boolean isConnectedFast(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(),info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     *
     * @param type NetworkInfo type
     * @param subType NetworkInfo subtype
     * @return True if the connection is fast
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 KBPS
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 KBPS
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 KBPS
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 KBPS
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 KBPS
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 KBPS
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 MBPS
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 KBPS
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 MBPS
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 KBPS
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 MBPS
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 MBPS
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 MBPS
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 KBPS
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ MBPS
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

}
