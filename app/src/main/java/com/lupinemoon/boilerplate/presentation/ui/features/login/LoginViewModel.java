package com.lupinemoon.boilerplate.presentation.ui.features.login;

import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.lupinemoon.boilerplate.BR;
import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.R;
import com.lupinemoon.boilerplate.presentation.ui.base.BaseViewModel;
import com.lupinemoon.boilerplate.presentation.utils.ActivityUtils;
import com.lupinemoon.boilerplate.presentation.widgets.Toasty;

import timber.log.Timber;

public class LoginViewModel extends BaseViewModel implements LoginContract.ViewModel {

    private LoginContract.View loginView;

    private String mobileNumber;
    private String password;

    private boolean loginClicked = false;

    LoginViewModel(@NonNull LoginContract.View view, @Nullable State savedInstanceState) {
        loginView = ActivityUtils.checkNotNull(view, "view cannot be null!");

        if (savedInstanceState != null && savedInstanceState instanceof LoginState) {
            LoginState loginState = (LoginState) savedInstanceState;
            mobileNumber = loginState.mobileNumber;
            password = loginState.password;
        }

        // Setup key event for mobile number
        loginView.getBinding().mobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Timber.d("Login edit text event: %s", actionId);
                processEditTextEvent(actionId);
                return false;
            }
        });

        // Setup key event for password
        loginView.getBinding().password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Timber.d("Login edit text event: %s", actionId);
                processEditTextEvent(actionId);
                return false;
            }
        });
    }

    // Method invoked to process and determine if should auto login
    // it is a roulette, returning the user to either of the empty fields
    // unless both are filled with text, in which case, we perform click on login.
    private void processEditTextEvent(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (TextUtils.isEmpty(loginView.getBinding().password.getText())) {
                loginView.getBinding().password.requestFocus();
            } else {
                loginView.getBinding().loginButton.performClick();
            }
        } else if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (TextUtils.isEmpty(loginView.getBinding().mobileNumber.getText())) {
                loginView.getBinding().mobileNumber.requestFocus();
            } else {
                if (TextUtils.isEmpty(loginView.getBinding().password.getText())) {
                    loginView.getBinding().password.requestFocus();
                } else {
                    loginView.getBinding().loginButton.performClick();
                }
            }
        }
    }

    @Override
    public State getInstanceState() {
        return new LoginState(this);
    }

    public void onLoginClick() {
        loginClicked = true;
        notifyChange();
        if (validate()) {
            loginView.getPresenter().performLogin(mobileNumber, password);
        }
    }

    public void onSkipClick(){
        // Skip login
        loginView.getPresenter().performSkip();
    }

    public void onRegisterClick() {
        reportAnalyticsJoinNowTap();
    }

    public void onForgotPasswordClick() {
        loginView.showToastMsg(loginView.getActivity().getApplicationContext().getString(R.string.forgot_password),
                               Toasty.ToastType.INFO);
    }

    @Bindable
    public boolean getAllowSkip() {
        return BuildConfig.DEBUG;
    }

    @Bindable
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        notifyPropertyChanged(BR.mobileNumber);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public String getMobileNumberError() {
        if (!loginClicked) {
            return "";
        }

        int mobileNumberMinLength = loginView.getActivity().getResources().getInteger(R.integer.mobile_number_min_length);
        if (TextUtils.isEmpty(mobileNumber)) {
            return loginView.getActivity().getString(R.string.error_please_fill_in_mobile_number);
        } else if (mobileNumber.length() < mobileNumberMinLength) {
            return String.format(
                    loginView.getActivity().getString(R.string.error_mobile_number_invalid_length),
                    mobileNumberMinLength);
        }

        return "";
    }

    public String getPasswordError() {
        if (!loginClicked) {
            return "";
        }

        int passwordMinLength = loginView.getActivity().getResources().getInteger(R.integer.password_min_length);
        if (TextUtils.isEmpty(password)) {
            return loginView.getActivity().getString(R.string.error_please_fill_in_password);
        } else if (password.length() < passwordMinLength) {
            return String.format(
                    loginView.getActivity().getString(R.string.error_password_invalid_length),
                    passwordMinLength);
        }

        return "";
    }

    private boolean validate() {
        boolean valid;
        valid = TextUtils.isEmpty(getMobileNumberError());

        if (valid) {
            valid = TextUtils.isEmpty(getPasswordError());
        }

        Timber.d("Valid: %b", valid);

        return valid;
    }

    private static class LoginState extends State {

        final String mobileNumber;
        final String password;

        LoginState(LoginViewModel viewModel) {
            super(viewModel);
            mobileNumber = viewModel.mobileNumber;
            password = viewModel.password;
        }

        LoginState(Parcel in) {
            super(in);
            mobileNumber = in.readString();
            password = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mobileNumber);
            dest.writeString(password);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<LoginState> CREATOR = new Parcelable.Creator<LoginState>() {
            @Override
            public LoginState createFromParcel(Parcel in) {
                return new LoginState(in);
            }

            @Override
            public LoginState[] newArray(int size) {
                return new LoginState[size];
            }
        };
    }

    private void reportAnalyticsJoinNowTap() {

    }

}
