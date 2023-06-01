package com.m_learning.feature.courses;

import static com.m_learning.utils.ConstantApp.ACTION_EDIT;
import static com.m_learning.utils.ConstantApp.COURSE_INFO;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.PICKFILE_RESULT_CODE;
import static com.m_learning.utils.ConstantApp.REQUEST_CODE_CLICK_IMAGE;
import static com.m_learning.utils.ConstantApp.REQUEST_IMAGE_CAPTURE;
import static com.m_learning.utils.ConstantApp.REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA;
import static com.m_learning.utils.ConstantApp.REQUEST_SINGLE_IMAGE_NEW;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.m_learning.R;
import com.m_learning.databinding.ActivityAddCourseBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Course;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ImageCompress;
import com.m_learning.utils.ItemSelectImageDialogFragment;
import com.m_learning.utils.ToolUtils;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddCourseActivity extends BaseActivity implements ImageCompress.onFinishedCompressListListener, ItemSelectImageDialogFragment.Listener, DatePickerDialog.OnDateSetListener {

    ActivityResultLauncher<Intent> resultLauncher;

    private ActivityAddCourseBinding binding;
    private AddCourseActivity mActivity;
    private ItemSelectImageDialogFragment mItemSelectImageDialogFragment;
    private File filePathImageCamera = null;
    private ImageCompress imageCompress;
    private Uri selectedImageUri;
    private long deadline;
    private LinearLayout llImgViews;
    private int from;
    private Course course = null;
    String courseId;
    int type = 0;
    private GalleryConfig.Build config;
    ArrayList<String> uploadedFileList = new ArrayList<String>();

    String courseImage = "";
    //    ArrayList<File> filesMapList = new ArrayList<File>();
    ArrayList<String> filesMap = new ArrayList<String>();
    private ArrayList<String> imgPaths = new ArrayList<>();
    private ArrayList<String> imgPathsUrl = new ArrayList<>();

    public static Intent newInstance(Activity mActivity, int from, Course course) {
        Intent intent = new Intent(mActivity, AddCourseActivity.class);
        intent.putExtra(FROM_WHERE, from);
        intent.putExtra(COURSE_INFO, course);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCourseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mActivity = this;
        course = (Course) getIntent().getSerializableExtra(COURSE_INFO);
        from = getIntent().getIntExtra(FROM_WHERE, 0);
        config = new GalleryConfig.Build()
                .limitPickPhoto(1)
                .singlePhoto(true)
                .filterMimeTypes(new String[]{"image/gif"});
        imageCompress = new ImageCompress(mActivity);
        initDialog();
        initListener();
        llImgViews = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llImgViews.setOrientation(LinearLayout.HORIZONTAL);
        llImgViews.setLayoutParams(linearParams);
        binding.hScrollView.addView(llImgViews);


        LayoutInflater inflater = LayoutInflater.from(this);
        View viewImage = inflater.inflate(R.layout.item_image, llImgViews, false);
        ImageView addImg = viewImage.findViewById(R.id.img_add);
        ImageView imgClose = viewImage.findViewById(R.id.img_delete);
        imgClose.setVisibility(View.INVISIBLE);
        llImgViews.addView(viewImage);
        addImg.setOnClickListener(v -> {
            checkPermissions(1);
        });
        if (from == ACTION_EDIT) {
            imgPaths = course.getCourseFiles();
            courseImage = course.getCourseImage();
            courseId = course.getCourseId();
            binding.edCourseCode.setText(course.getCourseCode());
            binding.edName.setText(course.getCourseName());
            binding.edDetails.setText(course.getCourseDescriptions());
            binding.edDeadline.setText(ToolUtils.convertMillisecondToDate(course.getCourseDeadline()));
            Glide.with(this).load((courseImage)).into(binding.imgProduct);
            binding.chbAssignment.setChecked(course.getHaveAssignment());
            if (imgPaths != null && !imgPaths.isEmpty()) {
                imgPathsUrl.addAll(imgPaths);
                uploadedFileList.addAll(imgPaths);
            }
            binding.btnAdd.setText(getString(R.string.save));
            binding.tvTitle.setText(getString(R.string.edit_course));
        }

        if (imgPaths != null && !imgPaths.isEmpty())
            addImage(imgPaths);


    }

    private void initListener() {
        binding.llAddImage.setOnClickListener(view -> checkPermissions(0));
        binding.edDeadline.setOnClickListener(view -> setDate());
        binding.btnAdd.setOnClickListener(view -> validateInputs());
        binding.ibtnBack.setOnClickListener(view -> finish());
    }

    private void validateInputs() {
        binding.edName.setError(null);
        binding.edDetails.setError(null);
        binding.edCourseCode.setError(null);
        binding.edDeadline.setError(null);
        if (filePathImageCamera == null && TextUtils.isEmpty(courseImage)) {
            showErrorMessage(getString(R.string.hint_must_select));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edName)) {
            binding.edName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edCourseCode)) {
            binding.edCourseCode.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edDetails)) {
            binding.edDetails.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edDeadline)) {
            binding.edDeadline.setError(getString(R.string.is_required));
            return;
        }
        ToolUtils.hideKeyboard(this);
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            if (filePathImageCamera != null)
                uploadImageToStorage();
            else if (filesMap != null && !filesMap.isEmpty()) {
                UploadFiles();
            } else addCourse();

        } else {
            showErrorMessage(getString(R.string.noInternetConnection));
        }
    }

    void setDate() {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .minDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
//                .minDate(1950, 0, 1)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        binding.edDeadline.setText(date);
        deadline = ToolUtils.convertDateToMillisecond(date);
    }

    public void checkPermissions(int type) {
        this.type = type;
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
        if (type == 0) showSelectImageDialog();
        else
            chooseFile();
    }

    private void showSelectImageDialog() {
        mItemSelectImageDialogFragment.show(getSupportFragmentManager(), ItemSelectImageDialogFragment.class.getSimpleName());
    }

    public void onGalleryClicked() {
        onGalleryClickedImplementation();
    }

    public void onCameraClicked() {
        onCameraClickedImplementation();
    }

    @Override
    public void onVideoClicked() {

    }

    private void onGalleryClickedImplementation() {
        int request;
        config.limitPickPhoto(1);

        config.hintOfPick(mActivity.getString(R.string.hint_must_select));
        request = REQUEST_SINGLE_IMAGE_NEW;
        GalleryActivity.openActivity(mActivity, request, config.build());
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


    private void onCameraClickedImplementation() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            filePathImageCamera = AppShareMethods.getNewImageFile(mActivity);
            Uri photoURI;
            int request = REQUEST_IMAGE_CAPTURE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                photoURI = FileProvider.getUriForFile(mActivity, mActivity.getString(R.string.provider), filePathImageCamera);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                photoURI = Uri.fromFile(filePathImageCamera);
                request = REQUEST_CODE_CLICK_IMAGE;
            }

            mActivity.startActivityForResult(takePictureIntent, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CLICK_IMAGE && resultCode == RESULT_OK
                && data != null) {
            List<String> photos = new ArrayList<>();
            if (data.getDataString() != null) {
                photos.add(data.getDataString());
                imageCompress.compressImages(true, photos, this);
            } else {
                Bundle bundle = data.getExtras();
                final Bitmap photo = (Bitmap) bundle.get("data");
                Uri tempUri = imageCompress.getImageUri(photo);
                String imagePath = imageCompress.getRealPathFromURI(tempUri);
                if (imagePath != null) {
                    photos.add(imagePath);
                    imageCompress.compressImages(true, photos, this);

                } else
                    showErrorMessage(mActivity.getString(R.string.failLoadImg));
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && filePathImageCamera != null) {
            List<String> photos = new ArrayList<>();
            photos.add(filePathImageCamera.getAbsolutePath());
            imageCompress.compressImages(true, photos, this);
        } else if (requestCode == REQUEST_SINGLE_IMAGE_NEW && data != null) {

            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            imageCompress.compressImages(true, photos, this);


        }
        else if (requestCode == PICKFILE_RESULT_CODE && data != null) {
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
                    filesMap.add(file.toString());
                    imgPaths.add(file.toString());
                    addImage(imgPaths);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Check path: ", e.getMessage() + "  77");

            }

        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void addImage(ArrayList<String> filesMap) {
        LayoutInflater inflater = LayoutInflater.from(this);
        llImgViews.removeAllViews();
//        if (imgPaths == null) {
        imgPaths = new ArrayList<String>();

        imgPaths.addAll(filesMap);

        for (int i = 0; i < imgPaths.size(); i++) {
            Log.e("image: ", imgPaths.get(i) + " //");
            String file = imgPaths.get(i);
            View view = inflater.inflate(R.layout.item_image, llImgViews, false);
            ImageView img = view.findViewById(R.id.img_add);
            ImageView img_upload = view.findViewById(R.id.img_upload);
            ImageView imgClose = view.findViewById(R.id.img_delete);
            FrameLayout frame = view.findViewById(R.id.frame);
            frame.setTag(file);
            if (from != ACTION_EDIT) {
                img_upload.setVisibility(View.GONE);

            } else img_upload.setVisibility(View.GONE);

            if (from == ACTION_EDIT) {
                int finalI1 = i;
                frame.setOnClickListener(v -> {
                    ToolUtils.openFileUrl(mActivity, imgPaths.get(finalI1));
                });
            }

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

            int finalI = i;
            imgClose.setOnClickListener(view1 -> removeImg(finalI, (View) view1.getParent()));
            llImgViews.addView(view);

        }
        View view = inflater.inflate(R.layout.item_image, llImgViews, false);
        ImageView addImg = view.findViewById(R.id.img_add);
        ImageView imgClose = view.findViewById(R.id.img_delete);
        imgClose.setVisibility(View.INVISIBLE);
        llImgViews.addView(view);
        addImg.setOnClickListener(v ->
                checkPermissions(1));

    }

    private void removeImg(int position, View view) {
        try {
            String image = imgPaths.get(position);
            if (!imgPathsUrl.isEmpty() && position < imgPathsUrl.size()) imgPathsUrl.remove(image);
            if (!imgPaths.isEmpty() && position < imgPaths.size()) imgPaths.remove(position);
            if (!filesMap.isEmpty() && position < filesMap.size()) filesMap.remove(position);
//            filesMapList.remove(position);
//            uploadedFileList=new ArrayList<>();
            if (!uploadedFileList.isEmpty() && position < uploadedFileList.size())
                uploadedFileList.remove(image);

            llImgViews.removeView(view);
            addImage(imgPaths);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showErrorMessage(getString(R.string.permission_not_granted_error));
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissions(type);
        }
    }

    @Override
    public void onError(String error) {
        showErrorMessage(mActivity.getString(R.string.failPickImg));

    }

    @Override
    public void onSuccessMainImageCompress(List<String> photos) {
        filePathImageCamera = new File(photos.get(0));
        setImageInView();
    }

    private void setImageInView() {
        Glide.with(this).load(Uri.fromFile(filePathImageCamera)).into(binding.imgProduct);
    }

    private void uploadImageToStorage() {
//        try {

        String imageName = "Image_" + UUID.randomUUID().toString();

//            Bitmap bmp = MediaStore.Images.Media.getBitmap((this).getContentResolver(), selectedImageUri);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] data = baos.toByteArray();
        CoursesAndLectures.getInstance().saveFileToFirestore(imageName, filePathImageCamera, new StringRequestListener<Uri>() {
            //            CoursesAndLectures.getInstance().saveCourseFileToFirestore(imageName, data, new RequestListener<Uri>() {
            @Override
            public void onSuccess(String message, Uri data) {
                courseImage = data.toString();
                if (filesMap != null && !filesMap.isEmpty()) {
                    UploadFiles();
                } else addCourse();
            }

            @Override
            public void onFail(String message) {
                hideProgress();
                showErrorMessage(message);
            }
        });
//        } catch (IOException e) {
//            showErrorMessage(e.getMessage());
//            e.printStackTrace();
//        }
    }

    private void UploadFiles() {
        uploadedFileList = new ArrayList<>();
        uploadedFileList.addAll(imgPathsUrl);
        for (int i = 0; i < filesMap.size(); i++) {
            Log.e("file i: ", i + " ");
            uploadFileToStorage(new File(filesMap.get(i)), i);
        }
    }

    private void uploadFileToStorage(File file, int index) {
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().saveFileToFirestore(file.getName(), file, new StringRequestListener<Uri>() {
                @Override
                public void onSuccess(String type, Uri data) {
                    Log.e("file url: ", type + " " + data.toString() + " ");
//            saveFileList(type,data.toString());
                    uploadedFileList.add(data.toString());
                    Log.e("file index: ", index + " ");
                    if (index == filesMap.size() - 1) {
                        addCourse();
                    }
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorMessage(message);
                }
            });
        } else showErrorMessage(getString(R.string.noInternetConnection));


    }


    private void addCourse() {

        UserInfo userInfo = AppSharedData.getUserData();
        if (course == null) {
            course = new Course();
            courseId = UUID.randomUUID().toString();
            course.setIsActive(1);
            course.setStudentsNo(0);
            course.setViewsNo(0);
            course.setIsDeleted(false);
            course.setCreatedAt(ToolUtils.getCurrentDateTimeLong());
            course.setCourseId(courseId);
        }
        course.setCourseCode(binding.edCourseCode.getText().toString().trim());
        course.setCourseName(binding.edName.getText().toString().trim());
        course.setCourseDescriptions(binding.edDetails.getText().toString().trim());
        course.setCourseImage(courseImage);
        course.setCourseFiles(uploadedFileList);
        course.setLecturerId(userInfo.getUserId());
        course.setLecturerName(userInfo.getShortName());
        course.setHaveAssignment(binding.chbAssignment.isChecked());
        course.setCourseDeadline(deadline);
        CoursesAndLectures.getInstance().addEditCourseInDataBase(from, course, new StringRequestListener<Course>() {
            @Override
            public void onSuccess(String message, Course data) {
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

    @Override
    public void onSuccessImageCompress(List<String> photos) {

    }

    public void initDialog() {
        mItemSelectImageDialogFragment = ItemSelectImageDialogFragment.newInstance(getString(R.string.select_image_dialog_choose_image), 2);
        mItemSelectImageDialogFragment.setListener(this);
    }

    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);

    }

}