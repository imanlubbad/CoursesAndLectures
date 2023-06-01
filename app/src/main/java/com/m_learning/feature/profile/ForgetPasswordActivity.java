package com.m_learning.feature.profile;

import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.m_learning.R;
import com.m_learning.databinding.ActivityForgetPasswordBinding;
import com.m_learning.feature.general.LoginActivity;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.network.feature.User;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.ToolUtils;

public class ForgetPasswordActivity extends BaseActivity implements BaseView {

    private ActivityForgetPasswordBinding binding;

    public static Intent newInstance(LoginActivity mActivity, int fromWhere) {
        Intent intent = new Intent(mActivity, ForgetPasswordActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        onClickListener();

    }

    private void onClickListener() {
        binding.btnReset.setOnClickListener(view -> sendEmail(binding.edEmail));
        binding.ibtnBack.setOnClickListener(view -> finish());
    }

    public void sendEmail(EditText edEmail) {
        binding.edEmail.setError(null);
        String email = edEmail.getText().toString().trim();
        if (AppShareMethods.isEmptyEditText(edEmail)) {
            binding.edEmail.setError(getString(R.string.is_required));
            return;
        }
        if (!AppShareMethods.isValidEmailAddress(edEmail)) {
            binding.edEmail.setError(getString(R.string.invalid_email));
            return;
        }

        if (ToolUtils.isNetworkConnected()) {
            ToolUtils.hideKeyboard(this);
            showProgress();
            User.forgetPassword(email,
                    new RequestListener<String>() {
                        @Override
                        public void onSuccess(String data) {
                            hideProgress();
                            showErrorMessage(data);
                        }
                        @Override
                        public void onFail(String message) {
                            hideProgress();
                            showErrorMessage( message);
                        }
                    });
        } else {
            showErrorMessage(getString(R.string.noInternetConnection));
        }

    }


    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);
    }


}