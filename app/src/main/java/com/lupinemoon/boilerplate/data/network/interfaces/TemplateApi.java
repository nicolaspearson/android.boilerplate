package com.lupinemoon.boilerplate.data.network.interfaces;

import com.lupinemoon.boilerplate.data.models.KeyValue;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TemplateApi {

    @GET("notify")
    Completable doNotifyApiCall(
            @Header("Authorization") String authString);

    @GET("fake")
    Flowable<KeyValue> getKeyValue(
            @Header("Authorization") String authString,
            @Field("key") String key);

    @FormUrlEncoded
    @POST("fake")
    Flowable<KeyValue> postKeyValue(
            @Header("Authorization") String authString,
            @Field("key") String key,
            @Field("value") String value);
}
