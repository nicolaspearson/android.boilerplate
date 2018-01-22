package com.lupinemoon.boilerplate.presentation.ui.features.splash;

import com.lupinemoon.boilerplate.presentation.ui.base.BaseContract;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;

class SplashContract extends BaseContract {

    interface View extends IBaseView {
        Presenter getPresenter();
        void setPresenter(IBasePresenter presenter);
        void startLandingActivity();
        void startLoginActivity();
    }

    interface Presenter extends IBasePresenter {
        void performAutoLogin();
    }

}
