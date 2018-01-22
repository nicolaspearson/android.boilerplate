package com.lupinemoon.boilerplate.presentation.ui.features.landing.home;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

class HomePresenter extends BasePresenter implements HomeContract.Presenter {

    private HomeContract.View homeView;

    HomePresenter(@NonNull HomeContract.View view, @NonNull Bundle args) {
        // Set the view locally
        homeView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

}
