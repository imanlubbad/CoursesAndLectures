package com.m_learning.network.feature;


import static com.m_learning.network.utils.FirebaseReferance.getNotificationsReference;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.m_learning.R;
import com.m_learning.network.model.Notifications;
import com.m_learning.network.utils.FirebaseReferance;
import com.m_learning.network.utils.RequestListener;
import com.m_learning.utils.MLearningApp;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationsRequests {
    private static String TAG="NotificationsRequests";
    private RequestListener<ArrayList<Notifications>> getNotificationsRequest;
    private static NotificationsRequests instance;

    public static NotificationsRequests getInstance() {
        if (instance == null)
            instance = new NotificationsRequests();
        return instance;
    }

    private ValueEventListener getNotificationsDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                Log.e(TAG, "dataSnapshot{" + dataSnapshot.getValue().toString() + "}");
                ArrayList<Notifications> groupBeans = new ArrayList<>();
                for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                    Log.e(TAG, "snapshotChild{" + snapshotChild.getValue() + "}");
                    Notifications item = snapshotChild.getValue(Notifications.class);
                    if (item != null && item.getStatus() == 0 && !TextUtils.isEmpty(item.getKey())) {
                        FirebaseReferance.getUserNotificationsReference(FirebaseReferance.getUserId())
                                .child(item.getKey()).child("status").setValue(1);
                    }
                    if (!TextUtils.isEmpty(item.getKey()))
                        groupBeans.add(item);
                }
                Collections.reverse(groupBeans);
                getNotificationsRequest.onSuccess(groupBeans);
            } else {
                getNotificationsRequest.onFail(MLearningApp.getInstance().getString(R.string.no_data_found));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            getNotificationsRequest.onFail(databaseError.getMessage());
        }
    };

    public void startNotificationsDataListener(RequestListener<ArrayList<Notifications>> requestListener) {
        this.getNotificationsRequest = requestListener;
        FirebaseReferance.getUserNotificationsReference(FirebaseReferance.getUserId())
                .orderByChild("createdAt")
                .addValueEventListener(getNotificationsDataListener);
    }

    public static void getUnReadMsg(String userId, RequestListener<Integer> requestListener) {
        Log.e("test_crash", userId);
        FirebaseReferance.getUserNotificationsReference(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = 0;
                        int i = 1;
                        for (DataSnapshot snapshotChild : dataSnapshot.getChildren()) {
                            Log.e(TAG, "snapshotChild{" + snapshotChild.getValue() + "}");
                            try {
                                Notifications item = snapshotChild.getValue(Notifications.class);
                                if (item != null && item.getStatus() == 0) {
                                    count++;
                                }
                                if (i >= dataSnapshot.getChildrenCount()) {
                                    requestListener.onSuccess(count);
                                }
                                i++;
                            } catch (Exception ex) {

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        requestListener.onFail(databaseError.getMessage());
                    }
                });
    }

    public void stopNotificationsDataListener() {
        if (getNotificationsRequest != null)
            FirebaseReferance
                    .getUserNotificationsReference(FirebaseReferance.getUserId())
                    .removeEventListener(getNotificationsDataListener);
    }

    public static void addNotificationItem(String id, Notifications
            notifications, RequestListener<Notifications> requestListener) {
        String key = getNotificationsReference().push().getKey();
        notifications.setKey(key);
        FirebaseReferance.getUserNotificationsReference(id).child(key).setValue(notifications).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
               if (requestListener!=null)requestListener.onSuccess(notifications);
            } else {
                if (requestListener!=null) requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public static void deleteNotification(String key, RequestListener<Boolean> requestListener) {
        FirebaseReferance.getUserNotificationsReference(FirebaseReferance.getUserId())
                .child(key).setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestListener.onSuccess(task.isSuccessful());
            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

    public static void deleteAllNotification(RequestListener<Boolean> requestListener) {
        FirebaseReferance.getUserNotificationsReference(FirebaseReferance.getUserId()).setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestListener.onSuccess(task.isSuccessful());
            } else {
                requestListener.onFail(task.getException().getMessage());
            }
        });
    }

}
