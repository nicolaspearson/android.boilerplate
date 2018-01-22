package com.lupinemoon.boilerplate.presentation.widgets.transitionimageview;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImageTransition extends ChangeBounds {

    public ImageTransition() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return ImageTransitionHelper.getTransitionProperties(super.getTransitionProperties());
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        ImageTransitionHelper.captureValues(transitionValues.view, transitionValues.values);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        ImageTransitionHelper.captureValues(transitionValues.view, transitionValues.values);
    }

    @Override
    public Animator createAnimator(
            ViewGroup sceneRoot,
            TransitionValues startValues,
            TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        return ImageTransitionHelper.createAnimator(super.createAnimator(
                sceneRoot,
                startValues,
                endValues),
                                                    sceneRoot,
                                                    endValues.view,
                                                    startValues.values,
                                                    endValues.values);
    }
}