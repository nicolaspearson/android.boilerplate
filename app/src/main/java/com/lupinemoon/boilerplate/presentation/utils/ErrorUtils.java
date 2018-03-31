package com.lupinemoon.boilerplate.presentation.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.data.models.AppError;
import com.lupinemoon.boilerplate.data.models.AppErrorResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;
import timber.log.Timber;


public class ErrorUtils {

    public static String getErrorMessage(Throwable throwable, Context context) {
        return getErrorMessage(throwable, context, null);
    }

    public static String getErrorMessage(Throwable throwable, Context context, String fallbackMsg) {
        String message = fallbackMsg;
        AppErrorResponse appErrorResponse = null;
        if (throwable instanceof HttpException) {
            Timber.d(
                    "getErrorMessage: throwable = %s, fallbackMsg = %s",
                    throwable.toString(),
                    fallbackMsg);
            HttpException retroErr = (HttpException) throwable;
            try {
                appErrorResponse = appErrorResponseFromRetrofit(retroErr);
            } catch (Exception e) {
                Timber.e(e, "Convert RetrofitError to AppErrorResponse failed");
            }
        }

        if (appErrorResponse != null &&
                (appErrorResponse.getAppError() != null) &&
                (appErrorResponse.getAppError().getMessages() != null) &&
                (appErrorResponse.getAppError().getMessages().size() > 0)) {
            message = appErrorResponse.getAppError().getMessages().get(0).getValue();
        }

        return !TextUtils.isEmpty(message) ? message : context.getString(R.string.message_generic_request_failed);
    }

    private static AppErrorResponse appErrorResponseFromRetrofit(HttpException retroErr) {
        AppErrorResponse appErrorResponse = new AppErrorResponse(new AppError());
        try {
            if ((retroErr != null) && (retroErr.response() != null)) {
                Timber.d("retroErr body length = %s", retroErr.response().errorBody().contentLength());
                String errJson = RetrofitUtils.getHttpExceptionBody(retroErr);
                appErrorResponse = appErrorResponseFromString(errJson);
            }
        } catch (Exception e) {
            Timber.e(e, "Convert Retrofit error to AppErrorResponse failed");
        }
        return appErrorResponse;
    }

    private static AppErrorResponse appErrorResponseFromString(String errJson) {
        AppErrorResponse appErrorResponse = new AppErrorResponse(new AppError());
        AppError appError = new AppError();
        try {
            if (!TextUtils.isEmpty(errJson)) {
                    Gson gson = new GsonBuilder().create();
                    Timber.d("retroErr json = %s", errJson.length() < 4000 ? errJson : "Too Much Output!");
                    Object json = new JSONTokener(errJson).nextValue();
                    if (json instanceof JSONObject) {
                        appError = gson.fromJson(json.toString(), AppError.class);
                        if (appError == null) {
                            appErrorResponse = gson.fromJson(json.toString(), AppErrorResponse.class);
                            appError = appErrorResponse.getAppError();
                            Timber.d("AppErrorResponse: %s", appErrorResponse.toString());
                        }
                    } else if (json instanceof JSONArray) {
                        appError = gson.fromJson(((JSONArray) json).get(0).toString(), AppError.class);
                    }
                    if (appError != null && appError.getMessages() != null && appError.getMessages().size() > 0 && !TextUtils.isEmpty(
                            appError.getMessages().get(0).getValue())) {
                        appErrorResponse.setAppError(appError);
                    } else {
                        appErrorResponse = gson.fromJson(errJson, AppErrorResponse.class);
                    }
                    Timber.w("AppErrorResponse = %s\n%s", appErrorResponse, errJson);
                }
        } catch (Exception e) {
            Timber.e(e, "Convert Retrofit error to AppErrorResponse failed");
        }
        return appErrorResponse;
    }

    /**
     * Returns a new response body that transmits {@code content}. If {@code
     * contentType} is non-null and lacks a charset, this will use UTF-8.
     */
    public static ResponseBody create(MediaType contentType, String content) {
        Charset charset = Util.UTF_8;
        if (contentType != null) {
            charset = contentType.charset();
            if (charset == null) {
                charset = Util.UTF_8;
                contentType = MediaType.parse("" + contentType + "; charset=utf-8");
            }
        }
        Buffer buffer = new Buffer().writeString(content, charset);
        return create(contentType, buffer.size(), buffer);
    }

    /**
     * Returns a new response body that transmits {@code content}.
     */
    public static ResponseBody create(
            final MediaType contentType, final long contentLength, final BufferedSource content) {
        if (content == null) throw new NullPointerException("source == null");

        Timber.d("create ResponseBody: contentLength = %s", contentLength);
        return new ResponseBody() {
            @Override
            public MediaType contentType() {
                Timber.d("getContentType: %s", contentType);
                return contentType;
            }

            @Override
            public long contentLength() {
                Timber.d("getContentLength: %s", contentLength);
                return contentLength;
            }

            @Override
            public BufferedSource source() {
                Timber.d("getSource: %s", content);
                return content;
            }
        };
    }

}
