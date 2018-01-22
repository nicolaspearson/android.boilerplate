package com.lupinemoon.boilerplate.presentation.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class FixedHeightImageView extends AspectRatioImageView {

    public FixedHeightImageView(Context context) {
        super(context);
    }

    public FixedHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public AdjustAspect getAspectToAdjust() {
        return AdjustAspect.WIDTH;
    }
}
