package com.lupinemoon.boilerplate.presentation.ui.features.template;

import com.lupinemoon.boilerplate.databinding.ActivityTemplateBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseViewModel;

class TemplateContract {

    interface View extends IBaseView {
        Presenter getPresenter();
        ViewModel getViewModel();
        ActivityTemplateBinding getBinding();
    }

    interface Presenter extends IBasePresenter {
        void performAction(String actionText);
    }

    interface ViewModel extends IBaseViewModel {

    }

}
