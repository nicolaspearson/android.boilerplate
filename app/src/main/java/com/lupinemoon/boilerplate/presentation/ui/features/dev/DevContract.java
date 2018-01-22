package com.lupinemoon.boilerplate.presentation.ui.features.dev;

import java.io.File;

import com.lupinemoon.boilerplate.databinding.ActivityDevBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseViewModel;

class DevContract {

    interface View extends IBaseView {
        Presenter getPresenter();
        ViewModel getViewModel();
        ActivityDevBinding getBinding();
        void emailFile(File file);
    }

    interface Presenter extends IBasePresenter {
        void performExportDatabase();
        void testPostFail();
    }

    interface ViewModel extends IBaseViewModel {

    }

}
