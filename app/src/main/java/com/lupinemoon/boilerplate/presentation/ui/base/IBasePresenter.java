package com.lupinemoon.boilerplate.presentation.ui.base;

import android.content.Intent;
import android.os.Bundle;

public interface IBasePresenter {
    void createView();
    void viewCreated();
    void pause();
    void resume();
    void start();
    void stop();
    void activityResult(int requestCode, int resultCode, Intent intent);
    void requestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    Bundle saveInstanceState(Bundle outState);
    void restoreInstanceState(Bundle inState);
    void destroyView();
    void destroy();
}
