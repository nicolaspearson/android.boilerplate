package com.lupinemoon.boilerplate.presentation.ui.base;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class BaseVMPActivity<VM extends IBaseViewModel, P extends IBasePresenter, B extends ViewDataBinding> extends BaseActivity<B> implements IBaseView {

    private static final String EXTRA_VIEW_MODEL_STATE = "viewModelState";

    private VM viewModel;

    private P presenter;

    @Nullable
    public abstract VM createViewModel(@Nullable BaseViewModel.State savedViewModelState);

    public VM getViewModel() {
        return viewModel;
    }

    public void setViewModel(VM viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    public abstract P createPresenter(@NonNull Bundle args);

    public P getPresenter() {
        return presenter;
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPresenter() != null) {
            getPresenter().createView();
        }

        BaseViewModel.State savedViewModelState = null;
        if (savedInstanceState != null) {
            savedViewModelState = savedInstanceState.getParcelable(EXTRA_VIEW_MODEL_STATE);
        }

        setViewModel(createViewModel(savedViewModelState));
        setPresenter(createPresenter(getIntent().getExtras()));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getPresenter() != null) {
            getPresenter().viewCreated();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getPresenter() != null) {
            getPresenter().start();
        }
    }

    @Override
    public void onStop() {
        if (getPresenter() != null) {
            getPresenter().stop();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPresenter() != null) {
            getPresenter().resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getPresenter() != null) {
            getPresenter().pause();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        if (getPresenter() != null) {
            getPresenter().destroyView();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onDestroy() {
        if (getPresenter() != null) {
            getPresenter().destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getViewModel() != null) {
            outState.putParcelable(EXTRA_VIEW_MODEL_STATE, getViewModel().getInstanceState());
        }

        if (getPresenter() != null) {
            getPresenter().saveInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (getPresenter() != null) {
            getPresenter().restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getPresenter() != null) {
            getPresenter().requestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPresenter() != null) {
            getPresenter().activityResult(requestCode, resultCode, data);
        }
    }
}