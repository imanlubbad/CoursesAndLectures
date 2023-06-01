package com.m_learning.feature.notifications;

import static com.m_learning.utils.ConstantApp.FROM_MY_COURSES;
import static com.m_learning.utils.ConstantApp.FROM_NOTIFICATION_LIST;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_ASSIGNMENT;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_REGISTER;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_VIEW;
import static com.m_learning.utils.ConstantApp.TYPE_DELETE_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_EDIT_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_VIEW_LECTURE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m_learning.R;
import com.m_learning.databinding.ActivityNotificationsBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.feature.courses.CourseDetailsActivity;
import com.m_learning.feature.lecturerHome.LecturerMainActivity;
import com.m_learning.network.feature.NotificationsRequests;
import com.m_learning.network.model.Notifications;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;

import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity implements BaseView, NotificationsAdapter.OnItemClickListener {
    private NotificationsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ActivityNotificationsBinding binding;
    private NotificationsActivity mActivity;

    public static Intent newInstance(Context mActivity, int fromWhere) {
        Intent intent = new Intent(mActivity, NotificationsActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mActivity = this;
        initAdapter();
        getMyNotifications();
        binding.ibBack.setOnClickListener(v -> onBackPressed());


    }

    private void getMyNotifications() {
        NotificationsRequests.getInstance().startNotificationsDataListener(new RequestListener<ArrayList<Notifications>>() {
            @Override
            public void onSuccess(ArrayList<Notifications> data) {
                hideProgress();
                showMyNotifications(data);

            }

            @Override
            public void onFail(String message) {
                hideProgress();
                if (message.equalsIgnoreCase(mActivity.getString(R.string.no_data_found))) {
                    hideListAndShowEmpty();
                } else {
                    showErrorMessage(message);

                }
            }
        });

    }

    private void initAdapter() {
        mAdapter = new NotificationsAdapter(this, this);
        mLayoutManager = new LinearLayoutManager(this);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.setItemAnimator(new DefaultItemAnimator());
        binding.rvList.setAdapter(mAdapter);
    }


    @Override
    public void onItemClick(Notifications item, int positions) {
        Intent resultIntent;
        String type = item.getType();
        switch (type) {
            case TYPE_COURSE_REGISTER:
            case TYPE_COURSE_VIEW:
            case TYPE_VIEW_LECTURE:
            case TYPE_ADD_ASSIGNMENT:
                resultIntent = new Intent(this, LecturerMainActivity.class);
                resultIntent.putExtra(FROM_WHERE, FROM_NOTIFICATION_LIST);
                resultIntent.setAction(type);
                startActivity(resultIntent);
                break;
            case TYPE_ADD_LECTURE:
            case TYPE_EDIT_LECTURE:
            case TYPE_DELETE_LECTURE:
                startActivity(CourseDetailsActivity.newInstance(this, null, item.getEventId(), FROM_MY_COURSES));
                break;
        }
    }

    public void showMyNotifications(ArrayList<Notifications> data) {
        binding.rvList.setVisibility(View.VISIBLE);
        binding.llNoResult.setVisibility(View.GONE);
        mAdapter.removeAll();
        mAdapter.addItem(data);
    }


    public void hideListAndShowEmpty() {
        binding.rvList.setVisibility(View.GONE);
        binding.llNoResult.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.rootView, message);
    }
}