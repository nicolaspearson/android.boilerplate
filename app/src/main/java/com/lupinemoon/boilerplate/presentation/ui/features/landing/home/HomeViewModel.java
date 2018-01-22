package com.lupinemoon.boilerplate.presentation.ui.features.landing.home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

public class HomeViewModel extends BaseViewModel implements HomeContract.ViewModel {

    private HomeContract.View homeView;

    HomeViewModel(@NonNull HomeContract.View view, @Nullable State savedInstanceState) {
        // Set the view locally
        homeView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public BaseViewModel.State getInstanceState() {
        // Not required
        return null;
    }

}
