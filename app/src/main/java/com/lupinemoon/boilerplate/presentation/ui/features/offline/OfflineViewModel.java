package com.lupinemoon.boilerplate.presentation.ui.features.offline;

import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

public class OfflineViewModel extends BaseViewModel implements OfflineContract.ViewModel {

    private OfflineContract.View offlineView;

    OfflineViewModel(@NonNull OfflineContract.View view, @Nullable State savedInstanceState) {
        // Set the view locally
        offlineView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public State getInstanceState() {
        // Not required
        return null;
    }

    @Bindable
    public String getOfflineHeaderMessage() {
        return String.format(Locale.getDefault(), offlineView.getActivity().getApplicationContext().getString(
                R.string.offline_header_message), offlineView.getActivity().getApplicationContext().getString(
                R.string.app_name));
    }

    public void onGotItClick() {
        offlineView.closeOfflineActivity();
    }

}
