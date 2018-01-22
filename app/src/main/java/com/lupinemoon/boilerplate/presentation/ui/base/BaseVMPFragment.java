package com.lupinemoon.boilerplate.presentation.ui.base;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import timber.log.Timber;

public abstract class BaseVMPFragment<VM extends IBaseViewModel, P extends IBasePresenter, B extends ViewDataBinding> extends BaseFragment<B> implements IBaseView {

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

        BaseViewModel.State savedViewModelState = null;
        if (savedInstanceState != null) {
            savedViewModelState = savedInstanceState.getParcelable(EXTRA_VIEW_MODEL_STATE);
        }

        setViewModel(createViewModel(savedViewModelState));
        setPresenter(createPresenter(getArguments()));

        if (getPresenter() != null) {
            getPresenter().createView();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Timber.d("onViewCreated: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().viewCreated();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().start();
        }
    }

    @Override
    public void onStop() {
        Timber.d("onStop: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().stop();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().resume();
        }
    }

    @Override
    public void onPause() {
        Timber.d("onPause: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().pause();
        }
        super.onPause();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Timber.d("onViewStateRestored: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Timber.d("onDestroyView: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().destroyView();
        }

        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Timber.d("onActivityResult: %s", getFragmentTag());
        if (getPresenter() != null) {
            getPresenter().activityResult(requestCode, resultCode, intent);
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
    public void onSaveInstanceState(Bundle outState) {
        Timber.d("onSaveInstanceState: %s", getFragmentTag());
        if (getViewModel() != null) {
            outState.putParcelable(EXTRA_VIEW_MODEL_STATE, getViewModel().getInstanceState());
        }

        if (getPresenter() != null) {
            outState = getPresenter().saveInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
    }
}
