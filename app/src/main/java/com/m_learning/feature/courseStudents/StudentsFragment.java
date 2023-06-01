package com.m_learning.feature.courseStudents;

import static com.m_learning.utils.ConstantApp.FROM_OTHERS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m_learning.R;
import com.m_learning.databinding.FragmentStudentsBinding;
import com.m_learning.feature.Chat.view.ChatActivity;
import com.m_learning.feature.baseView.BaseFragment;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.utils.OnSearchListener;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;

import java.util.ArrayList;

public class StudentsFragment extends BaseFragment implements OnSearchListener, BaseView, StudentsAdapter.OnItemClickListener {

    private StudentsAdapter mAdapter;
    private FragmentStudentsBinding binding;
    private ArrayList<UserInfo> list = new ArrayList<>();
    private ArrayList<UserInfo> searchList = new ArrayList<>();
    private String courseId;


    public StudentsFragment() {
        // Required empty public constructor
    }

    public StudentsFragment newInstance(String courseId) {
        StudentsFragment fragment = new StudentsFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentStudentsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
        }
        initAdapter();
        getList();

        return view;
    }

    @Override
    public void onSearch(String search) {
        searchList = new ArrayList<>();
        if (search.length() > 0) {
            for (UserInfo course : list) {
                if (course.getFullName().toLowerCase().contains(search.toLowerCase())) {
                    searchList.add(course);
                }
            }
            mAdapter.removeAll();
            mAdapter.addItem(searchList);
        } else {
            mAdapter.removeAll();
            mAdapter.addItem(list);

        }
    }

    private void getList() {
        CoursesAndLectures.getInstance().startCourseStudentsListListener(courseId, new RequestListener<ArrayList<UserInfo>>() {
            @Override
            public void onSuccess(ArrayList<UserInfo> data) {
                hideProgress();
                showMyList(data);
            }

            @Override
            public void onFail(String message) {
                hideProgress();
                if (message.equalsIgnoreCase(getString(R.string.no_data_found))) {
                    hideListAndShowEmpty();
                } else {
                    showErrorMessage(message);

                }
            }
        });

    }

    @Override
    public void hideProgress() {
        binding.progressCircular.setVisibility(View.GONE);

    }

    @Override
    public void showProgress() {
        binding.progressCircular.setVisibility(View.VISIBLE);

    }

    private void showMyList(ArrayList<UserInfo> data) {
        this.list = data;
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
        AppShareMethods.showSnackBar(getActivity(), binding.root, message);
    }

    private void initAdapter() {
        mAdapter = new StudentsAdapter(getActivity(), this);
        binding.rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvList.setItemAnimator(new DefaultItemAnimator());
        binding.rvList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(UserInfo obj, int positions) {
       startActivity(ChatActivity.getIntent(getActivity(),"", obj.getUserId(), FROM_OTHERS));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CoursesAndLectures.getInstance().stopCourseStudentsListListener(courseId);

    }

}