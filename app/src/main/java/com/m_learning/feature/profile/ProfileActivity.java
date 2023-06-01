package com.m_learning.feature.profile;

import static com.m_learning.utils.ConstantApp.TYPE_LECTURER;
import static com.m_learning.utils.ConstantApp.TYPE_USER;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.USER_INFO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.m_learning.databinding.ActivityProfileBinding;
import com.m_learning.network.feature.User;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ToolUtils;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserInfo userInfo;

    public static Intent newInstance(Activity mActivity, UserInfo userData, int fromWhere) {
        Intent intent = new Intent(mActivity, ProfileActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        intent.putExtra(USER_INFO, userData);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        userInfo = (UserInfo) getIntent().getSerializableExtra(USER_INFO);
//        if (userInfo.getUserType().equals(TYPE_USER)) {
//            binding.edit.setVisibility(View.VISIBLE);
//        } else binding.edit.setVisibility(View.GONE);
        setInfoForUser();
        User.getInstance().startGetUserData(AppSharedData.getUserData().getUserId(), new RequestListener<UserInfo>() {

            @Override
            public void onSuccess(UserInfo data) {
                AppSharedData.setUserData(data);
                userInfo = data;
                setInfoForUser();

            }

            @Override
            public void onFail(String message) {

            }
        });

        binding.ibtnBack.setOnClickListener(view1 -> finish());

        binding.edit.setOnClickListener(view1 -> startActivity(EditProfileActivity.newInstance(this, userInfo)));


    }

    private void setInfoForUser() {
        if (userInfo.getUserType().equals(TYPE_LECTURER)) {
            binding.edFirstName.setText(userInfo.getShortName());
            binding.edMiddleName.setVisibility(View.GONE);
            binding.edLastName.setVisibility(View.GONE);
            binding.edBirthDate.setVisibility(View.GONE);
        } else {
            binding.edMiddleName.setVisibility(View.VISIBLE);
            binding.edLastName.setVisibility(View.VISIBLE);
            binding.edBirthDate.setVisibility(View.VISIBLE);
            binding.edFirstName.setText(userInfo.getFirstName());
            binding.edMiddleName.setText(userInfo.getMiddleName());
            binding.edLastName.setText(userInfo.getLastName());
            binding.edBirthDate.setText(ToolUtils.convertMillisecondToDate(userInfo.getDob()));
        }
        binding.edAddress.setText(userInfo.getAddress());
        binding.edEmail.setText(userInfo.getEmail());
        binding.edMobileNo.setText(userInfo.getMobile());
        ToolUtils.setRoundImg(this, userInfo.getUserImage(), binding.ivAvatar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userInfo != null) User.getInstance().stopGetUserData(userInfo.getUserId());

    }
}