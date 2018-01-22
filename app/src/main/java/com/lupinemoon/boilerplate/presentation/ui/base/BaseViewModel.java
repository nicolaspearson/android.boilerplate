package com.lupinemoon.boilerplate.presentation.ui.base;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public abstract class BaseViewModel extends BaseObservable implements IBaseViewModel {

    public abstract State getInstanceState();

    @Override
    public boolean validate(String data) {
        return !TextUtils.isEmpty(data);
    }

    @Override
    public String getError(String data, String error) {
        if (TextUtils.isEmpty(data)) {
            return error;
        }
        return null;
    }

    public static class State implements Parcelable {

        protected State(BaseViewModel viewModel) {

        }

        protected State(Parcel in) {

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel source) {
                return new State(source);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };

    }
}

