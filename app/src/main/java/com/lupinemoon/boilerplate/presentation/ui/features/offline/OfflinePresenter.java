package com.lupinemoon.boilerplate.presentation.ui.features.offline;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

class OfflinePresenter extends BasePresenter implements OfflineContract.Presenter {

    private OfflineContract.View offlineView;

    OfflinePresenter(@NonNull OfflineContract.View view, @NonNull Bundle args) {
        // Set the view locally
        offlineView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

}
