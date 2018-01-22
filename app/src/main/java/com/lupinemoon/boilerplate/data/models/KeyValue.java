package com.lupinemoon.boilerplate.data.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.KeyValueRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.ToString;

@ToString
@Parcel(implementations = { KeyValueRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { KeyValue.class })
@SuppressWarnings("WeakerAccess")
public class KeyValue extends RealmObject {

    // Fields must be public
    @SerializedName("key")
    String key;

    @SerializedName("value")
    String value;

    @PrimaryKey
    String primaryKey;

    @Index
    String dbClass;

    public void compoundPrimaryKey() {
        if(!isManaged()) {
            String newPrimaryKey = String.format(
                    "%s",
                    (this.key != null ? this.key : ""));
            if (!TextUtils.isEmpty(this.dbClass)) {
                newPrimaryKey = String.format("[K:%s][DBC:%s]", newPrimaryKey, this.dbClass);
            }
            setPrimaryKey(newPrimaryKey);
        }
    }

    public KeyValue() {
        // No args constructor needed by the Parceler library
        this.key = "";
        this.value = "";
        this.dbClass = "";
    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        if (key != null) {
            return key;
        }
        return "";
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        if (value != null) {
            return value;
        }
        return "";
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDbClass() {
        return dbClass;
    }

    public void setDbClass(String dbClass) {
        this.dbClass = dbClass;
    }
}