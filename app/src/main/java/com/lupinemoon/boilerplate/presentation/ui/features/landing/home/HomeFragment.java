package com.lupinemoon.boilerplate.presentation.ui.features.landing.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.databinding.FragmentHomeBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseVMPFragment;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.widgets.OnBackPressedListener;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

public class HomeFragment extends BaseVMPFragment<HomeContract.ViewModel, HomeContract.Presenter, FragmentHomeBinding> implements HomeContract.View {

    public static final String TAG = HomeFragment.class.getSimpleName();

    public static HomeFragment instance(AppCompatActivity activity, Bundle args) {
        HomeFragment homeFragment = (HomeFragment) activity.getSupportFragmentManager().findFragmentByTag(
                HomeFragment.TAG);

        if (homeFragment == null) {
            // Create the fragment
            homeFragment = new HomeFragment();
            homeFragment.setArguments(args);
        }

        return homeFragment;
    }

    @Override
    protected int getContentViewResource() {
        return R.layout.fragment_home;
    }

    @Override
    public int getToolbarTitle() {
        return R.string.toolbar_title_home;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public boolean monitorOfflineMode() {
        return true;
    }

    @Override
    public OnBackPressedListener getOnBackPressedListener() {
        // Not required, implemented in the landing activity
        return null;
    }

    @Nullable
    @Override
    public HomeViewModel createViewModel(@Nullable BaseViewModel.State savedViewModelState) {
        // Create the view model
        return new HomeViewModel(this, savedViewModelState);
    }

    @Nullable
    @Override
    public HomePresenter createPresenter(@NonNull Bundle args) {
        // Create the presenter
        return new HomePresenter(this, args);
    }

    @Nullable
    @Override
    public FragmentHomeBinding createBinding(
            @NonNull LayoutInflater inflater,
            int layoutId,
            @Nullable ViewGroup parent,
            boolean attachToParent) {
        return DataBindingUtil.inflate(inflater, getContentViewResource(), parent, false);
    }

    @Override
    public FragmentHomeBinding getBinding() {
        return super.getBinding();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return getBinding().getRoot();
    }

    @Override
    public void showLoading() {
        // We hide the recycler view, because initially we have no data
        // to display. All calls except fetchRecItems() should be async
        // and performed in the background and therefore should not show
        // the popup loader.
        toggleNoNetworkView(true);
        toggleEmptyView(false);
        togglePopupLoader(true);
    }

    @Override
    public void hideLoading() {
        togglePopupLoader(false);
        toggleEmptyView(true);
    }

    @Override
    public void showNetworkErrorLayout(final GenericCallback genericCallback) {
        getBinding().noNetworkLayout.noNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genericCallback.execute();
            }
        });
        toggleNoNetworkView(false);
    }

    private void toggleEmptyView(boolean hide) {
        getBinding().emptyLayout.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    private void toggleNoNetworkView(boolean hide) {
        View noNetworkView = getBinding().noNetworkLayout.getRoot();
        noNetworkView.setVisibility(hide ? View.GONE : View.VISIBLE);
    }
}