package com.lupinemoon.boilerplate.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class AppError implements Parcelable {

    @SerializedName("message")
    private List<KeyValue> messages;

    @SerializedName("statusCode")
    private Integer statusCode;

    public AppError() {
        // Empty constructor
    }

    public List<KeyValue> getMessages() {
        return messages;
    }

    public void setMessages(List<KeyValue> messages) {
        this.messages = messages;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    protected AppError(Parcel in) {
        if (in.readByte() == 0x01) {
            messages = new ArrayList<>();
            in.readList(messages, KeyValue.class.getClassLoader());
        } else {
            messages = null;
        }
        statusCode = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (messages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(messages);
        }
        if (statusCode == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(statusCode);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<AppError> CREATOR = new Creator<AppError>() {
        @Override
        public AppError createFromParcel(Parcel in) {
            return new AppError(in);
        }

        @Override
        public AppError[] newArray(int size) {
            return new AppError[size];
        }
    };
}
