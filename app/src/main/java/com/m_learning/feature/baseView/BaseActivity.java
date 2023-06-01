package com.m_learning.feature.baseView;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.m_learning.utils.LoadingDialog;


public class BaseActivity extends AppCompatActivity implements BaseView {

    private LoadingDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoadingDialog();
    }


    public void initLoadingDialog() {
        mLoading = new LoadingDialog(this);
        mLoading.setDialogCancelable();
    }

    public void setDialogCancelable() {
        mLoading.setDialogCancelable();
    }

    @Override
    public void showProgress() {
        mLoading.showDialog();
    }

    @Override
    public void hideProgress() {
        mLoading.hideDialog();
    }

    @Override
    public void showErrorMessage(String message) {
    }

    @Override
    public void showErrorDialog(String message, String okMsg, String cancelMsg, String action) {

    }

}
