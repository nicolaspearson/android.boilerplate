package com.lupinemoon.boilerplate.data.analytics;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.lupinemoon.boilerplate.data.analytics.interfaces.IAnalyticsService;

public class AnalyticsService implements IAnalyticsService {

    private static AnalyticsService analyticsService = new AnalyticsService();

    private AnalyticsService() {
        // Empty constructor
    }

    public static synchronized AnalyticsService getInstance() {
        if (analyticsService == null) {
            analyticsService = new AnalyticsService();
        }
        return analyticsService;
    }

    @Override
    public void onViewStart(String viewResourceName) {
        Answers.getInstance().logContentView(new ContentViewEvent().putContentName(viewResourceName));
    }

    @Override
    public void reportPlayServicesVersion(String playServicesVersion) {
        CustomEvent customEvent = new CustomEvent("Play Services Version");
        customEvent.putCustomAttribute("Version", playServicesVersion);
        Answers.getInstance().logCustom(customEvent);
    }

    @Override
    public void reportAndroidOsVersion(String androidOsVersion) {
        CustomEvent customEvent = new CustomEvent("Android OS Version");
        customEvent.putCustomAttribute("Version", androidOsVersion);
        Answers.getInstance().logCustom(customEvent);
    }
}