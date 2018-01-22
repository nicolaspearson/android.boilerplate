package com.lupinemoon.boilerplate.presentation.ui.features.login;

import com.lupinemoon.boilerplate.databinding.ActivityLoginBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseViewModel;


class LoginContract {

    interface View extends IBaseView {
        Presenter getPresenter();
        ViewModel getViewModel();
        ActivityLoginBinding getBinding();
    }

    interface Presenter extends IBasePresenter {
        void performLogin(String mobileNumber, String password);
        void performSkip();
    }

    interface ViewModel extends IBaseViewModel {

    }

}
