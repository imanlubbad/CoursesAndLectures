package com.m_learning.utils;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.m_learning.R;

import java.io.File;
import java.io.IOException;

public class AppShareMethods {

    public static boolean isEmptyEditText(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }


    public static String getText(EditText editText) {
        return ToolUtils.convertToEngNo(editText.getText().toString().trim()) + "";
    }


    public static String getText(TextView editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isValidEmailAddress(EditText editText) {
        String email = editText.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    public static boolean isValidPassword(EditText editText) {
        return editText.getText().toString().trim().length() >= 6;
    }


    public static boolean isPasswordsMatch(EditText editText1, EditText editText2) {
        return editText1.getText().toString().trim().equals(editText2.getText().toString().trim());
    }

    public static void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public static void showSnackBar(Activity activity, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(activity.getResources().getColor(R.color.white));
        sbView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }


    public static File getNewImageFile(Context context) throws IOException {
        return File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }

}
