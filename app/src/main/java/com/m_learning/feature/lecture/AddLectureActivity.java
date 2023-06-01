package com.m_learning.feature.lecture;

import static com.m_learning.utils.ConstantApp.ACTION_ADD;
import static com.m_learning.utils.ConstantApp.ACTION_CAPTURE_VIDEO;
import static com.m_learning.utils.ConstantApp.ACTION_PIC_VIDEO;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.LECTURE;
import static com.m_learning.utils.ConstantApp.REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA;
import static com.m_learning.utils.ConstantApp.REQUEST_SINGLE_IMAGE_NEW;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.m_learning.R;
import com.m_learning.databinding.ActivityAddLectureBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.CoursesAndLectures;
import com.m_learning.network.model.Lecture;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.network.utils.StringRequestListener;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.ItemSelectImageDialogFragment;
import com.m_learning.utils.ToolUtils;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddLectureActivity extends BaseActivity implements ItemSelectImageDialogFragment.Listener {

    private ActivityAddLectureBinding binding;
    String lectureVideo;
    private File filePathVideo = null;
    private Lecture lecture = null;
    private String lectureId;
    int from;
    private GalleryConfig.Build config;
    private ItemSelectImageDialogFragment mItemSelectImageDialogFragment;
    private Bitmap thumbImg;
    private String thumbImgUrl = "";
    private String courseId;

    public static Intent newInstance(Activity activity) {
        return new Intent(activity, AddLectureActivity.class);
    }

    public static Intent newInstance(Activity activity, int from, String courseId, Lecture lecture) {
        Intent intent = new Intent(activity, AddLectureActivity.class);
        intent.putExtra(FROM_WHERE, from);
        intent.putExtra(LECTURE, lecture);
        intent.putExtra("courseId", courseId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddLectureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        from = getIntent().getIntExtra(FROM_WHERE, ACTION_ADD);
        courseId = getIntent().getStringExtra("courseId");
        lecture = (Lecture) getIntent().getSerializableExtra(LECTURE);
        config = new GalleryConfig.Build()
                .limitPickPhoto(1)
                .singlePhoto(false)
                .filterMimeTypes(new String[]{"image/gif"});

        mItemSelectImageDialogFragment = ItemSelectImageDialogFragment.newInstance(getString(R.string.select_image_dialog_choose_image), 3);
        mItemSelectImageDialogFragment.setListener(this);

        if (lecture != null) {
            binding.btnAdd.setText(getString(R.string.save));
            binding.tvTitle.setText(getString(R.string.edit_lectureT));
            binding.edName.setText(lecture.getTitle());
            lectureId = lecture.getId();
            thumbImgUrl = lecture.getImage();
            binding.edDetails.setText(lecture.getDescription());
            lectureVideo = lecture.getLectureVideo();
            ToolUtils.setImg(this, thumbImgUrl, binding.imgPhotoOrVideo);
            binding.llAddImage.setVisibility(View.GONE);
            binding.imgVideoThumb.setVisibility(View.VISIBLE);
        } else {
            binding.btnAdd.setText(getString(R.string.add));
            binding.tvTitle.setText(getString(R.string.add_lecture));
        }
        initListener();
    }

    private void initListener() {
        binding.llChooseVideo.setOnClickListener(view -> checkPermissions());
        binding.btnAdd.setOnClickListener(view -> validateInputs());
        binding.ibtnBack.setOnClickListener(view -> finish());
    }

    private void validateInputs() {
        binding.edName.setError(null);
        binding.edDetails.setError(null);
        if (filePathVideo == null && TextUtils.isEmpty(lectureVideo)) {
            showErrorMessage(getString(R.string.hint_must_video));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edName)) {
            binding.edName.setError(getString(R.string.is_required));
            return;
        }

        ToolUtils.hideKeyboard(this);
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            if (filePathVideo != null)
                uploadVideoToStorage();
            else addLecture();

        } else {
            showErrorMessage(getString(R.string.noInternetConnection));
        }
    }

    public void checkPermissions() {
        String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> permissionsToRequest = new ArrayList<String>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA);
            return;
        }

        showSelectVideoDialog();
    }

    private void showSelectVideoDialog() {
        mItemSelectImageDialogFragment.show(getSupportFragmentManager(), ItemSelectImageDialogFragment.class.getSimpleName());
    }


    private void onGalleryClickedImplementation() {
        int request;
        config.hintOfPick(getString(R.string.hint_must_select)).build();
        request = REQUEST_SINGLE_IMAGE_NEW;
        GalleryActivity.openActivity(this, request, config.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SINGLE_IMAGE_NEW && resultCode == RESULT_OK && data != null) {
            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            if ((photos != null && !photos.isEmpty())) {
                showErrorMessage(getString(R.string.selectVideo));
            } else {
                String video = data.getStringExtra(GalleryActivity.VIDEO);
                if (video != null && !video.isEmpty()) {
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);//MediaStore.Video.Thumbnails.MICRO_KIND
                    if (bMap != null) {
                        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(video));
                        int duration = mp.getDuration();
                        mp.release();
//                        if ((duration / 1000) > 60) {
//                            showErrorMessage(getString(R.string.video_too_large));
//                        } else {
                        filePathVideo = new File(video);
                        setSelectedVideo(filePathVideo, bMap);
//                        }
                    }
                }
            }
        } else if (requestCode == ACTION_CAPTURE_VIDEO && data != null) {
            Uri contentURI = data.getData();
            String selectedVideoPath = ToolUtils.getPath(contentURI, this);

            if (!TextUtils.isEmpty(selectedVideoPath)) {
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(selectedVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);//MediaStore.Video.Thumbnails.MICRO_KIND
                if (bMap != null) {
                    this.thumbImg = bMap;
                    filePathVideo = new File(selectedVideoPath);
                    setSelectedVideo(filePathVideo, bMap);
                }
            } else
                showErrorMessage(this.getString(R.string.fail_load_video));
        } else if (requestCode == ACTION_PIC_VIDEO && data != null) {
            Uri contentURI = data.getData();
            String selectedVideoPath = ToolUtils.getPath(contentURI, this);

            if (!TextUtils.isEmpty(selectedVideoPath)) {
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(selectedVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);//MediaStore.Video.Thumbnails.MICRO_KIND
                if (bMap != null) {
                    this.thumbImg = bMap;

                    filePathVideo = new File(selectedVideoPath);
                    setSelectedVideo(filePathVideo, bMap);
                }
            } else
                showErrorMessage(this.getString(R.string.fail_load_video));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_DENIED) {

            showErrorMessage(getString(R.string.permission_not_granted_error));
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        }
    }

    private void uploadVideoToStorage() {

        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            CoursesAndLectures.getInstance().saveLectureFileToFirestore(filePathVideo.getName(), filePathVideo, new StringRequestListener<Uri>() {
                @Override
                public void onSuccess(String type, Uri data) {
                    Log.e("file url: ", type + " " + data.toString() + " ");
                    lectureVideo = data.toString();
                    uploadVideoThumbToStorage();

                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorMessage(message);
                }
            });

        } else {
            showErrorMessage(getString(R.string.noInternetConnection));
        }
    }

    private void uploadVideoThumbToStorage() {
        addLecture();

        String imageName = UUID.randomUUID().toString();

        Bitmap bmp = thumbImg;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        CoursesAndLectures.getInstance().saveThumbFileToFirestore(imageName, data, new RequestListener<Uri>() {
            @Override
            public void onSuccess(Uri data) {
                thumbImgUrl = data.toString();
                addLecture();

            }

            @Override
            public void onFail(String message) {
                addLecture();
            }
        });

    }


    private void addLecture() {

        UserInfo userInfo = AppSharedData.getUserData();
        if (lecture == null) {
            lecture = new Lecture();
            lectureId = UUID.randomUUID().toString();
            lecture.setId(lectureId);
            lecture.setIsActive(1);
            lecture.setViewsNo(0);
            lecture.setIsDeleted(false);
            lecture.setCreatedAt(ToolUtils.getCurrentDateTimeLong());
            lecture.setCourseId(courseId);
            lecture.setLecturerId(userInfo.getUserId());
            lecture.setLecturerName(userInfo.getShortName());
        }
        lecture.setTitle(binding.edName.getText().toString().trim());
        lecture.setDescription(binding.edDetails.getText().toString().trim());
        lecture.setLectureVideo(lectureVideo);
        lecture.setImage(thumbImgUrl);

        CoursesAndLectures.getInstance().addEditCourseLectureInDataBase(from, lecture, new StringRequestListener<Lecture>() {
            @Override
            public void onSuccess(String message, Lecture data) {
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
    public void onGalleryClicked() {
//        onGalleryClickedImplementation();
        onVideoGalleryClickedImplementation();


    }

    private void onVideoGalleryClickedImplementation() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("video/*");
        startActivityForResult(galleryIntent, ACTION_PIC_VIDEO);
    }

    @Override
    public void onCameraClicked() {

    }

    @Override
    public void onVideoClicked() {
        onVideoCameraClickedImplementation();
    }


    public void onVideoCameraClickedImplementation() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra("android.intent.extra.durationLimit", 3 * 60);
        startActivityForResult(intent, ACTION_CAPTURE_VIDEO);
    }


    public void setSelectedVideo(File file, Bitmap bMap) {
        ToolUtils.setImg(this, file.getAbsolutePath(), binding.imgPhotoOrVideo);
        binding.llAddImage.setVisibility(View.GONE);
        binding.imgVideoThumb.setVisibility(View.VISIBLE);
        thumbImg = bMap;

    }

    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);
    }
}