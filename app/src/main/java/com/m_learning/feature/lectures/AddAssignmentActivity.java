package com.m_learning.feature.lectures;

import static com.m_learning.utils.ConstantApp.COURSE_INFO;
import static com.m_learning.utils.ConstantApp.PICKFILE_RESULT_CODE;
import static com.m_learning.utils.ConstantApp.REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.m_learning.R;
import com.m_learning.databinding.ActivityAddAssignmentBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Assignments;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ImageCompress;
import com.m_learning.utils.ToolUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddAssignmentActivity extends BaseActivity {
    private ImageCompress imageCompress;

    private ActivityAddAssignmentBinding binding;
    private Activity mActivity;
    private String assignmentFileUrl;
    private String assignmentFileName;
    private String courseId;
    private File assignmentFile;
    private Course course;

    public static Intent newInstance(Activity mActivity, Course course) {
        Intent intent = new Intent(mActivity, AddAssignmentActivity.class);
        intent.putExtra(COURSE_INFO, course);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssignmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mActivity = this;
        course = (Course) getIntent().getSerializableExtra(COURSE_INFO);
        courseId = course.getCourseId();
        imageCompress = new ImageCompress(mActivity);
        initListener();
    }


    private void initListener() {
        binding.llAddImage.setOnClickListener(view -> checkPermissions());
        binding.btnSend.setOnClickListener(view -> validateInputs());
        binding.ibtnBack.setOnClickListener(view -> finish());
    }

    private void validateInputs() {
        binding.edName.setError(null);
        if (assignmentFile == null) {
            showErrorMessage(getString(R.string.hint_must_file));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edName)) {
            binding.edName.setError(getString(R.string.is_required));
            return;
        }
        uploadFileToStorage();


    }

    private void uploadFileToStorage() {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().saveAssignmentFileToFirestore(assignmentFile.getName(), assignmentFile, new StringRequestListener<Uri>() {
                @Override
                public void onSuccess(String type, Uri data) {
                    Log.e("file url: ", type + " " + data.toString() + " ");
                    assignmentFileUrl = (data.toString());
                    addAssignment();

                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorMessage(message);
                }
            });
        } else showErrorMessage(getString(R.string.noInternetConnection));


    }


    private void addAssignment() {
        UserInfo userInfo = AppSharedData.getUserData();
        Assignments assignment = new Assignments();
        String assignmentId = UUID.randomUUID().toString();
        assignment.setIsActive(1);
        assignment.setDeleted(false);
        assignment.setCreatedAt(ToolUtils.getCurrentDateTimeLong());
        assignment.setCourseId(courseId);
        assignment.setId(assignmentId);
        assignment.setFile(assignmentFileUrl);
        assignment.setFileName(assignmentFileName);
        assignment.setTitle(binding.edName.getText().toString().trim());
        assignment.setStudentId(userInfo.getUserId());
        assignment.setStudentName(userInfo.getShortName());

        CoursesAndLectures.getInstance().addCourseAssignmentInDataBase(assignment,course.getLecturerId(), new StringRequestListener<Assignments>() {
            @Override
            public void onSuccess(String message, Assignments data) {
                hideProgress();
                showErrorMessage(message);
                finish();

            }

            @Override
            public void onFail(String message) {
                hideProgress();
                showErrorMessage(message);
            }
        });
    }

    public void checkPermissions() {
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> permissionsToRequest = new ArrayList<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA);
            return;
        }

        chooseFile();
    }

    private void chooseFile() {

        String[] mimeTypes = {
                "application/pdf",
                "application/zip",
                "application/vnd.ms-powerpoint",
                "application/vnd.ms-excel",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain"
        };

        //File Chooser
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICKFILE_RESULT_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && data != null) {
            Uri selectedImageUri = data.getData();

            Log.e("Check path: ", selectedImageUri.toString());
            Log.e("Check path: ", selectedImageUri.getPath());
            File fl = new File(selectedImageUri.getPath());
            String path = fl.getAbsolutePath();
            String extension = imageCompress.getRealPathFromURI1(selectedImageUri);
            if (extension != null)
                extension = extension.substring(extension.indexOf("."), extension.length());
            Log.e("Check name: ", fl.getName() + ". " + extension);
            InputStream in = null;
            try {
                String fileType = extension;
                in = getContentResolver().openInputStream(selectedImageUri);
                File file = imageCompress.createFileFromInputStream(in, String.valueOf(System.currentTimeMillis()) + fileType);
                if (file != null) {
                    Log.e("Check path: ", file.toString() + "  88");
                    assignmentFile = file;
                    assignmentFileName = file.getName();
                    addImage(file.toString());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Check path: ", e.getMessage() + "  77");

            }

        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addImage(String file) {
        binding.frame.setVisibility(View.VISIBLE);
        binding.tvFileName.setText(assignmentFileName);
        if (file.contains(".doc") || file.toString().contains(".docx")) {
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.word)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".pdf")) {
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.pdf)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".xls") || file.toString().contains(".xlsx")) {
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.excel)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".ppt") || file.toString().contains(".pptx")) {
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.powerpoint)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".txt")) {
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else if (file.contains(".zip") || file.toString().contains(".rar")) {
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.rar)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);
        } else
            Glide.with(mActivity).load(getResources().getDrawable(R.drawable.google_docs)).apply(RequestOptions.placeholderOf(R.drawable.nophotos)).into(binding.img);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showErrorMessage(getString(R.string.permission_not_granted_error));
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);

    }
}