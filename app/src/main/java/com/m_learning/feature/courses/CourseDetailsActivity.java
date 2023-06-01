package com.m_learning.feature.courses;

import static com.m_learning.utils.ConstantApp.ACTION_CLOSE;
import static com.m_learning.utils.ConstantApp.COURSE_INFO;
import static com.m_learning.utils.ConstantApp.FROM_MY_COURSES;
import static com.m_learning.utils.ConstantApp.FROM_STUDENTS;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.m_learning.R;
import com.m_learning.databinding.ActivityCourseDetailsBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.feature.lectures.AddAssignmentActivity;
import com.m_learning.feature.lectures.LecturesActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Assignments;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;

public class CourseDetailsActivity extends BaseActivity {

    private ActivityCourseDetailsBinding binding;
    private Course course;
    private String courseId;
    private int fromWhere;
    private LinearLayout llImgViews;
    private UserInfo userInfo;
    private Boolean isRegisteredInCourse = false;
    private Boolean haveAssignment = false;

    public static Intent newInstance(Activity mActivity, Course course, String courseId, int fromWhere) {
        Intent intent = new Intent(mActivity, CourseDetailsActivity.class);
        intent.putExtra(FROM_WHERE, fromWhere);
        intent.putExtra(COURSE_INFO, course);
        intent.putExtra("courseId", courseId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        llImgViews = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llImgViews.setOrientation(LinearLayout.HORIZONTAL);
        llImgViews.setLayoutParams(linearParams);
        binding.hScrollView.addView(llImgViews);

        userInfo = AppSharedData.getUserData();

        course = (Course) getIntent().getSerializableExtra(COURSE_INFO);
        fromWhere = getIntent().getIntExtra(FROM_WHERE, FROM_MY_COURSES);
        courseId = getIntent().getStringExtra("courseId");
        if (course != null) setCourseData();
        getCourseDetails(courseId);

        checkIfIRegisterInCourse();
        checkIfSubmitAssignment();

        binding.llAddAssignment.setOnClickListener(view1 -> addAssignment());
        binding.ibtnBack.setOnClickListener(view1 -> finish());
        binding.btnRegisterView.setOnClickListener(view1 -> {
            if (fromWhere == (FROM_MY_COURSES) || isRegisteredInCourse) {
                startActivity(LecturesActivity.newInstance(this, course, FROM_STUDENTS));
            } else {
                if (AppSharedData.getUserData().getCourseNo() < 5) registerInCourse();
                else showErrorMessage(getString(R.string.error_register_course));
            }
        });

    }

    private void checkIfIRegisterInCourse() {
        CoursesAndLectures.getInstance().checkIfIRegisterInCourse(courseId, userInfo.getUserId(), new RequestListener<Boolean>() {
            @Override
            public void onSuccess(Boolean isExist) {
                isRegisteredInCourse = isExist;
                if (isExist) {
                    binding.btnRegisterView.setVisibility(View.VISIBLE);
                    binding.btnRegisterView.setText(getString(R.string.view_course));
                } else {
                    binding.btnRegisterView.setVisibility(View.VISIBLE);
                    binding.btnRegisterView.setText(getString(R.string.register_for_the_course));
                    binding.llAddAssignment.setVisibility(View.GONE);
                    binding.llMyAssignment.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    private void checkIfSubmitAssignment() {
        CoursesAndLectures.getInstance().checkIfSubmitAssignment(courseId, userInfo.getUserId(), new RequestListener<Assignments>() {
            @Override
            public void onSuccess(Assignments assignments) {
                haveAssignment = (assignments != null);
                if (haveAssignment) {
                    binding.llMyAssignment.setVisibility(View.VISIBLE);
                    binding.llAddAssignment.setVisibility(View.GONE);
                    setAssignmentView(assignments);
                } else {
                    binding.llMyAssignment.setVisibility(View.GONE);
                    if (isRegisteredInCourse) {
                        binding.llAddAssignment.setVisibility(View.VISIBLE);
                    } else binding.llAddAssignment.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setAssignmentView(Assignments assignments) {
        binding.frame.setVisibility(View.VISIBLE);
        binding.tvFileName.setText(assignments.getFileName());
        String file = assignments.getFile();
        if (file.contains(".doc") || file.contains(".docx")) {
            Glide.with(this).load(getResources().getDrawable(R.drawable.word)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".pdf")) {
            Glide.with(this).load(getResources().getDrawable(R.drawable.pdf)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".xls") || file.toString().contains(".xlsx")) {
            Glide.with(this).load(getResources().getDrawable(R.drawable.excel)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".ppt") || file.toString().contains(".pptx")) {
            Glide.with(this).load(getResources().getDrawable(R.drawable.powerpoint)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".txt")) {
            Glide.with(this).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".zip") || file.toString().contains(".rar")) {
            Glide.with(this).load(getResources().getDrawable(R.drawable.rar)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else
            Glide.with(this).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        binding.frame.setOnClickListener(v -> {
            ToolUtils.openFileUrl(this, file);
        });


    }

    private void addAssignment() {
        startActivity(AddAssignmentActivity.newInstance(this, course));
    }

    private void addCourseView() {
        UserInfo user = AppSharedData.getUserData();
        if (user != null)
            CoursesAndLectures.getInstance().checkIfViewCourse(course, user);

    }

    private void registerInCourse() {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().addStudentCourse(course, new RequestListener<Course>() {
                @Override
                public void onSuccess(Course data) {
                    hideProgress();
                    binding.llAddAssignment.setVisibility(View.VISIBLE);

//                    binding.btnRegisterView.setVisibility(View.VISIBLE);
//                    binding.btnRegisterView.setText(getString(R.string.view_course));
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorMessage(message);
                }
            });
        } else
            showErrorMessage(getString(R.string.noInternetConnection));

    }

    private void getCourseDetails(String courseId) {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().startCourseDetailsListener(courseId, new RequestListener<Course>() {
                @Override
                public void onSuccess(Course data) {
                    hideProgress();
                    course = data;

                    setCourseData();
                    updateOtherTables(data);
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

    private void updateOtherTables(Course course) {
        CoursesAndLectures.getInstance().updateCourseViewsNo(course);
        CoursesAndLectures.getInstance().editCourseForAll(course);
    }

    private void setCourseData() {
        binding.tvName.setText(course.getCourseName());
        binding.tvDescription.setText(course.getCourseDescriptions());
        binding.tvLecturerName.setText(course.getLecturerName());

        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvViewNo.setText(String.format(getString(R.string.number), String.valueOf(course.getViewsNo())));
        binding.tvStudentNo.setText(String.format(getString(R.string.number), String.valueOf(course.getStudentsNo())));
        String deadline = String.valueOf(ToolUtils.convertMillisecondToDate(course.getCourseDeadline()));
        binding.tvDeadline.setText(String.format(getString(R.string.deadline_format), deadline));
        ToolUtils.setImgProgress(this, course.getCourseImage(), binding.imgProduct, binding.progressCircular);

        if (haveAssignment) {
            binding.llAddAssignment.setVisibility(View.GONE);
            binding.llMyAssignment.setVisibility(View.VISIBLE);
        } else if (isRegisteredInCourse && ToolUtils.checkADayAfterEnd(deadline)) {
            binding.llAddAssignment.setVisibility(View.VISIBLE);
            binding.llMyAssignment.setVisibility(View.GONE);
        } else {
            binding.llAddAssignment.setVisibility(View.GONE);
        }


        if (course.getCourseFiles() != null && !course.getCourseFiles().

                isEmpty()) {
            binding.llCourseDocument.setVisibility(View.VISIBLE);
            addImage(course.getCourseFiles());
        } else
            binding.llCourseDocument.setVisibility(View.GONE);

        addCourseView();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addImage(ArrayList<String> files) {
        LayoutInflater inflater = LayoutInflater.from(this);
        llImgViews.removeAllViews();
//        if (imgPaths == null) {
        ArrayList<String> imgPaths = new ArrayList<>();
        for (String file : files) {
            if (file != null) {
                imgPaths.add(file);
            }
        }

        for (int i = 0; i < imgPaths.size(); i++) {
            Log.e("image: ", imgPaths.get(i) + " //");
            String file = imgPaths.get(i);
            View view = inflater.inflate(R.layout.item_image, llImgViews, false);
            ImageView img = view.findViewById(R.id.img_add);
            ImageView img_upload = view.findViewById(R.id.img_upload);
            ImageView imgClose = view.findViewById(R.id.img_delete);
            FrameLayout frame = view.findViewById(R.id.frame);
            frame.setTag(file);
            img_upload.setVisibility(View.VISIBLE);


            imgClose.setVisibility(View.GONE);
            int finalI = i;
            view.setOnClickListener(v -> {
                ToolUtils.openFileUrl(this, imgPaths.get(finalI));
            });

            if (file.contains(".doc") || file.toString().contains(".docx")) {
                Glide.with(this).load(getResources().getDrawable(R.drawable.word)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.contains(".pdf")) {
                Glide.with(this).load(getResources().getDrawable(R.drawable.pdf)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.contains(".xls") || file.toString().contains(".xlsx")) {
                Glide.with(this).load(getResources().getDrawable(R.drawable.excel)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.contains(".ppt") || file.toString().contains(".pptx")) {
                Glide.with(this).load(getResources().getDrawable(R.drawable.powerpoint)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.contains(".txt")) {
                Glide.with(this).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.contains(".zip") || file.toString().contains(".rar")) {
                Glide.with(this).load(getResources().getDrawable(R.drawable.rar)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else
                Glide.with(this).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);

            llImgViews.addView(view);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoursesAndLectures.getInstance().stopCourseDetailsListener(courseId);
        CoursesAndLectures.getInstance().stopIfIRegisterInCourse(courseId, userInfo.getUserId());
        CoursesAndLectures.getInstance().stopCheckIfSubmitAssignment(courseId, userInfo.getUserId());
    }

    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);
    }
}