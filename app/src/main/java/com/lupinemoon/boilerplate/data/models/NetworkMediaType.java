package com.lupinemoon.boilerplate.data.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import lombok.ToString;
import okhttp3.MediaType;

/**
 * Media Type, appropriate to describe
 * the content type of an HTTP request or response body.
 */
@ToString
@SuppressWarnings("WeakerAccess")
public class NetworkMediaType extends RealmObject {

    String mediaType;

    String type;

    String subtype;

    String charset;

    @PrimaryKey
    @Index
    String primaryKey;

    public NetworkMediaType() {
        // No args constructor needed by Realm
    }

    public NetworkMediaType(String primaryKey, MediaType mediaType) {
        setPrimaryKey(primaryKey);
        if (mediaType != null) {
            setMediaType(mediaType.toString());
            setType(mediaType.type() != null ? mediaType.type() : "");
            setSubtype(mediaType.subtype() != null ? mediaType.subtype() : "");
            setCharset(mediaType.charset() != null ? mediaType.charset().toString() : "");
        }
    }

    public MediaType getSourceMediaType() {
        return MediaType.parse(getMediaType());
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
