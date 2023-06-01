package com.m_learning.feature.Chat.view;

import android.content.Context;

import com.m_learning.feature.Chat.model.Chat;

import java.io.File;

public interface ChatContract {
    interface View {
        void onSendMessageSuccess(Chat chat);

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat, int i);

        void onUpdateMessagesStatus(Chat chat, int i);

        void onDeleteMessagesStatus(String messageId, int i);

        void onGetMessagesFailure(String message);

        void onErrorResponse(String message);

        void showProgress();

        void hideProgress();

        void onSuccessResponse(String message);

        void showErrorMessage(String string);

        void showImageDialog();

        void setLicenseImageName(File filePath);
    }

    interface Presenter {
        void sendMessage(Context context, Chat chat, String receiverFirebaseToken, String os);

        void getMessage(String senderUid, String receiverUid, int limit, int i);

        void checkIfRoomExist(String senderUid, String receiverUid);

        void sendPushNotificationToReceiver(String username,
                                            String message,
                                            String uid,
                                            String firebaseToken,
                                            String receiverFirebaseToken, String os, String receiverId);

    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, Chat chat, String receiverFirebaseToken, String os);

        void getMessageFromFirebaseUser(String senderUid, String receiverUid, int limit, int i);

        void checkIfRoomExist(String senderUid, String receiverUid);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess(Chat chat);

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(Chat chat, int i);

        void onUpdateMessagesStatus(Chat chat, int i);

        void onDeleteMessagesStatus(String messageId, int i);

        void onGetMessagesFailure(String message);
    }

     interface OnLoadMoreListener {

        /**
         * Fires when user scrolled to the end of list.
         *
         * @param page            next page to download.
         * @param totalItemsCount current items count.
         */
        void onLoadMore(int page, int totalItemsCount);
    }

}
