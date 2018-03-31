package com.lupinemoon.boilerplate.data.network.interfaces;

import com.lupinemoon.boilerplate.data.models.AuthToken;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {

    @FormUrlEncoded
    @POST("login")
    Flowable<AuthToken> postLogin(
            @Field("msisdn") String msisdn,
            @Field("password") String password,
            @Field("website_id") String websiteId);

}
