package com.lupinemoon.boilerplate.presentation.ui.features.template;

import android.databinding.Bindable;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;

public class TemplateViewModel extends BaseViewModel implements TemplateContract.ViewModel {

    private TemplateContract.View templateView;

    private String templateString;

    TemplateViewModel(@NonNull TemplateContract.View view, @Nullable State savedInstanceState) {
        // Set the view locally
        templateView = ActivityUtils.checkNotNull(view, "view cannot be null!");

        if (savedInstanceState != null && savedInstanceState instanceof TemplateState) {
            TemplateState templateState = (TemplateState) savedInstanceState;
            // Restore local variable from saved state
            templateString = templateState.templateString;
        }
    }

    @Override
    public State getInstanceState() {
        return new TemplateState(this);
    }

    public void onTemplateClick(View view) {
        if (validate(templateString) && getError(templateString, "Error") == null) {
            templateView.getPresenter().performAction(templateString);
        }
    }

    @Bindable
    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
        notifyPropertyChanged(BR.templateString);
    }

    private static class TemplateState extends State {

        final String templateString;

        TemplateState(TemplateViewModel viewModel) {
            super(viewModel);
            templateString = viewModel.templateString;
        }

        TemplateState(Parcel in) {
            super(in);
            templateString = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(templateString);
        }

        @SuppressWarnings("unused")
        public static final Creator<TemplateState> CREATOR = new Creator<TemplateViewModel.TemplateState>() {
            @Override
            public TemplateViewModel.TemplateState createFromParcel(Parcel in) {
                return new TemplateViewModel.TemplateState(in);
            }

            @Override
            public TemplateViewModel.TemplateState[] newArray(int size) {
                return new TemplateViewModel.TemplateState[size];
            }
        };
    }
}
