package com.lupinemoon.boilerplate.presentation.utils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;
import timber.log.Timber;

public class RetrofitUtils {

    public static boolean isHttp401Error(Throwable throwable) {
        return isHttpError(throwable, 401);
    }

    public static boolean isHttp440Error(Throwable throwable) {
        return isHttpError(throwable, 440);
    }

    private static boolean isHttpError(Throwable throwable, int errorCode) {
        if (throwable != null && errorCode > 0) {
            Timber.d("isHttpError, throwable = %s, errorCode = %s", throwable, errorCode);
            if (throwable instanceof HttpException) {
                HttpException exception = (HttpException) throwable;
                Timber.d(
                        "isHttpError, is Retrofit error = %s, code = %s",
                        exception,
                        exception.code());
                return (exception.code() == errorCode);
            }
        }
        return false;
    }

    static String getHttpExceptionBody(HttpException exception) {
        if ((exception != null) && (exception.response() != null)) {
            HttpException exCopy = new HttpException(exception.response());
            return bodyString(exCopy.response().errorBody());
        } else {
            return "";
        }
    }

    private static byte[] bodyBytes(ResponseBody body) throws IOException {
        BufferedSource source = body.source();
        // Create a clone of the source to avoid consumption
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();
        byte[] bytes = null;
        try {
            bytes = buffer.clone().readByteArray();
        } finally {
            Util.closeQuietly(source);
        }
        return bytes;
    }

    private static String bodyString(ResponseBody body) {
        if (body != null) {
            try {
                byte[] bytes = bodyBytes(body);
                Charset charset = body.contentType() != null ? body.contentType().charset(Util.UTF_8) : Util.UTF_8;
                return new String(bytes, charset.name());
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
