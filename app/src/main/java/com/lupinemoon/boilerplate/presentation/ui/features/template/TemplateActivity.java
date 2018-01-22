package com.lupinemoon.boilerplate.presentation.ui.features.template;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.databinding.ActivityTemplateBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseVMPActivity;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;

public class TemplateActivity extends BaseVMPActivity<TemplateContract.ViewModel, TemplateContract.Presenter, ActivityTemplateBinding> implements TemplateContract.View {

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_template;
    }

    @Override
    protected int getAnalyticsNameResource() {
        return R.string.app_name;
    }

    @Override
    protected boolean monitorOfflineMode() {
        return false;
    }

    @Nullable
    @Override
    public TemplateViewModel createViewModel(@Nullable BaseViewModel.State savedViewModelState) {
        // Create the view model
        return new TemplateViewModel(this, savedViewModelState);
    }

    @Nullable
    @Override
    public TemplatePresenter createPresenter(@NonNull Bundle args) {
        // Create the presenter
        return new TemplatePresenter(this, args);
    }

    @Nullable
    @Override
    public ActivityTemplateBinding createBinding(@NonNull Activity activity, int layoutId) {
        return DataBindingUtil.setContentView(activity, layoutId);
    }

    @Override
    public ActivityTemplateBinding getBinding() {
        return super.getBinding();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the view model variable
        getBinding().setViewModel((TemplateViewModel) getViewModel());
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

}
