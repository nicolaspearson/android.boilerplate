package com.lupinemoon.boilerplate.presentation.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ScrollView;

import timber.log.Timber;

public class AnimationUtils {

    public static void showViewAnimation(Context context, final View view, int animation) {
        if (!(view.getVisibility() == View.VISIBLE)) {
            Animation trans = android.view.animation.AnimationUtils.loadAnimation(context, animation);
            trans.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Timber.d("Show view animation start, setting view to VISIBLE: %s", view.getClass().getSimpleName());
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Timber.d("Show view animation end: %s", animation.getClass().getSimpleName());
                    view.requestFocus();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(trans);
        } else {
            view.requestFocus();
        }
    }

    public static void dismissViewAnimation(Context context, final View view, int animation) {
        dismissViewAnimation(context, view, animation, View.GONE);
    }

    public static void dismissViewAnimation(Context context, final View view, int animation, final int visibility) {
        if (!(view.getVisibility() == visibility)) {
            Animation trans = android.view.animation.AnimationUtils.loadAnimation(context, animation);
            trans.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Timber.d("Dismiss view animation start: %s", animation.getClass().getSimpleName());
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                        Timber.d("Dismiss view animation end, setting view to GONE: %s", view.getClass().getSimpleName());
                        view.setVisibility(visibility);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(trans);
        }
    }

    public static void animateTransitionStartActivity(Activity activity, int animEnter){
      activity.overridePendingTransition(animEnter, 0);
    }

    public static void animateTransitionFinishActivity(Activity activity, int animExit){
        activity.overridePendingTransition(0, animExit);
    }

    public static void scrollViewScroller(final ScrollView scrollView, Integer delayDuration) {

        int durationVal;
        if (delayDuration == null) {
            durationVal = 0;
        } else {
            durationVal = delayDuration;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Timber.d("Scrolling scroll view...");
                scrollView.smoothScrollTo(scrollView.getHeight(), scrollView.getWidth());
            }
        }, durationVal);


    }

}
