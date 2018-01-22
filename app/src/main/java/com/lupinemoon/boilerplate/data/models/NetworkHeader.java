package com.lupinemoon.boilerplate.data.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.ToString;
import okhttp3.Headers;

/**
 *  Header class to encapsulate the header information regarding a network request.
 *  Is composed into a NetworkRequest object
 */

@ToString
@SuppressWarnings("WeakerAccess")
public class NetworkHeader extends RealmObject {

    String namesAndValues;

    @PrimaryKey
    @Index
    String primaryKey;

    public NetworkHeader() {
        // No args constructor needed by Realm
    }

    public NetworkHeader(String primaryKey, Headers headers) {
        setPrimaryKey(primaryKey);
        setNamesAndValues(headers.toString());
    }

    public String getNamesAndValues() {
        return namesAndValues;
    }

    public void setNamesAndValues(String namesAndValues) {
        this.namesAndValues = namesAndValues;
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        String[] headerArray = getNamesAndValues().split("\\n");
        for (String line : headerArray) {
            builder.add(line);
        }
        return builder.build();
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
