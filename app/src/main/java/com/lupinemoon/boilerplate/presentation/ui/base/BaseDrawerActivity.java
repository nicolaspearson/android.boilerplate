package com.lupinemoon.boilerplate.presentation.ui.base;

import android.content.res.Configuration;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.utils.AndroidUtils;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public abstract class BaseDrawerActivity<B extends ViewDataBinding> extends BaseActivity<B> implements IBaseView {

    public DrawerLayout drawerLayout;

    public ActionBarDrawerToggle drawerToggle;

    public NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResource());

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
                supportActionBar.setHomeButtonEnabled(true);
            }
        }

        // Set up the navigation drawer.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            drawerLayout.setStatusBarBackground(R.color.color_primary_dark);
            navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
            if (navigationView != null) {
                setupDrawerContent(navigationView);
            }

            drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close) {
                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);
                    overrideMenuFonts(navigationView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    AndroidUtils.hideKeyboard(BaseDrawerActivity.this);
                }
            };
            drawerToggle.setDrawerIndicatorEnabled(true);

            // Set the drawer toggle as the DrawerListener
            drawerLayout.addDrawerListener(drawerToggle);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupDrawerContent(NavigationView navigationView) {
        // Override in child class
        Timber.d("setupDrawerContent");
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    protected void hideDrawerMenu() {
        if (getDrawerLayout() != null) {
            getDrawerLayout().closeDrawer(GravityCompat.START);
        }
    }

    protected boolean closeLeftDrawerIfOpened() {
        if ((getDrawerLayout() != null) && (getDrawerLayout().isDrawerOpen(GravityCompat.START))) {
            hideDrawerMenu();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");
        if (!closeLeftDrawerIfOpened()) {
            if (onBackPressedListener != null) {
                onBackPressedListener.doOnBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void overrideMenuFonts(View view) {
        try {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    overrideMenuFonts(viewGroup.getChildAt(i));
                }
            } else if (view instanceof TextView) {
                CalligraphyUtils.applyFontToTextView(
                        this,
                        (TextView) view,
                        "fonts/Roboto-Medium.ttf");
            }
        } catch (Exception e) {
            Timber.e(e, "Setting custom font failed");
        }
    }
}
