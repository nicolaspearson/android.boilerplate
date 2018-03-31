package com.lupinemoon.boilerplate.data.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.ToString;
import okhttp3.RequestBody;
import okio.Buffer;
import timber.log.Timber;

@ToString
@SuppressWarnings("WeakerAccess")
public class NetworkRequestBody extends RealmObject {

    NetworkMediaType networkMediaType;

    byte[] content;

    long contentLength;

    @PrimaryKey
    @Index
    String primaryKey;

    public NetworkRequestBody() {
        // No args constructor needed by Realm
    }

    public NetworkRequestBody(String primaryKey, RequestBody requestBody) {
        try {
            setPrimaryKey(primaryKey);
            setContentLength(requestBody.contentLength());
            setNetworkMediaType(new NetworkMediaType(getPrimaryKey(), requestBody.contentType()));

            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            setContent(buffer.readByteArray());
        } catch (Exception e) {
            Timber.e(e, "Conversion Failed");
        }
    }

    public RequestBody getSourceRequestBody() {
        return RequestBody.create(getNetworkMediaType().getSourceMediaType(), getContent());
    }

    public NetworkMediaType getNetworkMediaType() {
        return networkMediaType;
    }

    public void setNetworkMediaType(NetworkMediaType networkMediaType) {
        this.networkMediaType = networkMediaType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
