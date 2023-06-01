package com.m_learning.feature.Chat.presenter;

import static android.app.Activity.RESULT_OK;
import static com.m_learning.utils.ConstantApp.REQUEST_CODE_CLICK_IMAGE;
import static com.m_learning.utils.ConstantApp.REQUEST_IMAGE_CAPTURE;
import static com.m_learning.utils.ConstantApp.REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA;
import static com.m_learning.utils.ConstantApp.REQUEST_SINGLE_IMAGE_NEW;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.m_learning.R;
import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.view.ChatContract;
import com.m_learning.utils.AppShareMethods;
import com.m_learning.utils.ImageCompress;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnSendMessageListener,
        ImageCompress.onFinishedCompressListListener,
        ChatContract.OnGetMessagesListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;
    Activity mActivity;
    String roomId;
    String receiverId;
    private Dialog dialog;
    private File filePathImageCamera = null;
    private final GalleryConfig.Build config;
    private final ImageCompress imageCompress;
    private Uri selectedImageUri;

    public ChatPresenter(Activity mActivity, ChatContract.View view) {
        this.mView = view;
        this.mActivity = mActivity;
        mChatInteractor = new ChatInteractor(this, this);
        config = new GalleryConfig.Build()
                .limitPickPhoto(1)
                .singlePhoto(true)
                .filterMimeTypes(new String[]{"image/gif"});
        imageCompress = new ImageCompress(mActivity);
    }

    @Override
    public void sendMessage(Context context, Chat chat, String receiverFirebaseToken, String os) {
        mChatInteractor.sendMessageToFirebaseUser(context, chat, receiverFirebaseToken, os);
    }

    @Override
    public void sendPushNotificationToReceiver(String username, String message, String uid,
                                               String firebaseToken, String receiverFirebaseToken,
                                               String os, String receiverId) {

        mChatInteractor.sendPushNotificationToReceiver(username, message, uid,
                receiverFirebaseToken, os);
    }

    @Override
    public void getMessage(String senderUid, String receiverUid, int limit, int i) {
        mChatInteractor.getMessageFromFirebaseUser(senderUid, receiverUid, limit, i);
    }

    @Override
    public void checkIfRoomExist(String senderUid, String receiverUid) {
        mChatInteractor.checkIfRoomExist(senderUid, receiverUid);
    }

    @Override
    public void onSendMessageSuccess(Chat chat) {
        mView.onSendMessageSuccess(chat);
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(Chat chat, int i) {
        mView.onGetMessagesSuccess(chat, i);
    }

    @Override
    public void onUpdateMessagesStatus(Chat chat, int i) {
        mView.onUpdateMessagesStatus(chat, i);

    }

    @Override
    public void onDeleteMessagesStatus(String messageId, int i) {
        mView.onDeleteMessagesStatus(messageId, i);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }

    public void onDestroy() {
        mChatInteractor.onDestroy();
    }

    public void chooseProfileImage() {
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
        showSelectImageDialog();
    }

    private void showSelectImageDialog() {
        mView.showImageDialog();
    }

    public void onGalleryClicked() {
        onGalleryClickedImplementation();
    }

    public void onCameraClicked() {
        onCameraClickedImplementation();
    }

    private void onGalleryClickedImplementation() {
        int request;
        config.limitPickPhoto(1);

        config.hintOfPick(mActivity.getString(R.string.hint_must_select));
        request = REQUEST_SINGLE_IMAGE_NEW;
        GalleryActivity.openActivity(mActivity, request, config.build());
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                } else if (mView != null)
                    mView.showErrorMessage(mActivity.getString(R.string.failLoadImg));
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && filePathImageCamera != null) {
            List<String> photos = new ArrayList<>();
            photos.add(filePathImageCamera.getAbsolutePath());
            imageCompress.compressImages(true, photos, this);
        } else if (requestCode == REQUEST_SINGLE_IMAGE_NEW && data != null) {

            List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            if ((photos != null && !photos.isEmpty())) {
                imageCompress.compressImages(true, photos, this);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA &&
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (mView != null)
                mView.showErrorMessage(mActivity.getString(R.string.permission_not_granted_error));
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseProfileImage();
        }
    }

    @Override
    public void onError(String error) {
        if (mView != null) mView.showErrorMessage(mActivity.getString(R.string.failPickImg));

    }

    @Override
    public void onSuccessMainImageCompress(List<String> photos) {
        filePathImageCamera = new File(photos.get(0));
        mView.setLicenseImageName(filePathImageCamera);
    }

    @Override
    public void onSuccessImageCompress(List<String> photos) {

    }
}
