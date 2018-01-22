package com.lupinemoon.boilerplate.presentation.widgets.transitionimageview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

class ImageTransitionHelper {

    private static final String PROPERTY_NAME_ROUNDING_PROGRESS = "itl:changeBounds:roundingProgress";

    static String[] getTransitionProperties(String[] parentTransitionProperties) {
        if (parentTransitionProperties == null || parentTransitionProperties.length == 0) {
            return new String[]{PROPERTY_NAME_ROUNDING_PROGRESS};
        }

        String[] transitionProperties = Arrays.copyOf(
                parentTransitionProperties,
                parentTransitionProperties.length + 1);
        transitionProperties[transitionProperties.length - 1] = PROPERTY_NAME_ROUNDING_PROGRESS;

        return transitionProperties;
    }

    static void captureValues(View view, Map<String, Object> values) {
        if (view instanceof TransitionImageView) {
            values.put(
                    PROPERTY_NAME_ROUNDING_PROGRESS,
                    ((TransitionImageView) view).getRoundingProgress());
        }
    }

    static Animator createAnimator(
            Animator parentAnimator, ViewGroup sceneRoot,
            View endValuesView,
            Map<String, Object> startValues,
            Map<String, Object> endValues) {
        if (parentAnimator == null) {
            return null;
        }

        float startRoundingProgress = (float) startValues.get(PROPERTY_NAME_ROUNDING_PROGRESS);
        float endRoundingProgress = (float) endValues.get(PROPERTY_NAME_ROUNDING_PROGRESS);

        final ObjectAnimator roundingProgressAnimator = ObjectAnimator.ofFloat(endValuesView,
                                                                               TransitionImageView.ROUNDING_PROGRESS_PROPERTY,
                                                                               startRoundingProgress,
                                                                               endRoundingProgress);

        if (parentAnimator instanceof AnimatorSet) {
            ArrayList<Animator> parentAnimators = ((AnimatorSet) parentAnimator).getChildAnimations();

            parentAnimators.add(roundingProgressAnimator);

            parentAnimator = new AnimatorSet();
            ((AnimatorSet) parentAnimator).playTogether(parentAnimators);
        } else {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(parentAnimator, roundingProgressAnimator);
            return set;
        }

        return parentAnimator;
    }
}