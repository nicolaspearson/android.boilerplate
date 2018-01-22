package com.lupinemoon.boilerplate.presentation.ui.features.template;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

class TemplatePresenter extends BasePresenter implements TemplateContract.Presenter {

    private TemplateContract.View templateView;

    TemplatePresenter(@NonNull TemplateContract.View view, @NonNull Bundle args) {
        // Set the view locally
        templateView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void performAction(String actionText) {
        templateView.showSnackbarMsg(actionText, Snackbar.LENGTH_SHORT);
    }

}
