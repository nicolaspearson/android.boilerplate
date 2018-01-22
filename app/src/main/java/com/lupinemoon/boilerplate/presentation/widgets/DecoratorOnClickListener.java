package com.lupinemoon.boilerplate.presentation.widgets;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view on click listener that enlables multiple click listeners to be
 * assigned and triggered on the same view.
 */
public class DecoratorOnClickListener implements View.OnClickListener {

    private final List<View.OnClickListener> listeners = new ArrayList<>();

    public void add(View.OnClickListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onClick(View v) {
        for (View.OnClickListener listener : listeners) {
            listener.onClick(v);
        }
    }
}
