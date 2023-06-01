package com.m_learning.feature.courses;

import static com.m_learning.utils.ConstantApp.FROM_MY_COURSES;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m_learning.R;
import com.m_learning.databinding.FragmentMyCoursesBinding;
import com.m_learning.feature.baseView.BaseFragment;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.OnSearchListener;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;


public class MyCoursesFragment
        extends BaseFragment implements OnSearchListener, BaseView, CourseAdapter.OnItemClickListener {

    private String textForSearch = "";
    private CourseAdapter mAdapter;
    private FragmentMyCoursesBinding binding;
    private ArrayList<Course> list = new ArrayList<>();
    private ArrayList<Course> searchList = new ArrayList<>();

    public MyCoursesFragment() {
    }


    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentMyCoursesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        initAdapter();
        getList();

        return view;
    }

    @Override
    public void onSearch(String search) {
        textForSearch = search;
        searchList = new ArrayList<>();
        if (search.length() > 0) {
            for (Course course : list) {
                if (course.getCourseName().toLowerCase().contains(search.toLowerCase())) {
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
        if (ToolUtils.isNetworkConnected()) {




            showProgress();
            CoursesAndLectures.getInstance().startCoursesListListener(new RequestListener<ArrayList<Course>>() {
                @Override
                public void onSuccess(ArrayList<Course> data) {
                    showMyList(data);
                    UserInfo user = AppSharedData.getUserData();
                    user.setMyCourses(data);
                    AppSharedData.setUserData(user);
                    hideProgress();
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    if (message.equalsIgnoreCase(getString(R.string.no_messages))) {
                        hideListAndShowEmpty();
                    } else {
                        showErrorMessage(message);

                    }
                }
            });

        } else showErrorMessage(getString(R.string.noInternetConnection));
    }

    @Override
    public void hideProgress() {
        binding.progressCircular.setVisibility(View.GONE);

    }

    @Override
    public void showProgress() {
        binding.progressCircular.setVisibility(View.VISIBLE);

    }

    private void showMyList(ArrayList<Course> data) {
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
        mAdapter = new CourseAdapter(getActivity(), this);
        binding.rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvList.setItemAnimator(new DefaultItemAnimator());
        binding.rvList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(Course obj, int positions) {
        startActivity(CourseDetailsActivity.newInstance(getActivity(), obj,obj.getCourseId(), FROM_MY_COURSES));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CoursesAndLectures.getInstance().stopCoursesListListener();

    }
}