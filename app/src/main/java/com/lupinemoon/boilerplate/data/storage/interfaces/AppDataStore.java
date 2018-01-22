package com.lupinemoon.boilerplate.data.storage.interfaces;

import android.content.Context;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import com.lupinemoon.boilerplate.data.models.AuthToken;
import com.lupinemoon.boilerplate.data.models.KeyValue;

public interface AppDataStore {

    // Template API
    Completable performNotifyApiCall(Context context);
    Flowable<KeyValue> fetchKeyValue(Context context, String key);
    Flowable<KeyValue> saveKeyValue(Context context, KeyValue keyValue);

    // Auth API
    Flowable<AuthToken> doLogin(Context context, String msisdn, String password, String websiteId);
}
