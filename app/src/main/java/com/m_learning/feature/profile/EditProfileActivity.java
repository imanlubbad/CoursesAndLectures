package com.m_learning.feature.profile;

import static com.m_learning.utils.ConstantApp.REQUEST_CODE_CLICK_IMAGE;
import static com.m_learning.utils.ConstantApp.REQUEST_IMAGE_CAPTURE;
import static com.m_learning.utils.ConstantApp.REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA;
import static com.m_learning.utils.ConstantApp.REQUEST_SINGLE_IMAGE_NEW;
import static com.m_learning.utils.ConstantApp.TYPE_LECTURER;
import static com.m_learning.utils.ConstantApp.USER_INFO;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.m_learning.R;
import com.m_learning.databinding.ActivityEditProfileBinding;
import com.m_learning.feature.baseView.BaseActivity;
import com.m_learning.network.feature.User;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.RequestListener;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EditProfileActivity extends BaseActivity implements ImageCompress.onFinishedCompressListListener, ItemSelectImageDialogFragment.Listener, DatePickerDialog.OnDateSetListener {


    private ActivityEditProfileBinding binding;
    private UserInfo userInfo;
    private ItemSelectImageDialogFragment mItemSelectImageDialogFragment;
    private File filePathImageCamera = null;
    private ImageCompress imageCompress;
    private GalleryConfig.Build config;
    private String userImage;
    private long dob;

    public static Intent newInstance(Activity mActivity, UserInfo userData) {
        Intent intent = new Intent(mActivity, EditProfileActivity.class);
        intent.putExtra(USER_INFO, userData);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        config = new GalleryConfig.Build()
                .limitPickPhoto(1)
                .singlePhoto(true)
                .filterMimeTypes(new String[]{"image/gif"});
        imageCompress = new ImageCompress(this);
        initDialog();
        userInfo = (UserInfo) getIntent().getSerializableExtra(USER_INFO);
        setInfoForUser();
        binding.rlImage.setOnClickListener(view1 -> checkPermissions());
        binding.ibtnBack.setOnClickListener(view1 -> finish());
        binding.btnSave.setOnClickListener(view1 -> validateInput());
        binding.edBirthDate.setOnClickListener(view1 -> setDate());

    }

    private void validateInput() {
        binding.edFirstName.setError(null);
        binding.edMiddleName.setError(null);
        binding.edLastName.setError(null);
        binding.edAddress.setError(null);
        binding.edMobileNo.setError(null);
        binding.edBirthDate.setError(null);

        if (AppShareMethods.isEmptyEditText(binding.edFirstName)) {
            binding.edFirstName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edMiddleName)) {
            binding.edMiddleName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edLastName)) {
            binding.edLastName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edBirthDate)) {
            binding.edMiddleName.setError(getString(R.string.is_required));
            return;
        }
        if (AppShareMethods.isEmptyEditText(binding.edAddress)) {
            binding.edAddress.setError(getString(R.string.is_required));
            return;
        }


        if (AppShareMethods.isEmptyEditText(binding.edMobileNo)) {
            binding.edMobileNo.setError(getString(R.string.is_required));
            return;
        }
        if (ToolUtils.isNetworkConnected()) {
            showProgress();
            if (filePathImageCamera != null) {
                uploadImageToStorage();
            } else {
                updateProfile();
            }
        } else showErrorMessage(getString(R.string.noInternetConnection));
    }

    private void updateProfile() {
        UserInfo userData = userInfo;
        userData.setAddress(binding.edAddress.getText().toString().trim());
        userData.setFirstName(binding.edFirstName.getText().toString().trim());
        userData.setMiddleName(binding.edMiddleName.getText().toString().trim());
        userData.setLastName(binding.edLastName.getText().toString().trim());
        userData.setMobile(binding.edMobileNo.getText().toString().trim());
        userData.setDob(dob);
        userData.setUserImage(userImage);

        User.saveUserInfoToDatabase(userData, new StringRequestListener<UserInfo>() {
            @Override
            public void onSuccess(String msg, UserInfo data) {
                hideProgress();
                showErrorMessage(getString(R.string.update_success));
                AppSharedData.setUserData(data);
                finish();
            }

            @Override
            public void onFail(String message) {
                showErrorMessage(message);
                hideProgress();
            }
        });

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
        showSelectImageDialog();

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

        config.hintOfPick(getString(R.string.hint_must_select));
        request = REQUEST_SINGLE_IMAGE_NEW;
        GalleryActivity.openActivity(this, request, config.build());
    }


    private void onCameraClickedImplementation() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            filePathImageCamera = AppShareMethods.getNewImageFile(this);
            Uri photoURI;
            int request = REQUEST_IMAGE_CAPTURE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                photoURI = FileProvider.getUriForFile(this, getString(R.string.provider), filePathImageCamera);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                photoURI = Uri.fromFile(filePathImageCamera);
                request = REQUEST_CODE_CLICK_IMAGE;
            }

            startActivityForResult(takePictureIntent, request);
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
                    showErrorMessage(getString(R.string.failLoadImg));
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && filePathImageCamera != null) {
            List<String> photos = new ArrayList<>();
            photos.add(filePathImageCamera.getAbsolutePath());
            imageCompress.compressImages(true, photos, this);
        } else if (requestCode == REQUEST_SINGLE_IMAGE_NEW && data != null) {

            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            imageCompress.compressImages(true, photos, this);


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
            checkPermissions();
        }
    }

    @Override
    public void onError(String error) {
        showErrorMessage(getString(R.string.failPickImg));

    }

    @Override
    public void onSuccessMainImageCompress(List<String> photos) {
        filePathImageCamera = new File(photos.get(0));
        setImageInView();
    }

    @Override
    public void onSuccessImageCompress(List<String> photos) {

    }

    private void setImageInView() {
        Glide.with(this).load(Uri.fromFile(filePathImageCamera)).into(binding.ivAvatar);
    }

    private void uploadImageToStorage() {

        String imageName = "Image_" + UUID.randomUUID().toString();
        User.getInstance().saveImageToFirestore(imageName, filePathImageCamera, new RequestListener<Uri>() {
            @Override
            public void onSuccess(Uri data) {
                userImage = data.toString();
                updateProfile();
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


    public void initDialog() {
        mItemSelectImageDialogFragment = ItemSelectImageDialogFragment.newInstance(getString(R.string.select_image_dialog_choose_image), 2);
        mItemSelectImageDialogFragment.setListener(this);
    }

    @Override
    public void showErrorMessage(String message) {
        AppShareMethods.showSnackBar(this, binding.root, message);

    }

    private void setInfoForUser() {
        if (userInfo.getUserType().equals(TYPE_LECTURER)) {
            binding.edFirstName.setText(userInfo.getShortName());
            binding.edMiddleName.setVisibility(View.GONE);
            binding.edLastName.setVisibility(View.VISIBLE);
            binding.edBirthDate.setVisibility(View.GONE);
        } else {
            binding.edMiddleName.setVisibility(View.VISIBLE);
            binding.edLastName.setVisibility(View.VISIBLE);
            binding.edBirthDate.setVisibility(View.VISIBLE);
            binding.edFirstName.setText(userInfo.getFirstName());
            binding.edMiddleName.setText(userInfo.getMiddleName());
            binding.edLastName.setText(userInfo.getLastName());
            binding.edBirthDate.setText(ToolUtils.convertMillisecondToDate(userInfo.getDob()));
        }

        binding.edAddress.setText(userInfo.getAddress());
        binding.edFirstName.setText(userInfo.getFirstName());
        binding.edMiddleName.setText(userInfo.getMiddleName());
        binding.edLastName.setText(userInfo.getLastName());
        binding.edBirthDate.setText(ToolUtils.convertMillisecondToDate(userInfo.getDob()));
        binding.edMobileNo.setText(userInfo.getMobile());
        userImage = userInfo.getUserImage();
        ToolUtils.setRoundImg(this, userImage, binding.ivAvatar);
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
                .maxDate(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                .minDate(1950, 0, 1)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        binding.edBirthDate.setText(date);
        dob = ToolUtils.convertDateToMillisecond(date);
    }
}