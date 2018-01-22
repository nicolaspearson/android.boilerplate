package com.lupinemoon.boilerplate.data.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.ToString;
import okhttp3.Headers;
import okhttp3.RequestBody;
import com.lupinemoon.boilerplate.presentation.utils.StringUtils;

/**
 *  Request object used to save the state of a network call.
 *  This will enable database persistence and reconstruction of the call later.
 */

@ToString
@SuppressWarnings("WeakerAccess")
public class NetworkRequest extends RealmObject {

    String method;

    String url;

    NetworkHeader networkHeader;

    NetworkRequestBody networkRequestBody;

    @PrimaryKey
    @Index
    String primaryKey;

    public void generatePrimaryKey() {
        if (!isManaged()) {
            setPrimaryKey(StringUtils.getUniqueUUID().substring(0, 32));
        }
    }

    public NetworkRequest() {
        // No args constructor needed by Realm
    }

    public NetworkRequest(String method, String url, Headers headers, RequestBody requestBody) {
        generatePrimaryKey();
        setMethod(method);
        setUrl(url);
        setNetworkHeader(new NetworkHeader(getPrimaryKey(), headers));
        setNetworkRequestBody(new NetworkRequestBody(getPrimaryKey(), requestBody));
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NetworkHeader getNetworkHeader() {
        return networkHeader;
    }

    public void setNetworkHeader(NetworkHeader networkHeader) {
        this.networkHeader = networkHeader;
    }

    public NetworkRequestBody getNetworkRequestBody() {
        return networkRequestBody;
    }

    public void setNetworkRequestBody(NetworkRequestBody networkRequestBody) {
        this.networkRequestBody = networkRequestBody;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
