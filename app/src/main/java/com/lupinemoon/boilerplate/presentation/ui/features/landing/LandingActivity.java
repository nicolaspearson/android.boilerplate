package com.lupinemoon.boilerplate.presentation.ui.features.landing;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.MainApplication;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.databinding.ActivityLandingBinding;
import com.lupinemoon.boilerplate.presentation.services.rxbus.RxBus;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.LogoutEvent;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.NetworkRestoredEvent;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.NoNetworkEvent;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseDrawerActivity;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseFragment;
import com.lupinemoon.boilerplate.presentation.ui.features.dev.DevActivity;
import com.lupinemoon.boilerplate.presentation.ui.features.landing.home.HomeFragment;
import com.lupinemoon.boilerplate.presentation.ui.features.login.LoginActivity;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;
import com.lupinemoon.boilerplate.presentation.utils.AndroidUtils;
import com.lupinemoon.boilerplate.presentation.utils.AnimationUtils;
import com.lupinemoon.boilerplate.presentation.utils.Constants;
import com.lupinemoon.boilerplate.presentation.widgets.Toasty;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LandingActivity extends BaseDrawerActivity<ActivityLandingBinding> implements LandingContract.View, NavigationView.OnNavigationItemSelectedListener {

    private long backPressed;

    private LandingContract.Presenter presenter;

    private static String visibleFragment;

    protected int getContentViewResource() {
        return R.layout.activity_landing;
    }

    @Override
    protected int getAnalyticsNameResource() {
        return R.string.analytics_landing;
    }

    @Override
    protected boolean monitorOfflineMode() {
        return false;
    }

    @Nullable
    @Override
    public ActivityLandingBinding createBinding(@NonNull Activity activity, int layoutId) {
        return DataBindingUtil.setContentView(activity, layoutId);
    }

    @Override
    public ActivityLandingBinding getBinding() {
        return super.getBinding();
    }

    @Override
    public com.lupinemoon.boilerplate.presentation.ui.features.landing.LandingContract.Presenter getPresenter() {
        return presenter;
    }

    public static String getVisibleFragment() {
        return visibleFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fades in from the bottom
        AnimationUtils.animateTransitionStartActivity(this.getActivity(), R.anim.slide_up_fade_in);

        // Create the presenter
        presenter = new com.lupinemoon.boilerplate.presentation.ui.features.landing.LandingPresenter(this, getIntent().getExtras());

        // Subscribe to the RxBus
        createRxBusSubscriptions();

        // Navigate to the home fragment
        selectMenuItem(HomeFragment.TAG, true);

        updateDevMenuVisibility();

        // Check play services and show an error dialog if required
        if (AndroidUtils.checkPlayServices(this, true)) {
            Timber.d("Play Services Is Available");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (fragmentManager.getBackStackEntryCount() <= 1) {
            applyExit();
        } else {
            fragmentManager.popBackStackImmediate();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                Timber.d(
                        "fragment.getBackStackEntryCount(): %s",
                        fragmentManager.getBackStackEntryCount());
                // Set the toolbar title
                BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(
                        fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName());
                if (fragment != null && navigationView != null) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(fragment.getToolbarTitle());
                    }

                    Timber.d("fragment.getFragmentTag(): %s", fragment.getFragmentTag());

                    // Check the item in the navigation drawer
                    selectMenuItem(fragment.getFragmentTag(), false);
                }
            }
        }
    }

    public void selectMenuItem(String fragmentTag, boolean performNavigation, Object... objects) {
        // Check the item in the navigation drawer
        int menuItemId = 0;
        if (fragmentTag.equals(HomeFragment.TAG)) {
            menuItemId = R.id.home_navigation_menu_item;
        }

        if (menuItemId > 0) {
            if (performNavigation) {
                performNavigationToSelectedItem(
                        navigationView.getMenu().findItem(menuItemId),
                        true,
                        objects);
            }
            navigationView.setCheckedItem(menuItemId);
        }
    }

    private void applyExit() {
        if (backPressed + Constants.BACK_PRESSED_EXIT_TIME_INTERVAL > System.currentTimeMillis()) {
            ActivityUtils.minimizeApp(this);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
            showToastMsg(getString(R.string.back_press_exit), Toasty.ToastType.INFO);
        }
        backPressed = System.currentTimeMillis();
    }

    private void updateDevMenuVisibility() {
        if (navigationView != null) {
            Menu menu = navigationView.getMenu();
            if (menu != null) {
                menu.setGroupVisible(R.id.group_dev, BuildConfig.DEBUG);
            }
        }
    }

    @Override
    public void setupDrawerContent(NavigationView navigationView) {
        super.setupDrawerContent(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        performNavigationToSelectedItem(menuItem, false);
        return true;
    }

    private void performNavigationToSelectedItem(
            @NonNull MenuItem menuItem,
            Boolean forceNavigation,
            Object... objects) {
        if (!menuItem.isChecked() || forceNavigation) {
            BaseFragment fragment = null;

            Bundle bundle = new Bundle();
            // TODO: Add bundle extras to be used in fragments

            switch (menuItem.getItemId()) {
                case R.id.home_navigation_menu_item:
                    fragment = HomeFragment.instance((AppCompatActivity) getActivity(), bundle);
                    break;

                case R.id.logout_navigation_menu_item:
                    logout();
                    break;

                case R.id.dev_settings_navigation_menu_item:
                    if (BuildConfig.DEBUG || BuildConfig.STAGING) {
                        Intent intent = new Intent(getActivity(), DevActivity.class);
                        startActivity(intent);
                    }
                    break;

                default:
                    break;
            }

            // Navigate to the fragment
            navigateToFragment(fragment);
        }

        // Added 50 millisecond delay to the closing navigation, this,
        // with it's minimal complexity achieves quite a balanced result.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDrawerLayout().closeDrawers();
            }
        }, 50);

    }

    private void navigateToFragment(BaseFragment fragment) {
        if (fragment != null) {
            // Set the toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(fragment.getToolbarTitle());
            }

            visibleFragment = fragment.getClass().getSimpleName();

            Timber.d("Visible Fragment: %s", getVisibleFragment());

            // Navigate to the fragment
            ActivityUtils.addFragmentToActivityAnim(
                    getSupportFragmentManager(),
                    fragment,
                    fragment.getFragmentTag(),
                    R.id.activity_content_container,
                    true,
                    false, R.anim.fade_in, 0, 0, 0);
        }
    }

    private void logout() {
        MainApplication.setLoggedIn(false);
        Intent intent = new Intent(this, LoginActivity.class);
        supportFinishAfterTransition();
        AnimationUtils.animateTransitionFinishActivity(
                this.getActivity(),
                R.anim.slide_down_fade_out);
        startActivity(intent);
    }

    private void createRxBusSubscriptions() {

        rxBusEvents.add(
                RxBus.getDefault().observeEvents(LogoutEvent.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<LogoutEvent>() {
                            @Override
                            public void accept(LogoutEvent logoutEvent) {
                                Timber.w("Perform Logout");
                                logout();
                            }
                        }));

        rxBusEvents.add(
                RxBus.getDefault().observeEvents(NoNetworkEvent.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<NoNetworkEvent>() {
                            @Override
                            public void accept(NoNetworkEvent noNetworkEvent) {
                                Timber.w("No Network Event");
                            }
                        }));

        rxBusEvents.add(
                RxBus.getDefault().observeEvents(NetworkRestoredEvent.class)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<NetworkRestoredEvent>() {
                    @Override
                    public void accept(NetworkRestoredEvent networkRestoredEvent) throws Exception {
                        Timber.w("%s: Network restored event. Triggering queue processing.", this.getClass().getSimpleName());
                        getPresenter().retryQueuedRequests();
                    }
                }));

    }
}

