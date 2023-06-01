package com.m_learning.feature.lecture;

import static com.m_learning.utils.ConstantApp.ACTION_CLOSE;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.LECTURE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.m_learning.R;
import com.m_learning.databinding.ActivityLectureDetailsBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Lecture;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.ToolUtils;

public class LectureDetailsActivity extends BaseActivity {

    private ActivityLectureDetailsBinding binding;
    private Lecture Lecture;
    private String fromwhere;
    private String lectureId;
    private String courseId;

    public static Intent newInstance(Activity mActivity, Lecture lecture, int fromWhere) {
        Intent intent = new Intent(mActivity, LectureDetailsActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        intent.putExtra(LECTURE, lecture);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLectureDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Lecture = (Lecture) getIntent().getSerializableExtra(LECTURE);
        lectureId = Lecture.getId();
        courseId = Lecture.getCourseId();
        fromwhere = getIntent().getStringExtra(FROM_WHERE);
        setLectureData();
        getLectureDetails();

    }

    private void getLectureDetails() {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().startLectureDetailsListener(courseId, lectureId, new RequestListener<Lecture>() {
                @Override
                public void onSuccess(Lecture data) {
                    hideProgress();
                    Lecture = data;
                    setLectureData();
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorDialog(message, getString(R.string.ok), "", ACTION_CLOSE);
                }
            });
        } else
            showErrorDialog(getString(R.string.noInternetConnection), getString(R.string.ok), "", ACTION_CLOSE);

    }

    private void setLectureData() {
        binding.tvName.setText(Lecture.getTitle());
        binding.tvDescription.setText(Lecture.getDescription());
        binding.tvLecturerName.setText(Lecture.getLecturerName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoursesAndLectures.getInstance().stopLectureDetailsListener(courseId, lectureId);
    }
}