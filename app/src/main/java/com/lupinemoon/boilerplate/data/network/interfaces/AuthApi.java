package com.lupinemoon.boilerplate.data.network.interfaces;

import io.reactivex.Flowable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import com.lupinemoon.boilerplate.data.models.AuthToken;

public interface AuthApi {

    @FormUrlEncoded
    @POST("login")
    Flowable<AuthToken> postLogin(
            @Field("msisdn") String msisdn,
            @Field("password") String password,
            @Field("website_id") String websiteId);

}
