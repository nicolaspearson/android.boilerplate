package com.lupinemoon.boilerplate.presentation.widgets.swipe;

import android.view.View;

class SwipeCardView {

    private View view;

    private FlingCardListener flingCardListener;

    SwipeCardView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    FlingCardListener getFlingCardListener() {
        return flingCardListener;
    }

    void setFlingCardListener(FlingCardListener flingCardListener) {
        this.flingCardListener = flingCardListener;
    }
}
