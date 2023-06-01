package com.m_learning.utils;

import static android.app.Activity.RESULT_OK;
import static com.m_learning.utils.ConstantApp.ACTION_CLOSE;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;


public class DialogUtils {
    public static void showAlertDialog(final Activity mActivity, String title, String message, String okMsg, String cancelMsg, final String action) {

        final CustomFloatDialog dialog = CustomFloatDialog.newInstance(title, message, okMsg, cancelMsg);
        dialog.setListener(new CustomFloatDialog.onClickListener() {
            @Override
            public void onOkClick() {
                dialog.dismiss();
                if (action.equalsIgnoreCase(ACTION_CLOSE)) {
                    mActivity.setResult(RESULT_OK);
                    mActivity.finish();
                }
            }

            @Override
            public void onCancelClick() {
                dialog.dismiss();
            }
        });
        ((AppCompatActivity) mActivity).getSupportFragmentManager().beginTransaction().add(dialog, "DialogMessage").commitAllowingStateLoss();
    }


    public static void showAlertDialogWithListener(Activity mActivity, String title, String message, String okMsg, String cancelMsg, final onClickListener listener) {

        final CustomFloatDialog dialog = CustomFloatDialog.newInstance(title, message, okMsg, cancelMsg);
        dialog.setListener(new CustomFloatDialog.onClickListener() {
            @Override
            public void onOkClick() {
                if (listener != null) listener.onOkClick();
                dialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                listener.onCancelClick();
                dialog.dismiss();
            }
        });
        ((AppCompatActivity) mActivity).getSupportFragmentManager().beginTransaction().add(dialog, "DialogMessage").commitAllowingStateLoss();
    }


    public interface onClickListener {
        void onOkClick();

        void onCancelClick();
    }

    public interface onClickListenerWithText {
        void onOkClick(String text);

        void onCancelClick();
    }
}
