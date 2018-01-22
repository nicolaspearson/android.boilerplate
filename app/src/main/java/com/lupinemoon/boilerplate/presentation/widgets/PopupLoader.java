package com.lupinemoon.boilerplate.presentation.widgets;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import timber.log.Timber;
import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.R;

public class PopupLoader {

    private static PopupWindow mPopupWindow;

    private static Handler mDismissHandler;

    private static synchronized PopupWindow getInstance(Context context) {
        if (mPopupWindow == null) {
            Timber.d("New Popup Window");
            final int dimen = context.getResources().getDimensionPixelSize(R.dimen.loader_dimen);
            mPopupWindow = new PopupWindow(dimen, dimen);
        }
        return mPopupWindow;
    }

    private static synchronized Handler getHandler() {
        if (mDismissHandler == null) {
            Timber.d("New Popup Dismiss Handler");
            mDismissHandler = new Handler();
        }
        Timber.d("Removed Dismiss Handler Callbacks.");
        mDismissHandler.removeCallbacksAndMessages(null);
        return mDismissHandler;
    }

    private static boolean isAttachedToWindow(View v) {
        return ((v != null) && (v.getWindowToken() != null));
    }

    public static PopupWindow showPopupLoader(final Context context, final View parentView) {
        final PopupWindow popupWindow = PopupLoader.getInstance(context);

        Timber.d("showPopupLoader");

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.progress_bar, null);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        popupWindow.setContentView(rootView);
        popupWindow.setAnimationStyle(android.R.anim.fade_in);

        final int dimen = context.getResources().getDimensionPixelSize(R.dimen.loader_dimen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAttachedToWindow(parentView)) {
                    return;
                }
                int viewHeight = parentView.getHeight();
                popupWindow.showAtLocation(
                        parentView,
                        Gravity.CENTER,
                        0,
                        (-1 * (viewHeight / 4 - dimen / 4)));
                progressBar.setVisibility(View.VISIBLE);
            }
        }, 50);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissPopupLoader(context);
            }
        }, BuildConfig.HTTP_TIMEOUT * 1000 * 2);

        return popupWindow;
    }

    public static void dismissPopupLoader(Context context) {
        try {
            Timber.d("dismissPopupLoader(): %s", PopupLoader.getInstance(context));
            PopupLoader.getInstance(context).dismiss();
        } catch (Exception e) {
            Timber.e(e, "dismissPopupLoader() failed: %s", e.toString());
        }
    }
}