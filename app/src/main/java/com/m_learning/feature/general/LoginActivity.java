package com.m_learning.feature.general;

import static com.m_learning.network.feature.User.logoutUser;
import static com.m_learning.utils.ConstantApp.TYPE_USER;
import static com.m_learning.utils.ConstantApp.ACTION_NOTHING;
import static com.m_learning.utils.ConstantApp.FROM_LOGIN;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.m_learning.R;
import com.m_learning.databinding.ActivityLoginBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.feature.profile.ForgetPasswordActivity;
import com.m_learning.network.feature.User;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.DialogUtils;
import com.m_learning.utils.ToolUtils;

public class LoginActivity extends BaseActivity implements BaseView {
    private ActivityLoginBinding binding;
    Activity mActivity;

    public static Intent newInstance(Context mActivity, int fromWhere) {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mActivity = this;
        onClickListener();

    }

    void onClickListener() {
        binding.btnLogin.setOnClickListener(view -> {
            validateInput(binding.edEmail, binding.edPassword);
        });
        binding.tvSingUp.setOnClickListener(view -> {
            startActivity(SignUpActivity.newInstance(this));
        });
        binding.tvForgot.setOnClickListener(view -> {
            startActivity(ForgetPasswordActivity.newInstance(this, FROM_LOGIN));
        });
    }

    public void validateInput(EditText edEmail, EditText edPassword) {
        edEmail.setError(null);
        edPassword.setError(null);
        if (AppShareMethods.isEmptyEditText(edEmail)) {
            edEmail.setError(getString(R.string.is_required));
            return;
        }
        if (!AppShareMethods.isValidEmailAddress(edEmail)) {
            edEmail.setError(getString(R.string.invalid_email));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edPassword)) {
            edPassword.setError(getString(R.string.is_required));
            return;
        }
        if (!AppShareMethods.isValidPassword(edPassword)) {
            edPassword.setError(getString(R.string.error_week_password));
            return;
        }
        ToolUtils.hideKeyboard(this);
        if (ToolUtils.isNetworkConnected()) {
            showProgress();

            User.loginWithEmailAndPassword(edEmail.getText().toString(), edPassword.getText().toString(), new StringRequestListener<UserInfo>() {
                @Override
                public void onSuccess(String msg, UserInfo data) {
                    hideProgress();
                    if (data != null) {
                        if (data.getIsActive() == 0) {
                            logoutUser(mActivity);
                        } else {
                            AppSharedData.setUserData(data);
                            AppSharedData.setUserLogin(true);

                            if (data.getUserType().equals(TYPE_USER))
                                ToolUtils.goToMainUser(mActivity, FROM_LOGIN);
                            else
                                ToolUtils.goToTeacherMainActivity(mActivity, FROM_LOGIN);

                        }
                    } else {
                        showErrorMessage(msg);
                    }
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorMessage(message);

                }
            });
        } else {
            hideProgress();
            showErrorDialog(mActivity.getString(R.string.noInternetConnection), "", mActivity.getString(R.string.ok), "");

        }
    }


    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);
    }

    @Override
    public void showErrorDialog(String message, String okMsg, String cancelMsg, String action) {
        DialogUtils.showAlertDialog(this, "", message, okMsg, cancelMsg, ACTION_NOTHING);
    }
}