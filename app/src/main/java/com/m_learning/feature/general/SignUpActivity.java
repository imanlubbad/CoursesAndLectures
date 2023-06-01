package com.m_learning.feature.general;

import static com.m_learning.utils.ConstantApp.ACTION_NOTHING;
import static com.m_learning.utils.ConstantApp.FROM_SIGN_UP;
import static com.m_learning.utils.ConstantApp.TYPE_USER;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.m_learning.R;
import com.m_learning.databinding.ActivitySignUpBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.User;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.DialogUtils;
import com.m_learning.utils.ToolUtils;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Calendar;

public class SignUpActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private ActivitySignUpBinding binding;
    private SignUpActivity mActivity;
    private long dob = 0;


    public static Intent newInstance(Activity mActivity) {
        return new Intent(mActivity, SignUpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mActivity = this;
        onClickListener();

    }

    void onClickListener() {
        binding.btnSignUp.setOnClickListener(view -> {
            validateInput(binding.edFirstName, binding.edMiddleName, binding.edLastName,
                    binding.edAddress, binding.edBirthDate, binding.edEmail, binding.edMobileNo, binding.edPassword, binding.edConfirmPassword);
        });
        binding.tvSignIn.setOnClickListener(view -> {
            finish();
        });
        binding.ibtnBack.setOnClickListener(view -> {
            finish();
        });
        binding.edBirthDate.setOnClickListener(view -> {
            setDate();
        });

    }

    private void validateInput(EditText edFirstName, EditText edMiddleName, EditText edLastName, EditText edAddress,
                               EditText edBirthDate, EditText edEmail, EditText edMobileNo, EditText edPassword,
                               EditText edConfirmPassword) {

        edFirstName.setError(null);
        edMiddleName.setError(null);
        edLastName.setError(null);
        edAddress.setError(null);
        edEmail.setError(null);
        edMobileNo.setError(null);
        edPassword.setError(null);
        edConfirmPassword.setError(null);
        edBirthDate.setError(null);

        if (AppShareMethods.isEmptyEditText(edFirstName)) {
            edFirstName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edMiddleName)) {
            edMiddleName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edLastName)) {
            edLastName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edBirthDate)) {
            edMiddleName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edAddress)) {
            edAddress.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edEmail)) {
            edEmail.setError(getString(R.string.is_required));
            return;
        }
        if (!AppShareMethods.isValidEmailAddress(edEmail)) {
            edEmail.setError(getString(R.string.invalid_email));
            return;
        }
        if (AppShareMethods.isEmptyEditText(edMobileNo)) {
            edMobileNo.setError(getString(R.string.is_required));
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
        if (!AppShareMethods.isPasswordsMatch(edPassword, edConfirmPassword)) {
            edConfirmPassword.setError(getString(R.string.error_match_password));
            return;
        }
        UserInfo userData = new UserInfo();
        userData.setAddress(edAddress.getText().toString().trim());
        userData.setEmail(edEmail.getText().toString().trim());
        userData.setFirstName(edFirstName.getText().toString().trim());
        userData.setMiddleName(edMiddleName.getText().toString().trim());
        userData.setLastName(edLastName.getText().toString().trim());
        userData.setMobile(edMobileNo.getText().toString().trim());
        userData.setDob(dob);
        userData.setIsActive(1);
        userData.setUserType(TYPE_USER);
//        userData.setUserType(TYPE_LECTURER);
        userData.setDeviceToken(FirebaseInstanceId.getInstance().getToken());

        ToolUtils.hideKeyboard(this);
        if (ToolUtils.isNetworkConnected()) {
            showProgress();

            User.userSignUp(userData, edPassword.getText().toString(), new RequestListener<UserInfo>() {
                @Override
                public void onSuccess(UserInfo data) {
                    hideProgress();
                    saveUserToDatabase(data);
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

    private void saveUserToDatabase(UserInfo userData) {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            userData.setCreatedAt(ToolUtils.getCurrentDateTimeLong());
            User.saveUserInfoToDatabase(userData, new StringRequestListener<UserInfo>() {
                @Override
                public void onSuccess(String msg, UserInfo data) {
                    hideProgress();
                    showErrorMessage(msg);
                    AppSharedData.setUserData(data);
                    AppSharedData.setUserLogin(true);
                    ToolUtils.goToMainUser(mActivity, FROM_SIGN_UP);
                }

                @Override
                public void onFail(String message) {
                    showErrorMessage(message);
                    hideProgress();
                }
            });
        } else {
            showErrorMessage(mActivity.getString(R.string.noInternetConnection));

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

    void setDate() {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .maxDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .minDate(1950, 0, 1)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        binding.edBirthDate.setText(date);
        dob = ToolUtils.convertDateToMillisecond(date);
    }
}