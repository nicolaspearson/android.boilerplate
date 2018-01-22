package com.lupinemoon.boilerplate.presentation.ui.features.landing;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lupinemoon.boilerplate.data.storage.AppRepository;
import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;
import com.lupinemoon.boilerplate.presentation.utils.NetworkUtils;

class LandingPresenter extends BasePresenter implements LandingContract.Presenter {

    private LandingContract.View landingView;

    private AppRepository appRepository = AppRepository.getInstance();

    LandingPresenter(@NonNull LandingContract.View view, @NonNull Bundle args) {
        // Set the view locally
        landingView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void retryQueuedRequests() {
        if (NetworkUtils.hasActiveNetworkConnection(landingView.getActivity().getApplicationContext())) {
            appRepository.retryNetworkRequests(landingView.getActivity().getApplicationContext());
        }
    }
}

