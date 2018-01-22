package com.lupinemoon.boilerplate.presentation.ui.features.offline;

import com.lupinemoon.boilerplate.databinding.ActivityOfflineBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseViewModel;

class OfflineContract {

    interface View extends IBaseView {
        Presenter getPresenter();
        ViewModel getViewModel();
        ActivityOfflineBinding getBinding();
        void closeOfflineActivity();
    }

    interface Presenter extends IBasePresenter {

    }

    interface ViewModel extends IBaseViewModel {

    }

}
