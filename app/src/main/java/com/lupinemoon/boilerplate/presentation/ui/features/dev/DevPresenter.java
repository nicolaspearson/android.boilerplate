package com.lupinemoon.boilerplate.presentation.ui.features.dev;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.data.storage.AppRepository;
import com.lupinemoon.boilerplate.presentation.ui.base.BasePresenter;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

class DevPresenter extends BasePresenter implements DevContract.Presenter {

    private DevContract.View devView;

    private AppRepository appRepository = AppRepository.getInstance();

    DevPresenter(@NonNull DevContract.View view, @NonNull Bundle args) {
        // Set the view locally
        devView = ActivityUtils.checkNotNull(view, "view cannot be null!");
    }

    @Override
    public void performExportDatabase() {

        devView.showSnackbarMsg(
                devView.getActivity().getString(R.string.export_database),
                Snackbar.LENGTH_SHORT);

        nonViewDisposables.add(
                appRepository.getRealmDatabaseFile(
                        devView.getActivity().getExternalCacheDir())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<File>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull File file) throws Exception {
                                if (devView.isAttached()) {
                                    devView.emailFile(file);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                                Timber.w(throwable);
                                if (devView.isAttached()) {
                                    devView.showSnackbarMsg(
                                            devView.getActivity().getString(R.string.export_failed),
                                            Snackbar.LENGTH_SHORT);
                                }
                            }
                        }));
    }

    @Override
    public void testPostFail() {

        KeyValue kv = new KeyValue("Key", "Value");

        nonViewDisposables.add(
        appRepository.saveKeyValue(devView.getActivity(),kv)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<KeyValue>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull KeyValue keyValue) throws Exception {
                        // Pass through, faux handling
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        // Pass through, faux handling
                    }
                }));

    }
}
