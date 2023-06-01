package com.m_learning.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.m_learning.R;

import java.util.Objects;


public class LoadingDialog {

    private AlertDialog alertDialog;

    private Activity activity;

    @SuppressLint("InflateParams")
    public LoadingDialog(Activity activity) {
        this.activity = activity;

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.CustomDialogTheme)
                .setCancelable(false)
                .setView(LayoutInflater.from(activity).inflate(R.layout.dialog_loading_layout, null));
        alertDialog = dialog.create();

    }

    public void showDialog() {
        if (activity.isDestroyed()) {
            return;
        }

        if (alertDialog != null && !alertDialog.isShowing()) {
            Context context = ((ContextWrapper) alertDialog.getContext()).getBaseContext();
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing()) {
                    alertDialog.show();
                    setDialogDimension();
                }
            } else {
                try {
                    alertDialog.show();
                    setDialogDimension();
                }catch (Exception ex){

                }

            }
        }
    }

    @SuppressLint("NewApi")
    private void setDialogDimension() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
        int width = (int) (activity.getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        int height = (int) (activity.getWindowManager().getDefaultDisplay().getHeight() * 0.35);
        layoutParams.width = width;
        layoutParams.height = height;
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    public void hideDialog() {
        if (activity.isDestroyed()) {
            return;
        }

        if (alertDialog != null && alertDialog.isShowing()) {
            Context context = ((ContextWrapper) alertDialog.getContext()).getBaseContext();
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing()) {
                    alertDialog.dismiss();
                }
            } else {
                alertDialog.dismiss();
            }
        }
    }

    public void setDialogCancelable() {
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
    }
}
