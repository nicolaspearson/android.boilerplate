package com.lupinemoon.boilerplate.presentation.ui.features.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

class SplashPresenter extends BasePresenter implements SplashContract.Presenter {

    private SplashContract.View splashView;

    SplashPresenter(@NonNull SplashContract.View view, @NonNull Bundle args) {
        // Set the view locally
        splashView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void performAutoLogin() {
        if (splashView.isAttached()) {
            splashView.startLandingActivity();
        }
    }

}
