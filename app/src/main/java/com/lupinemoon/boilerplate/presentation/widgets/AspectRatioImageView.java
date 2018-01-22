package com.lupinemoon.boilerplate.presentation.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.widgets.utils.WidgetUtils;

public abstract class AspectRatioImageView extends AppCompatImageView {

    private ImageSize imageSize = ImageSize.STANDARD;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AspectRatioImageView,
                0, 0);

        try {
            int size = typedArray.getInteger(R.styleable.AspectRatioImageView_imageSize, -1);
            if (size > -1) {
                this.imageSize = ImageSize.values()[size];
            }

        } finally {
            typedArray.recycle();
        }
    }

    public float getAspectRatio() {
        return this.imageSize.getWidth() > this.imageSize.getHeight()
                ? (float) this.imageSize.getWidth() / (float) this.imageSize.getHeight()
                : (float) this.imageSize.getHeight() / (float) this.imageSize.getWidth();
    }

    public abstract AdjustAspect getAspectToAdjust();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (getAspectToAdjust().equals(AdjustAspect.WIDTH)) {
            width = (int) (height / getAspectRatio());
        } else {
            height = (int) (width * getAspectRatio());
        }

        setMeasuredDimension(width, height);
    }

    enum AdjustAspect {
        WIDTH,
        HEIGHT
    }

    private enum ImageSize {

        THUMB(WidgetUtils.getImageThumbnailSize()[0], WidgetUtils.getImageThumbnailSize()[1]),
        STANDARD(WidgetUtils.getImageStandardSize()[0], WidgetUtils.getImageStandardSize()[1]),
        LARGE(WidgetUtils.getImageLargeSize()[0], WidgetUtils.getImageLargeSize()[1]);

        private int width;
        private int height;

        ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}