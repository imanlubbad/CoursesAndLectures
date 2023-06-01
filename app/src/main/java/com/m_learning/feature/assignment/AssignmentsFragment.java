package com.m_learning.feature.assignment;

import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.m_learning.R;
import com.m_learning.databinding.FragmentAssignmentsBinding;
import com.m_learning.feature.baseView.BaseFragment;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.utils.OnSearchListener;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Assignments;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;

import java.util.ArrayList;


public class AssignmentsFragment extends BaseFragment implements OnSearchListener, BaseView, AssignmentsAdapter.OnItemClickListener {

    private AssignmentsAdapter mAdapter;
    private FragmentAssignmentsBinding binding;
    private ArrayList<Assignments> list = new ArrayList<>();
    private ArrayList<Assignments> searchList = new ArrayList<>();
    private int fromWhere;
    private String courseId;


    public AssignmentsFragment() {
        // Required empty public constructor
    }

    public AssignmentsFragment newInstance(String courseId, int fromWhere) {
        AssignmentsFragment fragment = new AssignmentsFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putInt(FROM_WHERE, fromWhere);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentAssignmentsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        courseId = getArguments().getString("courseId");
        fromWhere = getArguments().getInt(FROM_WHERE);
        initAdapter();
        getList();

        return view;
    }

    @Override
    public void onSearch(String search) {
        searchList = new ArrayList<>();
        if (search.length() > 0) {
            for (Assignments item : list) {
                if (item.getTitle().toLowerCase().contains(search.toLowerCase())
                        || (item.getCourseName().toLowerCase().contains(search.toLowerCase())
                        || item.getStudentName().toLowerCase().contains(search.toLowerCase()))) {
                    searchList.add(item);
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
        CoursesAndLectures.getInstance().startCourseAssignmentListListener(courseId, new RequestListener<ArrayList<Assignments>>() {
            @Override
            public void onSuccess(ArrayList<Assignments> data) {
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

    private void showMyList(ArrayList<Assignments> data) {
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
        mAdapter = new AssignmentsAdapter(getActivity(), fromWhere, this);
        binding.rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvList.setItemAnimator(new DefaultItemAnimator());
        binding.rvList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(Assignments obj, int positions) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CoursesAndLectures.getInstance().stopCourseAssignmentListListener(courseId);

    }

}