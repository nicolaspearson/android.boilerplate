package com.lupinemoon.boilerplate.presentation.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class FixedWidthImageView extends AspectRatioImageView {

    public FixedWidthImageView(Context context) {
        super(context);
    }

    public FixedWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public AdjustAspect getAspectToAdjust() {
        return AdjustAspect.HEIGHT;
    }
}
