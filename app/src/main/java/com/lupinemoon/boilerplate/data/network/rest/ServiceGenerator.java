package com.lupinemoon.boilerplate.data.network.rest;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;
import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.data.models.AppError;
import com.lupinemoon.boilerplate.data.models.AppErrorResponse;
import com.lupinemoon.boilerplate.presentation.services.rxbus.RxBus;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.PostRequestFailedEvent;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.SubscriptionExpiredEvent;
import com.lupinemoon.boilerplate.presentation.utils.Constants;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServiceGenerator {

    private static ServiceGenerator serviceGenerator = new ServiceGenerator();

    private static OkHttpClient okHttpClient;

    private static Retrofit retrofit;

    private static Retrofit.Builder retrofitBuilder = null;

    private ServiceGenerator() {
        // Empty constructor
    }

    public static synchronized ServiceGenerator getInstance() {
        if (serviceGenerator == null) {
            serviceGenerator = new ServiceGenerator();
        }
        return serviceGenerator;
    }

    public <S> S createService(Class<S> serviceClass, Context context) {
        return getRetrofit(context).create(serviceClass);
    }

    private static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            if (retrofitBuilder == null) {
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                        .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'").create();

                // Set the endpoint url
                retrofitBuilder = new Retrofit.Builder().baseUrl(BuildConfig.HTTP_API_ENDPOINT)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson));
            }

            retrofit = retrofitBuilder.client(getOkHttpClient(context)).build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            okHttpClient = createOkHttpClient(context);
        }
        return okHttpClient;
    }

    private static OkHttpClient createOkHttpClient(final Context context) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }

        // Set the timeout intervals
        clientBuilder = clientBuilder.connectTimeout(BuildConfig.HTTP_TIMEOUT, SECONDS)
                .readTimeout(BuildConfig.HTTP_TIMEOUT, SECONDS).writeTimeout(BuildConfig.HTTP_TIMEOUT, SECONDS);

        // This allows us to monitor uploads in 16 kb chunks
        clientBuilder.socketFactory(new RestrictedSocketFactory(16 * 1024));

        long DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50 mb

        // Install the HTTP cache in the application cache directory
        File cacheDir = context.getDir("service_api_cache", Context.MODE_PRIVATE);
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        clientBuilder = clientBuilder.cache(cache);

        clientBuilder.addInterceptor(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {

                Request origRequest = chain.request();
                Request.Builder builder = origRequest.newBuilder().addHeader("platform", "android")
                        .addHeader("User-Agent", "Boilerplate Android Version " + BuildConfig.VERSION_NAME)
                        .addHeader("app-version", String.valueOf(BuildConfig.VERSION_CODE))
                        .addHeader("os_version", String.valueOf(Build.VERSION.SDK_INT));

                Request request = builder.build();

                if (BuildConfig.DEBUG) {
                    Timber.d("HEADERS = %s", request.headers());
                }

                Response origResponse;
                try {
                    // Perform request (original request will be executed)
                    origResponse = chain.proceed(request);
                } catch (IOException exception) {
                    Timber.w(exception, "Response Exception Error");
                    // Notify error using the original request data.
                    notifyRequestFailed(request);
                    throw exception;
                }

                Response response = origResponse;
                if (!response.isSuccessful() || BuildConfig.DEBUG) {
                    ResponseBody body = origResponse.body();
                    String responseString = body.string();

                    // Recreate the response since responseString consumed it
                    response = origResponse.newBuilder().body(ResponseBody.create(body.contentType(), responseString))
                            .build();

                    if (!response.isSuccessful()) {
                        if (response.code() == 440) {
                            // User subscription has expired, show a dialog and
                            // navigate the user to login screen or subscribe
                            RxBus.getDefault().post(new SubscriptionExpiredEvent());
                        } else {
                            try {
                                // And again, if we receive remote error responses
                                // Let's notify and queue the request
                                notifyRequestFailed(request);
                                Gson gson = new GsonBuilder().create();
                                AppErrorResponse appErrorResponse = gson.fromJson(responseString,
                                        AppErrorResponse.class);
                                if (appErrorResponse != null && appErrorResponse.getAppError() != null) {
                                    AppError error = appErrorResponse.getAppError();
                                    Timber.d("Error, %s", error);
                                }
                            } catch (Throwable throwable) {
                                Timber.d("Parse error-response error");
                            }
                        }
                    }
                }

                return response;
            }
        });
        return clientBuilder.build();
    }

    /**
     * Method to receive all necessary properties of an OkHttpCall
     * to reconstruct it as such and return an object to manually invoke the call again.
     *
     * This is typically a utility method, and is called by any clients that need a call constructed.
     *
     * @param context
     * @param method
     * @param url
     * @param headers
     * @param requestBody
     * @return
     */
    public static Call retryRequest(Context context, String method, String url, Headers headers,
            RequestBody requestBody) {
        try {
            Request.Builder builder = new Request.Builder();
            builder.headers(headers);
            builder.removeHeader("platform");
            builder.removeHeader("User-Agent");
            builder.removeHeader("app-version");
            builder.removeHeader("os_version");
            builder.removeHeader("Retry");
            builder.addHeader("Retry", "true");
            builder.method(method, requestBody);
            builder.url(url);
            Request request = builder.build();
            Timber.d("Request Retry: %s", requestBody);
            return getOkHttpClient(context).newCall(request);
        } catch (Exception e) {
            Timber.e(e, "Retry request failed: Create Call object for client failed.");
        }

        return null;
    }

    /**
     * Is notified by OkHttp about a failed network request.
     * Will extract the request properties and post the event to repo.
     *
     * A failed request can be due to an exception, OR if we receive certain response.
     *
     * @param request Ok http request object to be persisted via POJO's
     */
    private static void notifyRequestFailed(Request request) {

        try {
            Timber.d("Request failed: %s", request);
            if (request != null && !TextUtils.isEmpty(request.method()) && request.method().equalsIgnoreCase("POST")
                    && request.headers() != null) {
                String authHeader = request.header("Authorization");
                String retryHeader = request.header("Retry");
                Timber.d("RetryHeader: %s | AuthHeader: %s", retryHeader, authHeader);
                if (!TextUtils.isEmpty(authHeader) && !authHeader.equals(Constants.KEY_NO_AUTH_TOKEN)
                        && TextUtils.isEmpty(retryHeader)) {
                    RxBus.getDefault().post(new PostRequestFailedEvent(request));
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Notify request failed event did not complete.");
        }
    }
}
