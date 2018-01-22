package com.lupinemoon.boilerplate.presentation.ui.base;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.lupinemoon.boilerplate.presentation.utils.DialogUtils;
import com.lupinemoon.boilerplate.presentation.widgets.Toasty;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

public interface IBaseView {
    Activity getActivity();
    boolean isAttached();
    boolean notAuthorized(Throwable throwable);
    void showLoading();
    void hideLoading();
    boolean isLoading();
    void showToastMsg(String msg, Toasty.ToastType toastType);
    void showSnackbarMsg(String msg, int duration);
    void showCustomRetryErrorDialog(Throwable throwable, final GenericCallback retryCallback, String title);
    void showCustomNetworkErrorDialog(final GenericCallback retryCallback);
    void showCustomNetworkErrorDialog(final GenericCallback retryCallback, final GenericCallback cancelCallback);
    AlertDialog showCustomAlertDialogSimple(int title, int message, int posButton, int negButton, View.OnClickListener posButtonClick, View.OnClickListener negButtonClick, DialogUtils.AlertType alertType);
    AlertDialog showCustomAlertDialogSimple(String title, String message, int posButton, int negButton, View.OnClickListener posButtonClick, View.OnClickListener negButtonClick, DialogUtils.AlertType alertType);
    void animateOnBackPressed(GenericCallback onBackPressedCallback);
}
