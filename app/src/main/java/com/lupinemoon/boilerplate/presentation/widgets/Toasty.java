package com.lupinemoon.boilerplate.presentation.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.widgets.utils.WidgetUtils;

@SuppressLint("InflateParams")
public class Toasty {

    public enum ToastType {
        INFO, SUCCESS, ERROR, WARNING
    }

    private static final String TOAST_TYPEFACE = "sans-serif-condensed";

    private Toasty() {
    }

    public static @CheckResult Toast normal(@NonNull Context context, @NonNull String message) {
        return normal(context, message, Toast.LENGTH_SHORT, null, false);
    }

    public static @CheckResult Toast normal(@NonNull Context context, @NonNull String message, Drawable icon) {
        return normal(context, message, Toast.LENGTH_SHORT, icon, true);
    }

    public static @CheckResult Toast normal(@NonNull Context context, @NonNull String message, int duration) {
        return normal(context, message, duration, null, false);
    }

    public static @CheckResult Toast normal(@NonNull Context context, @NonNull String message, int duration,
                                            Drawable icon) {
        return normal(context, message, duration, icon, true);
    }

    public static @CheckResult Toast normal(@NonNull Context context, @NonNull String message, int duration,
                                            Drawable icon, boolean withIcon) {
        return custom(context, message, icon, ContextCompat.getColor(context, R.color.toasty_default_text_color), duration, withIcon);
    }

    public static @CheckResult Toast warning(@NonNull Context context, @NonNull String message) {
        return warning(context, message, Toast.LENGTH_SHORT, true);
    }

    public static @CheckResult Toast warning(@NonNull Context context, @NonNull String message, int duration) {
        return warning(context, message, duration, true);
    }

    public static @CheckResult Toast warning(@NonNull Context context, @NonNull String message, int duration, boolean withIcon) {
        return custom(context, message, WidgetUtils.getDrawable(context, R.drawable.ic_error_outline_white),
                      ContextCompat.getColor(context, R.color.toasty_default_text_color), ContextCompat.getColor(context, R.color.toasty_warning_color), duration, withIcon, true);
    }

    public static @CheckResult Toast info(@NonNull Context context, @NonNull String message) {
        return info(context, message, Toast.LENGTH_SHORT, true);
    }

    public static @CheckResult Toast info(@NonNull Context context, @NonNull String message, int duration) {
        return info(context, message, duration, true);
    }

    public static @CheckResult Toast info(@NonNull Context context, @NonNull String message, int duration, boolean withIcon) {
        return custom(context, message, WidgetUtils.getDrawable(context, R.drawable.ic_info_outline_white),
                      ContextCompat.getColor(context, R.color.toasty_default_text_color), ContextCompat.getColor(context, R.color.toasty_info_color), duration, withIcon, true);
    }

    public static @CheckResult Toast success(@NonNull Context context, @NonNull String message) {
        return success(context, message, Toast.LENGTH_SHORT, true);
    }

    public static @CheckResult Toast success(@NonNull Context context, @NonNull String message, int duration) {
        return success(context, message, duration, true);
    }

    public static @CheckResult Toast success(@NonNull Context context, @NonNull String message, int duration, boolean withIcon) {
        return custom(context, message, WidgetUtils.getDrawable(context, R.drawable.ic_check_white),
                      ContextCompat.getColor(context, R.color.toasty_default_text_color), ContextCompat.getColor(context, R.color.toasty_success_color), duration, withIcon, true);
    }

    public static @CheckResult Toast error(@NonNull Context context, @NonNull String message) {
        return error(context, message, Toast.LENGTH_SHORT, true);
    }

    public static @CheckResult Toast error(@NonNull Context context, @NonNull String message, int duration) {
        return error(context, message, duration, true);
    }

    public static @CheckResult Toast error(@NonNull Context context, @NonNull String message, int duration, boolean withIcon) {
        return custom(context, message, WidgetUtils.getDrawable(context, R.drawable.ic_clear_white),
                      ContextCompat.getColor(context, R.color.toasty_default_text_color), ContextCompat.getColor(context, R.color.toasty_error_color), duration, withIcon, true);
    }

    public static @CheckResult Toast custom(@NonNull Context context, @NonNull String message, Drawable icon,
                                            @ColorInt int textColor, int duration, boolean withIcon) {
        return custom(context, message, icon, textColor, -1, duration, withIcon, false);
    }

    public static @CheckResult Toast custom(@NonNull Context context, @NonNull String message, @DrawableRes int iconRes,
                                            @ColorInt int textColor, @ColorInt int tintColor, int duration,
                                            boolean withIcon, boolean shouldTint) {
        return custom(context, message, WidgetUtils.getDrawable(context, iconRes), textColor,
                      tintColor, duration, withIcon, shouldTint);
    }

    public static @CheckResult Toast custom(@NonNull Context context, @NonNull String message, Drawable icon,
                                            @ColorInt int textColor, @ColorInt int tintColor, int duration,
                                            boolean withIcon, boolean shouldTint) {
        final Toast currentToast = new Toast(context);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        final ImageView toastIcon = (ImageView) toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = (TextView) toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint)
            drawableFrame = WidgetUtils.tint9PatchDrawableFrame(context, tintColor);
        else
            drawableFrame = WidgetUtils.getDrawable(context, R.drawable.toast_frame);
        WidgetUtils.setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            WidgetUtils.setBackground(toastIcon, icon);
        } else
            toastIcon.setVisibility(View.GONE);

        toastTextView.setTextColor(textColor);
        toastTextView.setText(message);
        toastTextView.setTypeface(Typeface.create(TOAST_TYPEFACE, Typeface.NORMAL));

        currentToast.setView(toastLayout);
        currentToast.setDuration(duration);
        return currentToast;
    }
}

