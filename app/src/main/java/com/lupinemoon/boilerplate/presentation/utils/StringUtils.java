package com.lupinemoon.boilerplate.presentation.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import timber.log.Timber;

public class StringUtils {

    /**
     * Encode a string to UTF
     *
     * @param str string to decode.
     * @return String value or null
     */
    public static String encodeUtf8(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, "Error encoding string to UTF-8");
        }
        return "";
    }

    /**
     * Decodes a UTF string.
     *
     * @param str string to decode.
     * @return String value or null
     */
    public static String decodeUtf8(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, "Error decoding string to UTF-8");
        }
        return "";
    }

    public static String getUniqueUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
