package com.lupinemoon.boilerplate.data.analytics.interfaces;


public interface IAnalyticsService {

    void onViewStart(String viewResourceName);

    void reportPlayServicesVersion(String playServicesVersion);

    void reportAndroidOsVersion(String androidOsVersion);
}
