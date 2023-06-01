package com.m_learning.network.feature;

import static com.m_learning.utils.ConstantApp.ANDROID;
import static com.m_learning.network.utils.FirebaseReferance.COUNTER;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.m_learning.R;
import com.m_learning.feature.Chat.model.UserData;
import com.m_learning.network.model.UserInfo;
import com.m_learning.network.utils.FirebaseReferance;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.MLearningApp;

import java.util.Objects;


public class ChatFirebase {
    private static ChatFirebase instance;
    private RequestListener<UserData> otherUserResponse;
    private RequestListener<Boolean> childEventResponse;
    private RequestListener<Boolean> childActiveResponse;

    public ChatFirebase() {
    }

    public static ChatFirebase getInstance() {
        if (instance == null)
            instance = new ChatFirebase();
        return instance;
    }

    private final String TAG = ChatFirebase.class.getSimpleName();

    private final ValueEventListener otherUser = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Log.i(TAG, dataSnapshot.getValue().toString());
                UserInfo user = dataSnapshot.getValue(UserInfo.class);
                otherUserResponse.onSuccess(new UserData(user));
            } else {
                otherUserResponse.onFail(MLearningApp.getInstance().getResources().getString(R.string.no_messages));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            otherUserResponse.onFail(databaseError.getMessage());
        }
    };

    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
            try {
                boolean status = (boolean) dataSnapshot.getValue();
                childEventResponse.onSuccess(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            childEventResponse.onFail(databaseError.getMessage());
        }
    };

    private final ChildEventListener childActiveListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
            try {
                Log.e("onChildAdded", dataSnapshot + "");
                boolean statusActive = (boolean) dataSnapshot.getValue();
                childActiveResponse.onSuccess(statusActive);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("test", dataSnapshot + "");

            }
        }

        @Override
        public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
            try {
                boolean statusActive = (boolean) dataSnapshot.getValue();
                Log.e("onChildChanged", dataSnapshot + "");

                childActiveResponse.onSuccess(statusActive);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            childActiveResponse.onFail(databaseError.getMessage());
        }
    };

    public void saveUserDataToDatabase(RequestListener<UserData> requestListener) {

        com.m_learning.network.model.UserInfo user = AppSharedData.getUserData();

        UserData userData = new UserData(user.getUserId() + "", user.getFirstName(),
                user.getUserImage(),
                AppSharedData.getFcmToken(), ANDROID, 1);

        FirebaseReferance.getInstance().getUserReference(userData.getMyIdMember())
                .setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestListener.onSuccess(userData);
            } else {
                requestListener.onFail(Objects.requireNonNull(task.getException()).getMessage());
            }

        });

    }

    public void startUserDataProfileListenerForSingleValueEvent(String userId, RequestListener<UserData> requestListener) {
        this.otherUserResponse = requestListener;
        FirebaseReferance.getUserReference(userId).addListenerForSingleValueEvent(otherUser);
    }

    public void startUserDataProfileListener(String userId, RequestListener<UserData> requestListener) {
        this.otherUserResponse = requestListener;
        FirebaseReferance.getUserReference(userId).addValueEventListener(otherUser);
    }

    public void changeStatusUserInRoom(String roomId, String userId, boolean active) {
        FirebaseReferance.getInstance().changeStatusUserInRoom(roomId, userId).setValue(active);

    }

    public void countUnReadMessage(String roomId, String userId, int counter) {
        FirebaseReferance.getInstance().countUnReadMessage(roomId, userId).setValue(counter);

    }

    public void setTypeStatus(String roomId, String userId, boolean status) {
        FirebaseReferance.getInstance().setTypeStatus(roomId, userId).setValue(status);

    }

    public void getCountUnReadMessage(String roomId, String userId) {
        FirebaseReferance.getInstance().getCountUnReadMessage(roomId, userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                int counter = 1;
                Log.i("/////////", dataSnapshot.getValue() + "");
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(COUNTER).getValue() != null) {
                        counter = Integer.parseInt(dataSnapshot.child(COUNTER).getValue() + "") + 1;
                    }
                }
                countUnReadMessage(roomId, userId, counter);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

    }

    public void saveImageToFirestore(String imageName, byte[] photo, RequestListener<Uri> requestListener) {
        StorageReference ref = FirebaseReferance.getInstance().getStorageChatImageReference(imageName);
        UploadTask uploadTask = ref.putBytes(photo);
        uploadTask.continueWithTask((Task<UploadTask.TaskSnapshot> task) -> {
            if (!task.isSuccessful()) {
                requestListener.onFail(task.getException().getMessage());
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                requestListener.onSuccess(Uri.parse(downloadUri.toString()));
            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public void getRoomReference(RequestListener<DataSnapshot> requestListener) {
        FirebaseReferance.getInstance().getChatRoomListReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                requestListener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                requestListener.onFail(databaseError.getMessage());
            }
        });
    }

    public void startChildTypetListener(String roomId, String userId, RequestListener<Boolean> requestListener) {
        this.childEventResponse = requestListener;
        FirebaseReferance.getInstance().typeIndicatorReference(roomId, userId).addChildEventListener(childEventListener);
    }

    public void startChildActiveListener(String roomId, String userId, RequestListener<Boolean> requestListener) {
        this.childActiveResponse = requestListener;
        FirebaseReferance.getInstance().statusUserInRoomReference(roomId, userId).addChildEventListener(childActiveListener);
    }

    public void stopUserDataProfileListener(String userId) {
        if (otherUser != null)
            FirebaseReferance.getInstance().getUserReference(userId).removeEventListener(otherUser);
    }

    public void stopChildTypetListener(String roomId, String userId) {
        if (childEventListener != null)
            FirebaseReferance.getInstance().typeIndicatorReference(roomId, userId).removeEventListener(childEventListener);
    }

    public void stopChildActiveListener(String roomId, String userId) {
        if (childActiveListener != null)
            FirebaseReferance.getInstance().statusUserInRoomReference(roomId, userId).removeEventListener(childActiveListener);
    }

    public static void getAllUnReadMessages(String userId, RequestListener<Integer> requestListener) {
        FirebaseReferance.getInstance().getSeen().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                int number = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().contains(userId)) {
                        for (DataSnapshot snap : data.getChildren()) {
                            if (snap.getKey().equals(userId)) {
                                int counter = snap.child("counter").getValue(Integer.class);
                                number = number + counter;
                            }
                        }
                    }
                }
                requestListener.onSuccess(number);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                requestListener.onFail(databaseError.getMessage());
            }
        });
    }

}
