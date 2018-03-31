package com.lupinemoon.boilerplate.data.storage.remote;

import android.content.Context;

import com.lupinemoon.boilerplate.data.models.AuthToken;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.data.network.services.AuthService;
import com.lupinemoon.boilerplate.data.network.services.TemplateService;
import com.lupinemoon.boilerplate.data.storage.AppRepository;
import com.lupinemoon.boilerplate.data.storage.interfaces.AppDataStore;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class AppRemoteDataStore implements AppDataStore {

    private static AppRemoteDataStore appRemoteDataStore;

    private AuthService authService;
    private TemplateService templateService;

    private AppRemoteDataStore(AppRepository appRepository) {
        authService = AuthService.getInstance(appRepository);
        templateService = TemplateService.getInstance(appRepository);
    }

    public static synchronized AppRemoteDataStore getInstance(AppRepository appRepository) {
        if (appRemoteDataStore == null) {
            appRemoteDataStore = new AppRemoteDataStore(appRepository);
        }
        return appRemoteDataStore;
    }

    // region Auth API
    @Override
    public Flowable<AuthToken> doLogin(
            Context context, String msisdn, String password, String websiteId) {
        return authService.doLogin(context, msisdn, password, websiteId);
    }
    // endregion

    // region Template API
    @Override
    public Completable performNotifyApiCall(Context context) {
        return templateService.doNotifyApiCall(context);
    }

    @Override
    public Flowable<KeyValue> fetchKeyValue(Context context, String key) {
        return templateService.getKeyValue(context, key);
    }

    @Override
    public Flowable<KeyValue> saveKeyValue(Context context, KeyValue keyValue) {
        return templateService.postKeyValue(context, keyValue);
    }
    // endregion
}
