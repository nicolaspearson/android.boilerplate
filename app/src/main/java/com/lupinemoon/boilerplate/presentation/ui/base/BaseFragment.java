package com.lupinemoon.boilerplate.presentation.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.ViewDataBinding;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.services.rxbus.RxBus;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.NetworkRestoredEvent;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.QueueProcessingComplete;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.QueueProcessingStarted;
import com.lupinemoon.boilerplate.presentation.ui.features.offline.OfflineActivity;
import com.lupinemoon.boilerplate.presentation.utils.DialogUtils;
import com.lupinemoon.boilerplate.presentation.utils.NetworkUtils;
import com.lupinemoon.boilerplate.presentation.widgets.OnBackPressedListener;
import com.lupinemoon.boilerplate.presentation.widgets.Toasty;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public abstract class BaseFragment<B extends ViewDataBinding> extends DialogFragment implements IBaseView {

    View rootView;

    private B binding;

    private BroadcastReceiver networkStateReceiver;

    public abstract String getFragmentTag();

    public abstract OnBackPressedListener getOnBackPressedListener();

    protected abstract int getContentViewResource();

    public abstract int getToolbarTitle();

    public abstract boolean monitorOfflineMode();

    protected CompositeDisposable rxBusEvents;


    @Nullable
    public abstract B createBinding(
            @NonNull LayoutInflater inflater,
            int layoutId,
            @Nullable ViewGroup parent,
            boolean attachToParent);

    public B getBinding() {
        return binding;
    }

    public void setBinding(B binding) {
        this.binding = binding;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rxBusEvents = new CompositeDisposable();

        createRxBusSubscriptions();

        Timber.d("onCreate: %s", getFragmentTag());
        if (getOnBackPressedListener() != null) {
            ((BaseActivity) getActivity()).setOnBackPressedListener(getOnBackPressedListener());
        }

        setShowsDialog(false);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Timber.d("onCreateView: %s", getFragmentTag());

        setBinding(createBinding(inflater, getContentViewResource(), container, false));

        View rootView = getBinding().getRoot();
        setRootView(rootView);

        configureOfflineLayout();

        if (monitorOfflineMode() && networkStateReceiver != null) {
            Timber.d("Register Network State Receiver");
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(networkStateReceiver, filter);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated: %s", getFragmentTag());
    }

    @Override
    public void onStop() {
        togglePopupLoader(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        togglePopupLoader(false);
        if (networkStateReceiver != null) {
            Timber.d("Unregister Network State Receiver");
            getActivity().unregisterReceiver(networkStateReceiver);
        }
        if (rxBusEvents != null) {
            rxBusEvents.clear();
            rxBusEvents = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        togglePopupLoader(false);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d("onAttach: %s", getFragmentTag());
    }

    @Override
    public boolean isAttached() {
        return getActivity() != null && isAdded();
    }

    @Override
    public void animateOnBackPressed(GenericCallback onBackPressedCallback) {
        ((BaseActivity) getActivity()).animateOnBackPressed(onBackPressedCallback);
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public void showLoading() {
        togglePopupLoader(true);
    }

    @Override
    public void hideLoading() {
        togglePopupLoader(false);
    }

    @Override
    public boolean isLoading() {
        return ((BaseActivity) getActivity()).isLoading();
    }

    public void togglePopupLoader(final boolean show) {
        ((BaseActivity) getActivity()).togglePopupLoader(show);
    }

    @Override
    public void showToastMsg(String msg, Toasty.ToastType toastType) {
        ((BaseActivity) getActivity()).showToastMsg(msg, toastType);
    }

    @Override
    public void showSnackbarMsg(String msg, int duration) {
        ((BaseActivity) getActivity()).showSnackbarMsg(msg, duration);
    }

    @Override
    public void showCustomRetryErrorDialog(
            Throwable throwable,
            final GenericCallback retryCallback,
            String title) {
        ((BaseActivity) getActivity()).showCustomRetryErrorDialog(throwable, retryCallback, title);
    }

    @Override
    public void showCustomNetworkErrorDialog(GenericCallback retryCallback) {
        ((BaseActivity) getActivity()).showCustomNetworkErrorDialog(retryCallback);
    }

    @Override
    public void showCustomNetworkErrorDialog(
            GenericCallback retryCallback,
            GenericCallback cancelCallback) {
        ((BaseActivity) getActivity()).showCustomNetworkErrorDialog(retryCallback, cancelCallback);
    }

    @Override
    public AlertDialog showCustomAlertDialogSimple(
            int title,
            int message,
            int posButton,
            int negButton,
            View.OnClickListener posButtonClick,
            View.OnClickListener negButtonClick,
            DialogUtils.AlertType alertType) {
        return ((BaseActivity) getActivity()).showCustomAlertDialogSimple(
                title,
                message,
                posButton,
                negButton,
                posButtonClick,
                negButtonClick,
                alertType);
    }

    @Override
    public AlertDialog showCustomAlertDialogSimple(
            String title,
            String message,
            int posButton,
            int negButton,
            View.OnClickListener posButtonClick,
            View.OnClickListener negButtonClick,
            DialogUtils.AlertType alertType) {
        return ((BaseActivity) getActivity()).showCustomAlertDialogSimple(
                title,
                message,
                posButton,
                negButton,
                posButtonClick,
                negButtonClick,
                alertType);
    }

    @Override
    public boolean notAuthorized(Throwable throwable) {
        return ((BaseActivity) getActivity()).notAuthorized(throwable);
    }

    private void configureOfflineLayout() {
        if (monitorOfflineMode()) {
            networkStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Timber.d("onReceive: Network State Receiver");
                    evaluateConnectionState(getRootView());
                }
            };
        }
    }

    private void createRxBusSubscriptions() {
        rxBusEvents.add(RxBus.getDefault().observeEvents(QueueProcessingComplete.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QueueProcessingComplete>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull QueueProcessingComplete queueProcessingComplete) throws Exception {
                        ViewGroup offlineLayout = (ViewGroup) getRootView().findViewById(R.id.offline_banner_layout);
                        final Context context = getActivity();
                        if (offlineLayout != null && NetworkUtils.hasActiveNetworkConnection(context)) {
                            if (offlineLayout.findViewById(R.id.offline_banner_offline_container).getVisibility() != View.VISIBLE) {
                                resetOfflineBanner(offlineLayout);
                            }
                        }
                    }
                }));

        // Queue started, there are > 0 items. Show syncing banner.
        rxBusEvents.add(
                RxBus.getDefault().observeEvents(QueueProcessingStarted.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<QueueProcessingStarted>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull QueueProcessingStarted queueProcessingStarted) throws Exception {
                                if (NetworkUtils.hasActiveNetworkConnection(getActivity())) {
                                    final View offlineLayout = rootView.findViewById(R.id.offline_banner_layout);
                                    if (offlineLayout != null && getActivity() != null && isAttached()) {
                                        offlineLayout.findViewById(R.id.offline_banner_offline_container).setVisibility(View.GONE);
                                        offlineLayout.findViewById(R.id.offline_banner_connected_container).setVisibility(View.VISIBLE);
                                        if (offlineLayout.getVisibility() == View.GONE) {
                                            offlineLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        }));
    }

    private void resetOfflineBanner(ViewGroup viewGroup){
        viewGroup.setVisibility(View.GONE);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void evaluateConnectionState(View rootView) {
        try {
            final View offlineLayout = rootView.findViewById(R.id.offline_banner_layout);
            if (monitorOfflineMode() && offlineLayout != null) {
                Timber.d("offlineLayout: %s", offlineLayout);

                // Configure the offline button
                View offlineButton = offlineLayout.findViewById(R.id.offline_more_button);
                if (offlineButton != null && !offlineButton.hasOnClickListeners()) {
                    Timber.d("Set offlineButton Click Listener");
                    offlineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent offlineIntent = new Intent(
                                    getActivity(),
                                    OfflineActivity.class);
                            getActivity().startActivity(offlineIntent);
                        }
                    });
                }

                if (NetworkUtils.hasActiveNetworkConnection(rootView.getContext())) {
                    Timber.d("evaluateConnectionState: Connected");
                    RxBus.getDefault().post(new NetworkRestoredEvent());
                    if (getActivity() != null && isAttached()) {
                        Timber.d("Offline Banner: Hide");
                        if (offlineLayout.findViewById(R.id.offline_banner_offline_container).getVisibility() == View.VISIBLE) {
                            offlineLayout.findViewById(R.id.offline_banner_offline_container).setVisibility(View.GONE);
                        }
                    }
                } else {
                    Timber.d("evaluateConnectionState: Disconnected");
                    if (getActivity() != null && isAttached()) {
                        Timber.d("Offline Banner: Show");
                        if (offlineLayout.findViewById(R.id.offline_banner_connected_container).getVisibility() == View.VISIBLE) {
                            offlineLayout.findViewById(R.id.offline_banner_connected_container).setVisibility(View.GONE);
                        }
                        offlineLayout.setVisibility(View.VISIBLE);
                        offlineLayout.findViewById(R.id.offline_banner_offline_container).setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, "evaluateConnectionState Error");
        }
    }
}
