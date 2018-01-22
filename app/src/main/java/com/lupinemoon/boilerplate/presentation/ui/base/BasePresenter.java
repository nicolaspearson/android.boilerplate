package com.lupinemoon.boilerplate.presentation.ui.base;

import android.content.Intent;
import android.os.Bundle;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter implements IBasePresenter {

    protected CompositeDisposable nonViewDisposables;

    public BasePresenter() {
        nonViewDisposables = new CompositeDisposable();
    }

    @Override
    public void createView() {

    }

    @Override
    public void viewCreated() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent intent) {

    }

    @Override
    public void requestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public Bundle saveInstanceState(Bundle outState) {
        return outState;
    }

    @Override
    public void restoreInstanceState(Bundle inState) {

    }

    @Override
    public void destroyView() {

    }

    @Override
    public void destroy() {
        if (nonViewDisposables != null) {
            nonViewDisposables.clear();
            nonViewDisposables = null;
        }
    }

}