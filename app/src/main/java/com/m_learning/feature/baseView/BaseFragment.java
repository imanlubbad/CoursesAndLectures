package com.m_learning.feature.baseView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.m_learning.utils.LoadingDialog;


public abstract class BaseFragment extends Fragment implements BaseView {

    private LoadingDialog mLoading;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        View view = provideYourFragmentView(inflater, parent, savedInstanseState);
//        initLoadingDialog();
        return view;
    }

    public abstract View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);

    public void initLoadingDialog() {
        mLoading = new LoadingDialog(getActivity());
        mLoading.setDialogCancelable();
    }

    public void setDialogCancelable() {
        if (mLoading != null)
            mLoading.setDialogCancelable();
    }

    @Override
    public void showProgress() {
        if (mLoading != null)
            mLoading.showDialog();
    }

    @Override
    public void hideProgress() {
        if (mLoading != null)
            mLoading.hideDialog();
    }

    @Override
    public void showErrorMessage(String message) {
    }

    @Override
    public void showErrorDialog(String message, String okMsg, String cancelMsg, String action) {
    }
}
