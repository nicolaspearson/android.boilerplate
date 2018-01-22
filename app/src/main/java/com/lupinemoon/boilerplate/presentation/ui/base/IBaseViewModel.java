package com.lupinemoon.boilerplate.presentation.ui.base;

public interface IBaseViewModel {
    boolean validate(String data);
    String getError(String data, String error);
    BaseViewModel.State getInstanceState();
}
