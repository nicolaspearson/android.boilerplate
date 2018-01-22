package com.lupinemoon.boilerplate.presentation.widgets.transitionimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.widgets.utils.WidgetUtils;

public class TransitionImageView extends AppCompatImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;

    private final RectF mDrawableRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;

    // Amount of rounding to apply
    private float mRoundingProgress;

    // Used while drawing to the canvas
    private float mRoundedRadius;

    // Common use cases
    enum RoundingProgress {

        // No rounding
        MIN(0f),

        // Perfect rounding
        MAX(1f);

        RoundingProgress(float progressValue) {
            mProgressValue = progressValue;
        }

        /**
         * Rounding value for this enum.
         *
         * @return the assigned rounding value
         */
        public float progressValue() {
            return mProgressValue;
        }

        // Rounding value for this enum.
        private float mProgressValue;
    }

    // Exposed property that is animated by ObjectAnimator
    public static final Property<View, Float> ROUNDING_PROGRESS_PROPERTY
            = new Property<View, Float>(Float.class, "roundingProgress") {
        @Override
        public Float get(View object) {
            return null;
        }

        @Override
        public void set(View object, Float value) {
            if (object instanceof TransitionImageView) {
                ((TransitionImageView) object).setRoundingProgress(value);
            }
        }
    };

    public TransitionImageView(Context context) {
        super(context);
        mRoundingProgress = RoundingProgress.MAX.progressValue();
        init();
    }

    public TransitionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransitionImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.TransitionImageView,
                defStyle,
                0);

        mRoundingProgress = constrain(
                typedArray.getFloat(
                        R.styleable.TransitionImageView_tiv_rounding,
                        RoundingProgress.MAX.progressValue()),
                RoundingProgress.MIN.progressValue(),
                RoundingProgress.MAX.progressValue());
        typedArray.recycle();

        init();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format(
                    "ScaleType %s not supported.",
                    scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }

        canvas.drawRoundRect(mDrawableRect, mRoundedRadius, mRoundedRadius, mBitmapPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidate();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    private void applyColorFilter() {
        if (mBitmapPaint != null) {
            mBitmapPaint.setColorFilter(mColorFilter);
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(
                        COLOR_DRAWABLE_DIMENSION,
                        COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                    return null;
                }

                bitmap = Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mRoundedRadius = getWidth() / 2f * mRoundingProgress;

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mDrawableRect.set(calculateBounds());

        applyColorFilter();
        updateShaderMatrix();
        invalidate();
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLengthHor = (int) (availableWidth
                + mRoundingProgress * (Math.min(availableWidth, availableHeight) - availableWidth));
        int sideLengthVer = (int) (availableHeight
                + mRoundingProgress * (Math.min(
                availableWidth,
                availableHeight) - availableHeight));

        float left = getPaddingLeft() + (availableWidth - sideLengthHor) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLengthVer) / 2f;

        return new RectF(left, top, left + sideLengthHor, top + sideLengthVer);
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate(
                (int) (dx + 0.5f) + mDrawableRect.left,
                (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    public void setRoundingProgress(float roundingProgress) {
        // constrain value in range [0f,1f]
        roundingProgress = constrain(roundingProgress, RoundingProgress.MIN.progressValue(),
                                     RoundingProgress.MAX.progressValue());

        if (mRoundingProgress == roundingProgress) {
            // no change
            return;
        }

        // apply changes
        mRoundingProgress = roundingProgress;
        setup();
    }

    public float getRoundingProgress() {
        return mRoundingProgress;
    }

    private float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    /*
     * Aspect Ratio Adjustment
     */

    private ImageSize imageSize = ImageSize.STANDARD;

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

    public float getAspectRatio() {
        return this.imageSize.getWidth() > this.imageSize.getHeight()
                ? (float) this.imageSize.getWidth() / (float) this.imageSize.getHeight()
                : (float) this.imageSize.getHeight() / (float) this.imageSize.getWidth();
    }

    private AdjustAspect getAspectToAdjust() {
        return AdjustAspect.HEIGHT;
    }

    private enum AdjustAspect {
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



