package com.lupinemoon.boilerplate.data.network.services;

import android.content.Context;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.data.network.interfaces.TemplateApi;
import com.lupinemoon.boilerplate.data.network.rest.ServiceGenerator;
import com.lupinemoon.boilerplate.data.storage.AppRepository;

public class TemplateService {

    private static TemplateService templateService;

    private TemplateApi templateApi;

    private AppRepository appRepository;

    private TemplateService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public static synchronized TemplateService getInstance(AppRepository appRepository) {
        if (templateService == null) {
            templateService = new TemplateService(appRepository);
        }
        return templateService;
    }

    private synchronized TemplateApi getTemplateApi(Context context) {
        // We want to reuse the same instance
        if (templateApi == null) {
            templateApi = ServiceGenerator.getInstance().createService(TemplateApi.class, context);
        }

        return templateApi;
    }

    public Completable doNotifyApiCall(Context context) {
        return getTemplateApi(context).doNotifyApiCall("No Auth");
    }

    public Flowable<KeyValue> getKeyValue(Context context, String key) {
        return getTemplateApi(context).getKeyValue(appRepository.getAuthString(context), key);
    }

    public Flowable<KeyValue> postKeyValue(Context context, KeyValue keyValue) {
        return getTemplateApi(context).postKeyValue(appRepository.getAuthString(context), keyValue.getKey(), keyValue.getValue());
    }
}
