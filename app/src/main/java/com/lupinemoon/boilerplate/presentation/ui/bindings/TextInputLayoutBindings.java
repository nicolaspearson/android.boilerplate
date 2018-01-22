package com.lupinemoon.boilerplate.presentation.ui.bindings;

import android.databinding.BindingAdapter;
import android.support.design.widget.TextInputLayout;

public class TextInputLayoutBindings {

    @BindingAdapter({"error"})
    public static void setError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }
}
