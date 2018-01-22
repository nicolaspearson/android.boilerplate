package com.lupinemoon.boilerplate.data.network.services;

import android.content.Context;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import com.lupinemoon.boilerplate.MainApplication;
import com.lupinemoon.boilerplate.data.models.AuthToken;
import com.lupinemoon.boilerplate.data.network.interfaces.AuthApi;
import com.lupinemoon.boilerplate.data.network.rest.ServiceGenerator;
import com.lupinemoon.boilerplate.data.storage.AppRepository;
import com.lupinemoon.boilerplate.presentation.utils.Constants;
import com.lupinemoon.boilerplate.presentation.utils.DateTimeUtils;

public class AuthService {

    private static AuthService authService;

    private AuthApi authApi;

    private AppRepository appRepository;

    private AuthService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public static synchronized AuthService getInstance(AppRepository appRepository) {
        if (authService == null) {
            authService = new AuthService(appRepository);
        }
        return authService;
    }

    private synchronized AuthApi getAuthApi(Context context) {
        // We want to reuse the same instance
        if (authApi == null) {
            authApi = ServiceGenerator.getInstance().createService(AuthApi.class, context);
        }

        return authApi;
    }

    public Flowable<AuthToken> doLogin(
            final Context context,
            String msisdn,
            String password,
            String websiteId) {
        return getAuthApi(context).postLogin(msisdn, password, websiteId)
                .map(new Function<AuthToken, AuthToken>() {
                    @Override
                    public AuthToken apply(AuthToken token) {
                        // Add the created_at date to the token
                        if (token != null) {
                            token.setCreatedAt(DateTimeUtils.getCurrentTimeStamp());
                        }

                        MainApplication.getStorage(context).putObject(
                                Constants.KEY_AUTH_TOKEN,
                                token);
                        MainApplication.setLoggedIn(true);
                        return token;
                    }
                });
    }
}
