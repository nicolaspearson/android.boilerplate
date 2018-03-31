package com.lupinemoon.boilerplate.presentation.widgets;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

import timber.log.Timber;

public class PopupLoaderDeterminate {

    private static PopupWindow mPopupWindow = getInstance();

    private static synchronized PopupWindow getInstance() {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        return mPopupWindow;
    }

    private static boolean isAttachedToWindow(View v) {
        return ((v != null) && (v.getWindowToken() != null));
    }

    public static PopupWindow showPopupLoader(
            final Context context,
            final View parentView,
            final GenericCallback genericCallback) {


        final PopupWindow popupWindow = PopupLoaderDeterminate.getInstance();

        Timber.d("showPopupLoader");

        LayoutInflater inflater = LayoutInflater.from(context);

        View rootView = inflater.inflate(R.layout.progress_bar_determinate, null);

        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        progressBar.setProgress(0);

        View cancelButton = rootView.findViewById(R.id.progress_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genericCallback != null) {
                    progressBar.setProgress(-1);
                    genericCallback.execute();
                }
            }
        });

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
                progressBar.setProgress(0);
                Toasty.info(context, context.getString(R.string.saving)).show();
            }
        }, 50);

        return popupWindow;
    }

    public static void dismissPopupLoader(Context context) {
        try {

            View rootView = PopupLoaderDeterminate.getInstance().getContentView();

            if (rootView != null) {
                final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
                int currentProgress = progressBar != null ? progressBar.getProgress() : 0;
                if (currentProgress > 0) {
                    if (currentProgress != 100) {
                        // Should be reported to crashlytics via retrofit.
                        Toasty.warning(
                                context,
                                context.getString(R.string.saving_did_not_complete)).show();
                    } else {
                        Toasty.success(
                                context,
                                context.getString(R.string.saving_success)).show();
                    }
                } else {
                    Toasty.warning(
                            context,
                            context.getString(R.string.saving_cancelled)).show();
                }

                View cancelButton = rootView.findViewById(R.id.progress_cancel);

                if (cancelButton != null) {
                    if (cancelButton.hasOnClickListeners()) {
                        cancelButton.setOnClickListener(null);
                    }
                }

            }
            PopupLoaderDeterminate.getInstance().dismiss();

        } catch (Exception e) {
            Timber.e(e, "dismissPopupLoader() failed: %s", e.toString());
        }
    }
}
