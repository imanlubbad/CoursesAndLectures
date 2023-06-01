package com.m_learning.feature.baseView;

public interface BaseView {
    void showProgress();

    void hideProgress();

    void showErrorMessage(String message);
    void showErrorDialog(String message, String okMsg, String cancelMsg, String action);
}
