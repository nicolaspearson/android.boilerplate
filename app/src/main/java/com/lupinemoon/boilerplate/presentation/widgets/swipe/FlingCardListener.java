package com.lupinemoon.boilerplate.presentation.widgets.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

import timber.log.Timber;

public class FlingCardListener implements View.OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;
    private float BASE_ROTATION_DEGREES;

    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;

    private float MAX_COS = (float) Math.cos(Math.toRadians(45));

    private int touchPosition;

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;

    private final int parentWidth;
    private final float halfWidth;

    private final FlingListener mFlingListener;

    private final Object dataObject;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private View frame = null;

    private boolean isAnimationRunning = false;

    private boolean click = true;

    private boolean allowNegativeRotation = false;

    private boolean allowYAxisMovement = false;

    FlingCardListener(
            View frame,
            Object itemAtPosition,
            float rotation_degrees,
            FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mFlingListener = flingListener;
    }

    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                click = true;
                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try {
                    x = event.getX(mActivePointerId);
                    y = event.getY(mActivePointerId);
                    success = true;
                } catch (IllegalArgumentException e) {
                    Timber.e(e, "Exception in onTouch(view, event): %d", mActivePointerId);
                }
                if (success) {
                    // Remember where we started
                    aDownTouchX = x;
                    aDownTouchY = y;

                    // To prevent an initial jump of the magnifier, aPosX
                    // and aPosY must have the values from the magnifier frame
                    if (aPosX == 0) {
                        aPosX = frame.getX();
                    }
                    if (aPosY == 0) {
                        aPosY = frame.getY();
                    }

                    if (y < objectH / 2) {
                        touchPosition = TOUCH_ABOVE;
                    } else {
                        touchPosition = TOUCH_BELOW;
                    }
                }

                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
                view.getParent().requestDisallowInterceptTouchEvent(false);

                // Check if this is a click event and then perform a click
                if (click) view.performClick();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() &
                        MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // Find the index of the active pointer and fetch its position
                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                final float xMove = event.getX(pointerIndexMove);
                final float yMove = event.getY(pointerIndexMove);

                // Calculate the distance moved
                final float dx = xMove - aDownTouchX;
                final float dy = yMove - aDownTouchY;

                if (Math.abs(dx + dy) > 10) {
                    // Not a click event
                    click = false;

                    // Move the frame
                    aPosX += dx;
                    aPosY += dy;

                    // Calculate the rotation degrees
                    float distObjectX = aPosX - objectX;
                    float rotation = BASE_ROTATION_DEGREES * 2.f * distObjectX / parentWidth;

                    if (allowNegativeRotation && touchPosition == TOUCH_BELOW) {
                        rotation = -rotation;
                    }

                    // In this area would be code for doing something with the view as the frame moves.
                    frame.setX(aPosX);
                    if (allowYAxisMovement) {
                        frame.setY(aPosY);
                    }
                    frame.setRotation(rotation);

                    mFlingListener.onScroll(getScrollProgressPercent(), dx, dy);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    private float getScrollProgressPercent() {
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack() {
        if (movedBeyondLeftBorder()) {
            // Left Swipe
            onSelected(true, getExitPoint(-objectW), 200, null);
            mFlingListener.onScroll(-1.0f, 0, 0);
        } else if (movedBeyondRightBorder()) {
            // Right Swipe
            onSelected(false, getExitPoint(parentWidth), 200, null);
            mFlingListener.onScroll(1.0f, 0, 0);
        } else {
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.setX(objectX);
            frame.setY(objectY);
            frame.setRotation(0);
            // Animation causes text to flicker
//            frame.animate()
//                    .setDuration(200)
//                    .setInterpolator(new OvershootInterpolator(1.5f))
//                    .x(objectX)
//                    .y(objectY)
//                    .rotation(0);
            mFlingListener.onScroll(0.0f, 0, 0);
        }
        return false;
    }

    private boolean movedBeyondLeftBorder() {
        return aPosX + halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder() {
        return aPosX + halfWidth > rightBorder();
    }

    private float leftBorder() {
        return parentWidth / 4.f;
    }

    private float rightBorder() {
        return 3 * parentWidth / 4.f;
    }

    private void onSelected(
            final boolean isLeft,
            float exitY,
            long duration,
            final GenericCallback animationEndCallback) {

        isAnimationRunning = true;

        float exitX;
        if (isLeft) {
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }

        if (!allowYAxisMovement) {
            exitY = this.frame.getY();
        }

        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(exitX)
                .y(exitY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isLeft) {
                            mFlingListener.onCardExited();
                            if (animationEndCallback != null) {
                                animationEndCallback.execute();
                            } else {
                                mFlingListener.leftExit(dataObject);
                            }
                        } else {
                            mFlingListener.onCardExited();
                            if (animationEndCallback != null) {
                                animationEndCallback.execute();
                            } else {
                                mFlingListener.rightExit(dataObject);
                            }
                        }
                        isAnimationRunning = false;
                    }
                })
                .rotation(getExitRotation(isLeft));
    }

    public void selectLeft(GenericCallback animationEndCallback) {
        if (!isAnimationRunning) {
            onSelected(true, objectY, 200, animationEndCallback);
        }
    }

    public void selectRight(GenericCallback animationEndCallback) {
        if (!isAnimationRunning) {
            onSelected(false, objectY, 200, animationEndCallback);
        }
    }

    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        // Your typical y = ax + b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = BASE_ROTATION_DEGREES;
        if (allowYAxisMovement) {
            rotation = rotation * 2.f * (parentWidth - objectX) / parentWidth;
        }
        if (allowNegativeRotation && touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }

    interface FlingListener {
        void onCardExited();

        void leftExit(Object dataObject);

        void rightExit(Object dataObject);

        void onScroll(float scrollProgressPercent, float dx, float dy);
    }

}
