package com.lupinemoon.boilerplate.presentation.ui.features.landing.home;

import android.support.v4.app.Fragment;

import com.lupinemoon.boilerplate.databinding.FragmentHomeBinding;
import com.lupinemoon.boilerplate.presentation.ui.base.IBasePresenter;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseView;
import com.lupinemoon.boilerplate.presentation.ui.base.IBaseViewModel;
import com.lupinemoon.boilerplate.presentation.widgets.interfaces.GenericCallback;

public class HomeContract {

    public interface View extends IBaseView {
        Presenter getPresenter();
        ViewModel getViewModel();
        FragmentHomeBinding getBinding();
        Fragment getFragment();
        void showNetworkErrorLayout(final GenericCallback genericCallback);
    }

    public interface Presenter extends IBasePresenter {

    }

    interface ViewModel extends IBaseViewModel {

    }

}
