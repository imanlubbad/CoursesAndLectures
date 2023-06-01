package com.m_learning.feature.lectures;

import static com.m_learning.utils.ConstantApp.COURSE_INFO;
import static com.m_learning.utils.ConstantApp.FROM_STUDENTS;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.m_learning.R;
import com.m_learning.databinding.ActivityLecturesBinding;
import com.m_learning.feature.lecture.LectureFragment;
import com.m_learning.network.model.Course;

public class LecturesActivity extends AppCompatActivity {
    private ActivityLecturesBinding binding;
    private Course course;
    private int fromwhere;
    private String lectureId;
    private String courseId;
    Fragment fragment = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static Intent newInstance(Activity mActivity, Course course, int fromWhere) {
        Intent intent = new Intent(mActivity, LecturesActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        intent.putExtra(COURSE_INFO, course);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLecturesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        course = (Course) getIntent().getSerializableExtra(COURSE_INFO);
        courseId = course.getCourseId();
        fromwhere = getIntent().getIntExtra(FROM_WHERE, FROM_STUDENTS);
        binding.tvTitle.setText(course.getCourseName());
        binding.ibtnBack.setOnClickListener(view1 -> finish());
        fragment = new LectureFragment().newInstance(courseId, fromwhere);
        replaceFragment(fragment);

    }

    private void replaceFragment(Fragment fragment) {

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

}