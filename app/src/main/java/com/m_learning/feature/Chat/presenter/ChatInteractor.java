package com.m_learning.feature.Chat.presenter;

import static com.m_learning.utils.ConstantApp.TB_CHAT_ROOMS;
import static com.m_learning.utils.ConstantApp.TB_RECENT;
import static com.m_learning.utils.ConstantApp.TYPE_CHAT;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.m_learning.fcm.FcmNotificationBuilder;
import com.m_learning.feature.Chat.model.Chat;
import com.m_learning.feature.Chat.view.ChatContract;


public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;
    private ValueEventListener valuListener;
    private String roomId;
    private ChildEventListener getMsgChildEventListener;
    private Query messageQuery;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Chat chat,
                                          final String receiverFirebaseToken, final String os) {
        int comparison = chat.senderUid.compareTo(chat.receiverUid);
        if (comparison < 0) {
            roomId = chat.senderUid + "_" + chat.receiverUid;
            Log.i("///***", comparison + "");
        } else {
            roomId = chat.receiverUid + "_" + chat.senderUid;
        }
        chat.groupId = roomId;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(TB_CHAT_ROOMS).child(roomId)
                .child("messages").push().setValue(chat);
        databaseReference.child(TB_RECENT).child(roomId)
                .setValue(chat);
        mOnSendMessageListener.onSendMessageSuccess(chat);

        databaseReference.child(TB_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(roomId)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + roomId + " exists");
                    chat.groupId = roomId;
                    if (!dataSnapshot.child(roomId).hasChild("messages")) {
                        getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid, 30, 0);
                    }
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    chat.groupId = roomId;
                    getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid, 30, 0);
                }

//                sendPushNotificationToReceiver(chat.senderName,
//                        chat.message,
//                        chat.senderUid,
//                        FirebaseInstanceId.getInstance().getToken(),
//                        receiverFirebaseToken, os, chat.receiverUid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void checkIfRoomExist(final String senderUid, final String receiverUid) {
        int comparison = senderUid.compareTo(receiverUid);
        if (comparison < 0) {
            roomId = senderUid + "_" + receiverUid;
        } else {
            roomId = receiverUid + "_" + senderUid;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        valuListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(roomId)) {
                    if (!dataSnapshot.child(roomId).hasChild("messages")) {
                        mOnGetMessagesListener.onGetMessagesFailure("getMessageFromFirebaseUser: no such room available");
                    }
                } else {
                    mOnGetMessagesListener.onGetMessagesFailure("getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        };
        databaseReference.child(TB_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(valuListener);
    }

    @Override
    public void getMessageFromFirebaseUser(final String senderUid, final String receiverUid, final int limit, final int i) {
        int comparison = senderUid.compareTo(receiverUid);

        if (comparison < 0) {
            roomId = senderUid + "_" + receiverUid;
        } else {
            roomId = receiverUid + "_" + senderUid;
        }
        messageQuery = FirebaseDatabase.getInstance().getReference().child(TB_CHAT_ROOMS).child(roomId)
                .child("messages").limitToLast(limit);
        getMsgChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chat.messageId = dataSnapshot.getKey();
                mOnGetMessagesListener.onGetMessagesSuccess(chat, i);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chat.messageId = dataSnapshot.getKey();
                Log.e("size chat update: ", chat.messageId + " " + chat.status + " " + chat.receiverUid);
                mOnGetMessagesListener.onUpdateMessagesStatus(chat, i);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mOnGetMessagesListener.onDeleteMessagesStatus(dataSnapshot.getKey(), i);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        };

        messageQuery.addChildEventListener(getMsgChildEventListener);

    }

    public void onDestroy() {
        if (getMsgChildEventListener != null)
            messageQuery.removeEventListener(getMsgChildEventListener);
    }

    public void sendPushNotificationToReceiver(String username, String message, String uid,
                                               String receiverFirebaseToken,
                                               String os) {

        Log.e("receiverFirebaseToken: ", receiverFirebaseToken + " 888");
        FcmNotificationBuilder.initialize()
                .type(TYPE_CHAT)
                .title(username)
                .message(message)
                .os(os)
                .uid(uid)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }
}
