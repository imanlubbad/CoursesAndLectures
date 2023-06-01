package com.m_learning.feature.general;

import static com.m_learning.utils.ConstantApp.TYPE_USER;
import static com.m_learning.utils.ConstantApp.FROM_SPLASH;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.m_learning.databinding.ActivitySplashBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.model.UserInfo;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ConstantApp;
import com.m_learning.utils.ToolUtils;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private final Handler handler = new Handler();

    public static Intent newInstance(Context mActivity, int fromWhere) {
        Intent intent = new Intent(mActivity, SplashActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        this.handler.postDelayed(this::goToNextScreen, ConstantApp.SPLASH_TIME_OUT);
    }

    private void goToNextScreen() {
        if (AppSharedData.isUserLogin() && AppSharedData.getUserData() != null) {
            UserInfo userData = AppSharedData.getUserData();
            if (userData != null) {
                if (userData.getUserType().equalsIgnoreCase(TYPE_USER)) {
                    ToolUtils.goToMainUser(this, FROM_SPLASH);
                } else {
                    ToolUtils.goToTeacherMainActivity(this, FROM_SPLASH);
                }

            } else {
                ToolUtils.navigateToLogin(this);
            }
        } else {
            ToolUtils.navigateToLogin(this);
        }
    }
}