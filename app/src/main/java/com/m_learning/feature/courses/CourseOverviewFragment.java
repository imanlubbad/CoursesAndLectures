package com.m_learning.feature.courses;

import static com.m_learning.utils.ConstantApp.ACTION_CLOSE;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.m_learning.databinding.FragmentCourseOverviewBinding;
import com.m_learning.feature.baseView.BaseFragment;
import com.m_learning.feature.baseView.BaseView;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Course;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.ToolUtils;

import java.util.ArrayList;


public class CourseOverviewFragment extends BaseFragment implements BaseView {


    private FragmentCourseOverviewBinding binding;
    private Course course;
    private String courseId;
    private LinearLayout llImgViews;
    private Activity mActivity;

    public CourseOverviewFragment() {
        // Required empty public constructor
    }

    public CourseOverviewFragment newInstance(Course course, String courseId) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putSerializable("course", course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentCourseOverviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        if (getArguments() != null) {
            course = (Course) getArguments().getSerializable("course");
            courseId = getArguments().getString("courseId");
        }
        mActivity = getActivity();
        llImgViews = new LinearLayout(getActivity());
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llImgViews.setOrientation(LinearLayout.HORIZONTAL);
        llImgViews.setLayoutParams(linearParams);
        binding.hScrollView.addView(llImgViews);

        setCourseData();
        getCourseDetails(course.getCourseId());


        return view;
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

    private void setCourseData() {
        binding.tvName.setText(course.getCourseName());
        binding.tvDescription.setText(course.getCourseDescriptions());
        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvViews.setText(String.format(getString(R.string.number), String.valueOf(course.getViewsNo())));
        binding.tvStudentsCount.setText(String.format(getString(R.string.number), String.valueOf(course.getStudentsNo())));
        binding.tvDeadline.setText(ToolUtils.convertMillisecondToDate(course.getCourseDeadline()));
        ToolUtils.setImgProgress(getActivity(), course.getCourseImage(), binding.imgProduct, binding.progressCircular);

        if (course.getCourseFiles() != null && !course.getCourseFiles().isEmpty()) {
            binding.tvCourseDoc.setVisibility(View.VISIBLE);
            addImage(course.getCourseFiles());
        } else
            binding.tvCourseDoc.setVisibility(View.GONE);

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void addImage(ArrayList<String> files) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
//                openFile(new File(imgPaths.get(finalI)));
//                openFile(imgPaths.get(finalI));
                ToolUtils.openFileUrl(mActivity, imgPaths.get(finalI));
            });

            if (file.contains(".doc") || file.toString().contains(".docx")) {
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.word)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.toString().contains(".pdf")) {
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.pdf)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.excel)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.powerpoint)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.toString().contains(".txt")) {
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.rar)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);
            } else
                Glide.with(mActivity).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(img);

//            img_upload.setOnClickListener(view1 -> downloadFile(file));
            llImgViews.addView(view);

        }
    }

}