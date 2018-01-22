package com.lupinemoon.boilerplate.presentation.ui.features.dev;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;
import java.util.Locale;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.databinding.ActivityDevBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseVMPActivity;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.utils.AnimationUtils;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

public class DevActivity extends BaseVMPActivity<DevContract.ViewModel, DevContract.Presenter, ActivityDevBinding> implements DevContract.View {

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_dev;
    }

    @Override
    protected int getAnalyticsNameResource() {
        return R.string.analytics_dev;
    }

    @Override
    protected boolean monitorOfflineMode() {
        return false;
    }

    @Nullable
    @Override
    public DevViewModel createViewModel(@Nullable BaseViewModel.State savedViewModelState) {
        // Create the view model
        return new DevViewModel(this, savedViewModelState);
    }

    @Nullable
    @Override
    public DevPresenter createPresenter(@NonNull Bundle args) {
        // Create the presenter
        return new DevPresenter(this, args);
    }

    @Nullable
    @Override
    public ActivityDevBinding createBinding(@NonNull Activity activity, int layoutId) {
        return DataBindingUtil.setContentView(activity, layoutId);
    }

    @Override
    public ActivityDevBinding getBinding() {
        return super.getBinding();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnimationUtils.animateTransitionStartActivity(
                this.getActivity(),
                R.anim.slide_in_right_fade_in);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_dev);
        }

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateOnBackPressed(new GenericCallback() {
                    @Override
                    public void execute() {
                        onBackPressed();
                    }
                });
            }
        });

        // Set the view model variable
        getBinding().setViewModel((DevViewModel)getViewModel());
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        AnimationUtils.animateTransitionFinishActivity(
                this.getActivity(),
                R.anim.slide_out_right_fade_out);
    }

    @Override
    public void emailFile(File file) {
        String emailBody = String.format(Locale.getDefault(), "Attached is the realm file for %s, use the Realm Browser to view the contents.", getString(R.string.app_name));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_database));
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.email)));
    }

}
