package com.lupinemoon.boilerplate.presentation.ui.features.landing;

import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;

interface LandingContract {

    interface View extends IBaseView {
        Presenter getPresenter();
    }

    interface Presenter extends IBasePresenter {
        void retryQueuedRequests();
    }

}
