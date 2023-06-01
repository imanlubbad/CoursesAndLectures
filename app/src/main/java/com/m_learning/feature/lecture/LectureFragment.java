package com.m_learning.feature.lecture;

import static com.m_learning.utils.ConstantApp.ACTION_ADD;
import static com.m_learning.utils.ConstantApp.ACTION_DELETE;
import static com.m_learning.utils.ConstantApp.ACTION_EDIT;
import static com.m_learning.utils.ConstantApp.FROM_STUDENTS;
import static com.m_learning.utils.ConstantApp.TYPE_VIEW_LECTURE;
import static com.m_learning.utils.ConstantApp.VIDEO_TYPE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.m_learning.R;
import com.m_learning.databinding.FragmentLectureBinding;
import com.m_learning.feature.baseView.BaseFragment;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.feature.general.ImageFullScreenActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Lecture;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.FirebaseReferance;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.DialogUtils;
import com.m_learning.utils.MediaData;
import com.m_learning.utils.OnSearchListener;

import java.util.ArrayList;

public class LectureFragment extends BaseFragment implements OnSearchListener, BaseView, LecturesAdapter.OnItemClickListener {

    private LecturesAdapter mAdapter;
    private FragmentLectureBinding binding;
    private ArrayList<Lecture> list = new ArrayList<>();
    private ArrayList<Lecture> searchList = new ArrayList<>();
    private String courseId = "";
    private int fromWhere;
    private UserInfo userInfo;


    public LectureFragment() {
        // Required empty public constructor
    }

    public LectureFragment newInstance(String courseId, int fromWhere) {
        LectureFragment fragment = new LectureFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putInt("fromWhere", fromWhere);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentLectureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
            fromWhere = getArguments().getInt("fromWhere");
        }
        userInfo = AppSharedData.getUserData();
        initAdapter();
        getList();
        if (fromWhere == FROM_STUDENTS)
            binding.fbAddLecture.setVisibility(View.GONE);
        else binding.fbAddLecture.setVisibility(View.VISIBLE);

        binding.fbAddLecture.setOnClickListener(view1 -> startActivity(AddLectureActivity.newInstance(getActivity(), ACTION_ADD, courseId, null)));

        return view;
    }

    @Override
    public void onSearch(String search) {
        searchList = new ArrayList<>();
        if (search.length() > 0) {
            for (Lecture item : list) {
                if (item.getTitle().toLowerCase().contains(search.toLowerCase())) {
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
        CoursesAndLectures.getInstance().startCourseLecturesListListener(courseId, new RequestListener<ArrayList<Lecture>>() {
            @Override
            public void onSuccess(ArrayList<Lecture> data) {
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

    private void showMyList(ArrayList<Lecture> data) {
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
        mAdapter = new LecturesAdapter(getActivity(), this, fromWhere);
        binding.rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvList.setItemAnimator(new DefaultItemAnimator());
        binding.rvList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(Lecture obj, int positions) {
        if (fromWhere == FROM_STUDENTS) {
            checkIfViewPrevious(obj, positions);
        } else {
            ViewLecture(obj);
        }
    }

    @Override
    public void onDelete(Lecture item, int positions) {
        DialogUtils.showAlertDialogWithListener(getActivity(), getString(R.string.delete), getString(R.string.delete_lecture_msg), getString(R.string.ok), getString(R.string.Cancel), new DialogUtils.onClickListener() {
            @Override
            public void onOkClick() {
                FirebaseReferance.getCourseLecturesReference(item.getCourseId(), item.getId()).removeValue();
                FirebaseReferance.getLecturesViewsReference(item.getId()).removeValue();
                CoursesAndLectures.getInstance().sendNotificationToAllStudents(ACTION_DELETE, item);
            }

            @Override
            public void onCancelClick() {

            }
        });
    }

    @Override
    public void onEdit(Lecture item, int positions) {
        startActivity(AddLectureActivity.newInstance(getActivity(), ACTION_EDIT, item.getCourseId(), item));
    }

    private void checkIfViewPrevious(Lecture obj, int positions) {
        String message = String.format(getString(R.string.view_lecture), userInfo.getShortName(), obj.getTitle());
        list = mAdapter.getList();
        Log.e("test:", obj.getId());
        if (positions == 0) {
            ViewLecture(obj);
            if (userInfo != null) {
                FirebaseReferance.getSeenLecturesReference(userInfo.getUserId()).child(courseId).child(obj.getId()).push().setValue(obj);
                FirebaseReferance.getLecturesViewsReference(obj.getId()).child(userInfo.getUserId()).push().setValue(userInfo);
                CoursesAndLectures.getInstance().sendNotifications(TYPE_VIEW_LECTURE, obj.getId(), userInfo.getUserId(), userInfo.getShortName(), message, obj.getLecturerId());
            }
        } else {
            Lecture previous = mAdapter.getItem(positions - 1);
            FirebaseReferance.getSeenLecturesReference(userInfo.getUserId()).child(courseId).child(previous.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ViewLecture(obj);
                        FirebaseReferance.getSeenLecturesReference(userInfo.getUserId()).child(courseId).child(obj.getId()).push().setValue(obj);
                        FirebaseReferance.getLecturesViewsReference(obj.getId()).child(userInfo.getUserId()).push().setValue(userInfo);
                        CoursesAndLectures.getInstance().sendNotifications(TYPE_VIEW_LECTURE, obj.getId(), userInfo.getUserId(), userInfo.getShortName(), message, obj.getLecturerId());
                    } else {
                        showErrorMessage(getString(R.string.shouldSeePevious));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void ViewLecture(Lecture obj) {
        MediaData mediaDataObj = new MediaData(obj.getImage(), VIDEO_TYPE, obj.getLectureVideo());
        startActivity(ImageFullScreenActivity.newInstance(getActivity(), mediaDataObj, obj.getTitle()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CoursesAndLectures.getInstance().stopCourseLecturesListListener(courseId);

    }

}