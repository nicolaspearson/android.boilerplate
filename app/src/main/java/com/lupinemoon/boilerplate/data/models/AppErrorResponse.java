package com.lupinemoon.boilerplate.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.ToString;

@ToString
public class AppErrorResponse implements Parcelable {

    @SerializedName("error")
    private AppError appError;

    public AppErrorResponse(AppError appError) {
        this.appError = appError;
    }

    public AppError getAppError() {
        return appError;
    }

    public void setAppError(AppError appError) {
        this.appError = appError;
    }

    protected AppErrorResponse(Parcel in) {
        appError = (AppError) in.readValue(AppError.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(appError);
    }

    @SuppressWarnings("unused")
    public static final Creator<AppErrorResponse> CREATOR = new Creator<AppErrorResponse>() {
        @Override
        public AppErrorResponse createFromParcel(Parcel in) {
            return new AppErrorResponse(in);
        }

        @Override
        public AppErrorResponse[] newArray(int size) {
            return new AppErrorResponse[size];
        }
    };
}
