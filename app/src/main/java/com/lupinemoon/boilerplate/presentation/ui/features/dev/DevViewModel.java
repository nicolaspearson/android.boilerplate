package com.lupinemoon.boilerplate.presentation.ui.features.dev;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

public class DevViewModel extends BaseViewModel implements DevContract.ViewModel {

    private DevContract.View devView;

    DevViewModel(@NonNull DevContract.View view, @Nullable State savedInstanceState) {
        // Set the view locally
        devView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public State getInstanceState() {
        // Not required
        return null;
    }

    public void onExportDatabaseClick(View view) {
        devView.getPresenter().performExportDatabase();
    }

    public void testPostFail(View view) {
        devView.getPresenter().testPostFail();
    }
}
