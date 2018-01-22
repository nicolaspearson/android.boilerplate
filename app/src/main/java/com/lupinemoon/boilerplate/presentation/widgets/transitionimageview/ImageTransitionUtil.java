package com.lupinemoon.boilerplate.presentation.widgets.transitionimageview;

import android.support.v4.app.SharedElementCallback;
import android.view.View;

import java.util.List;

public class ImageTransitionUtil {

    public static SharedElementCallback DEFAULT_SHARED_ELEMENT_CALLBACK = new SharedElementCallback() {
        @Override
        public void onSharedElementStart(
                List<String> sharedElementNames,
                List<View> sharedElements, List<View> sharedElementSnapshots) {
            super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);

            for (View sharedElement : sharedElements) {
                if (sharedElement instanceof TransitionImageView) {
                    ((TransitionImageView) sharedElement)
                            .setRoundingProgress(TransitionImageView.RoundingProgress.MAX.progressValue());
                }
            }
        }

        @Override
        public void onSharedElementEnd(
                List<String> sharedElementNames,
                List<View> sharedElements, List<View> sharedElementSnapshots) {
            super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

            for (View sharedElement : sharedElements) {
                if (sharedElement instanceof TransitionImageView) {
                    TransitionImageView tiv = (TransitionImageView) sharedElement;
                    if (tiv.getRoundingProgress() == TransitionImageView.RoundingProgress.MAX.progressValue()) {
                        tiv.setRoundingProgress(TransitionImageView.RoundingProgress.MIN.progressValue());
                    }
                }
            }
        }
    };
}