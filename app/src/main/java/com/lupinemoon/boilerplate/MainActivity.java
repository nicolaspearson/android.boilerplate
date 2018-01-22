package com.lupinemoon.boilerplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.lupinemoon.boilerplate.presentation.ui.features.landing.LandingActivity;
import com.lupinemoon.boilerplate.presentation.ui.features.splash.SplashActivity;
import com.lupinemoon.boilerplate.presentation.utils.AndroidUtils;
import com.lupinemoon.boilerplate.presentation.widgets.AppLifecycleHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppLifecycleHandler.isApplicationVisible() && MainApplication.isLoggedIn()) {
            startHomeActivity(getIntent());
        } else {
            startSplashActivity();
        }

        if (BuildConfig.DEBUG) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            AndroidUtils.getDeviceScreenDensity(metrics.densityDpi);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (AppLifecycleHandler.isApplicationVisible() && MainApplication.isLoggedIn()) {
            startHomeActivity(intent);
        }
    }

    private void startSplashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtras(this.getIntent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        this.startActivity(intent);
    }

    private void startHomeActivity(Intent newIntent) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.putExtras(newIntent != null ? newIntent : this.getIntent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        this.startActivity(intent);
    }
}