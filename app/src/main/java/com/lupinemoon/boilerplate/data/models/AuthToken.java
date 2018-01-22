package com.lupinemoon.boilerplate.data.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.AuthTokenRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.ToString;

@ToString
@Parcel(implementations = { AuthTokenRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { AuthToken.class })
@SuppressWarnings("unused, WeakerAccess")
public class AuthToken extends RealmObject {

    // Fields must be public
    @PrimaryKey
    @SerializedName("token")
    String token;

    String created_at;

    public AuthToken() {
        // Empty constructor needed by the Parceler library
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

}
