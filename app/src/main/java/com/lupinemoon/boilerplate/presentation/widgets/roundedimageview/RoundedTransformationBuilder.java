package com.lupinemoon.boilerplate.presentation.widgets.roundedimageview;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public final class RoundedTransformationBuilder {

    //private final Resources mResources;
    private final DisplayMetrics mDisplayMetrics;

    private float[] mCornerRadii = new float[]{0, 0, 0, 0};

    private boolean mOval = false;
    private float mBorderWidth = 0;
    private ColorStateList mBorderColor =
            ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;

    public RoundedTransformationBuilder() {
        mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    }

    public RoundedTransformationBuilder scaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
        return this;
    }

    /**
     * Set corner radius for all corners in px.
     *
     * @param radius the radius in px
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder cornerRadius(float radius) {
        mCornerRadii[Corner.TOP_LEFT] = radius;
        mCornerRadii[Corner.TOP_RIGHT] = radius;
        mCornerRadii[Corner.BOTTOM_RIGHT] = radius;
        mCornerRadii[Corner.BOTTOM_LEFT] = radius;
        return this;
    }

    /**
     * Set corner radius for a specific corner in px.
     *
     * @param corner the corner to set.
     * @param radius the radius in px.
     * @return the builder for chaning.
     */
    public RoundedTransformationBuilder cornerRadius(int corner, float radius) {
        mCornerRadii[corner] = radius;
        return this;
    }

    /**
     * Set corner radius for all corners in density independent pixels.
     *
     * @param radius the radius in density independent pixels.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder cornerRadiusDp(float radius) {
        return cornerRadius(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, mDisplayMetrics));
    }

    /**
     * Set corner radius for a specific corner in density independent pixels.
     *
     * @param corner the corner to set
     * @param radius the radius in density independent pixels.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder cornerRadiusDp(int corner, float radius) {
        return cornerRadius(corner,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, mDisplayMetrics));
    }

    /**
     * Set the border width in pixels.
     *
     * @param width border width in pixels.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder borderWidth(float width) {
        mBorderWidth = width;
        return this;
    }

    /**
     * Set the border width in density independent pixels.
     *
     * @param width border width in density independent pixels.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder borderWidthDp(float width) {
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, mDisplayMetrics);
        return this;
    }

    /**
     * Set the border color.
     *
     * @param color the color to set.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder borderColor(int color) {
        mBorderColor = ColorStateList.valueOf(color);
        return this;
    }

    /**
     * Set the border color as a {@link ColorStateList}.
     *
     * @param colors the {@link ColorStateList} to set.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder borderColor(ColorStateList colors) {
        mBorderColor = colors;
        return this;
    }

    /**
     * Sets whether the image should be oval or not.
     *
     * @param oval if the image should be oval.
     * @return the builder for chaining.
     */
    public RoundedTransformationBuilder oval(boolean oval) {
        mOval = oval;
        return this;
    }

    /**
     * Creates a {@link BitmapTransformation} for use with Glide.
     *
     * @return the {@link BitmapTransformation}
     */
    public BitmapTransformation build() {
        return new BitmapTransformation() {
            @Override
            protected Bitmap transform(
                    @NonNull BitmapPool pool,
                    @NonNull Bitmap toTransform,
                    int outWidth,
                    int outHeight) {
                Bitmap transformed = RoundedDrawable.fromBitmap(toTransform)
                        .setScaleType(mScaleType)
                        .setCornerRadius(mCornerRadii[0], mCornerRadii[1], mCornerRadii[2], mCornerRadii[3])
                        .setBorderWidth(mBorderWidth)
                        .setBorderColor(mBorderColor)
                        .setOval(mOval)
                        .toBitmap();
                if (!toTransform.equals(transformed)) {
                    toTransform.recycle();
                }
                return transformed;
            }

            @Override
            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

            }
        };
    }
}
