package com.lupinemoon.boilerplate.presentation.ui.features.splash;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import timber.log.Timber;
import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.databinding.ActivitySplashBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseActivity;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.features.landing.LandingActivity;
import com.lupinemoon.boilerplate.presentation.ui.features.login.LoginActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> implements SplashContract.View {

    Thread splashThread;

    Boolean autoLogin = false;

    private SplashContract.Presenter presenter;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getAnalyticsNameResource() {
        return R.string.analytics_splash;
    }

    @Override
    protected boolean monitorOfflineMode() {
        return false;
    }

    @Override
    public void setPresenter(IBasePresenter presenter) {
        this.presenter = (SplashContract.Presenter) presenter;
    }

    @Override
    public SplashContract.Presenter getPresenter() {
        return presenter;
    }

    @Nullable
    @Override
    public ActivitySplashBinding createBinding(@NonNull Activity activity, int layoutId) {
        return DataBindingUtil.setContentView(activity, layoutId);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPresenter(new SplashPresenter(this, getIntent().getExtras()));

        String versionText = String.format(
                getString(R.string.version),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE);
        getBinding().versionTextView.setText(versionText);

        if (BuildConfig.SPLASH_ANIMATION_ENABLED) {
            startAnimations();
        } else {
            navigateToNextActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.d("onNewIntent: %s", intent);
    }

    private void startAnimations() {
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_zoom_in);
        logoAnim.reset();

        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.splash_slide_up_fade_in);
        logoAnim.reset();

        getBinding().splashImageView.clearAnimation();
        getBinding().splashImageView.startAnimation(logoAnim);

        getBinding().versionTextView.clearAnimation();
        getBinding().versionTextView.startAnimation(textAnim);

        int colorFrom = ContextCompat.getColor(getApplicationContext(), R.color.grey_3e);
        int colorTo = ContextCompat.getColor(getApplicationContext(), R.color.black);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                colorFrom,
                colorTo);
        colorAnimation.setDuration(2000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                getBinding().splashContainer.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();

        splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 1900) {
                        sleep(100);
                        waited += 100;
                    }
                    throw new InterruptedException();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    navigateToNextActivity();
                }
            }
        };
        splashThread.start();
    }

    private void navigateToNextActivity() {
        // TODO: Check the auth token
        if (autoLogin) {
            presenter.performAutoLogin();
        } else {
            startLoginActivity();
        }
    }

    @Override
    public void startLandingActivity() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.putExtras(this.getIntent());
        this.finish();
        this.startActivity(intent);
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtras(this.getIntent());
        this.finish();
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}