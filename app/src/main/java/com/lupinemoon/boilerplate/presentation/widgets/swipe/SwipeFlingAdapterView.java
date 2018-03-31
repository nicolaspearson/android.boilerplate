package com.lupinemoon.boilerplate.presentation.widgets.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.lupinemoon.boilerplate.R;

import java.util.ArrayList;
import java.util.List;

public class SwipeFlingAdapterView extends BaseFlingAdapterView {

    private int MAX_VISIBLE = 5;
    private int MIN_ADAPTER_STACK = 10;
    private float ROTATION_DEGREES = 15.f;

    private Adapter mAdapter;

    private onFlingListener mainFlingListener;
    private AdapterDataSetObserver mDataSetObserver;

    private boolean mInLayout = false;
    private boolean mReset = false;
    int nextAdapterCard = 0;

    private List<SwipeCardView> swipeCardViews = new ArrayList<>();

    public SwipeFlingAdapterView(Context context) {
        this(context, null);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeAdapterStyle);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(
                attrs,
                R.styleable.SwipeFlingAdapterView,
                defStyle,
                0);

        MAX_VISIBLE = a.getInt(R.styleable.SwipeFlingAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(
                R.styleable.SwipeFlingAdapterView_min_adapter_stack,
                MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(
                R.styleable.SwipeFlingAdapterView_rotation_degrees,
                ROTATION_DEGREES);
        a.recycle();
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return false;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            getAdapter().unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;

        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            getAdapter().registerDataSetObserver(mDataSetObserver);
        }
    }

    public onFlingListener getMainFlingListener() {
        return mainFlingListener;
    }

    public void setMainFlingListener(onFlingListener onFlingListener) {
        this.mainFlingListener = onFlingListener;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    public View getSelectedView() {
        if (swipeCardViews != null && swipeCardViews.size() > 0 && swipeCardViews.get(0) != null) {
            return swipeCardViews.get(0).getView();
        }
        return null;
    }

    public FlingCardListener getTopCardListener() {
        if (swipeCardViews != null && swipeCardViews.size() > 0 && swipeCardViews.get(0) != null) {
            return swipeCardViews.get(0).getFlingCardListener();
        }
        return null;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mInLayout = true;

            for (int i = getChildCount(); i < MAX_VISIBLE; ++i) {
                addNextCard();
            }

            mInLayout = false;
        }

        @Override
        public void onInvalidated() {
            // Reset the UI
            mReset = true;
            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // If we don't have an adapter, we don't need to do anything
        if (mAdapter == null || getAdapter().getCount() == 0) {
            nextAdapterCard = 0;
            removeAllViewsInLayout();
            return;
        }

        mInLayout = true;

        if (mReset) {
            // Reset the UI
            removeAllViewsInLayout();
            nextAdapterCard = 0;
            mReset = false;
        }

        for (int i = getChildCount(); i < MAX_VISIBLE; ++i) {
            addNextCard();
        }

        mInLayout = false;

        if (getAdapter().getCount() <= MIN_ADAPTER_STACK) {
            getMainFlingListener().onAdapterAboutToEmpty(getAdapter().getCount());
        }
    }

    private void addNextCard() {
        if (nextAdapterCard < getAdapter().getCount()) {
            View view = getAdapter().getView(nextAdapterCard, null, this);
            if (view.getVisibility() != View.GONE) {
                makeAndAddView(view);
            }
            nextAdapterCard++;
        }
    }

    private void makeAndAddView(View view) {
        SwipeCardView swipeCardView = new SwipeCardView(view);
        View child = swipeCardView.getView();

        int childPosition = 0;

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        addViewInLayout(child, childPosition, lp, true);

        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(
                    getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(
                    getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }

        int absoluteGravity = gravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            absoluteGravity = Gravity.getAbsoluteGravity(gravity, getLayoutDirection());
        }

        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }

        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h);

        // Add a fling listener to the view
        swipeCardView.setFlingCardListener(new FlingCardListener(
                child,
                childPosition,
                ROTATION_DEGREES,
                new FlingCardListener.FlingListener() {
                    @Override
                    public void onCardExited() {
                        getMainFlingListener().removeFirstObjectInAdapter();
                        removeViewInLayout(getSelectedView());
                        swipeCardViews.remove(0);
                        nextAdapterCard--;
                        requestLayout();
                    }

                    @Override
                    public void leftExit(Object dataObject) {
                        getMainFlingListener().onLeftCardExit(dataObject);
                    }

                    @Override
                    public void rightExit(Object dataObject) {
                        getMainFlingListener().onRightCardExit(dataObject);
                    }

                    @Override
                    public void onScroll(float scrollProgressPercent, float dx, float dy) {
                        getMainFlingListener().onScroll(scrollProgressPercent, dx, dy);
                    }
                }));
        child.setOnTouchListener(swipeCardView.getFlingCardListener());
        swipeCardViews.add(swipeCardView);

    }

    public interface onFlingListener {
        void removeFirstObjectInAdapter();

        void onLeftCardExit(Object dataObject);

        void onRightCardExit(Object dataObject);

        void onAdapterAboutToEmpty(int itemsInAdapter);

        void onScroll(float scrollProgressPercent, float dx, float dy);
    }
}
