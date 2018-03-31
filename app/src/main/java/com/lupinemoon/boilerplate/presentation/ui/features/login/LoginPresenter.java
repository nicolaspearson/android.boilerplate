package com.lupinemoon.boilerplate.presentation.ui.features.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.features.landing.LandingActivity;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

    private LoginContract.View loginView;

    LoginPresenter(@NonNull LoginContract.View view, @NonNull Bundle args) {
        // Set the view locally
        loginView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void performLogin(final String mobileNumber, final String password) {
        if (loginView.isAttached()) {
            loginView.showLoading();

            // TODO: Add Login Logic Here
            Flowable.just(mobileNumber, password)
                    .delay(2500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                            if (loginView.isAttached()) {
                                loginView.showLoading();
                                loginView.getActivity().startActivity(new Intent(
                                        loginView.getActivity(),
                                        LandingActivity.class));
                            }
                        }
                    });
        }
    }

    @Override
    public void performSkip() {
        loginView.getActivity().startActivity(new Intent(loginView.getActivity(), LandingActivity.class));
    }
}
