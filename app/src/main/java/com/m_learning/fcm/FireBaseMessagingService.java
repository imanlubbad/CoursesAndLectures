package com.m_learning.fcm;


import static com.m_learning.utils.ConstantApp.FROM_CHAT;
import static com.m_learning.utils.ConstantApp.FROM_NOTIFICATION;
import static com.m_learning.utils.ConstantApp.FROM_WHERE;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_ASSIGNMENT;
import static com.m_learning.utils.ConstantApp.TYPE_ADD_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_CHAT;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_REGISTER;
import static com.m_learning.utils.ConstantApp.TYPE_COURSE_VIEW;
import static com.m_learning.utils.ConstantApp.TYPE_DELETE_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_EDIT_LECTURE;
import static com.m_learning.utils.ConstantApp.TYPE_LECTURER;
import static com.m_learning.utils.ConstantApp.TYPE_USER;
import static com.m_learning.utils.ConstantApp.TYPE_VIEW_LECTURE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.m_learning.R;
import com.m_learning.feature.Chat.view.ChatActivity;
import com.m_learning.feature.courses.CourseDetailsActivity;
import com.m_learning.feature.general.SplashActivity;
import com.m_learning.feature.lecturerHome.LecturerMainActivity;
import com.m_learning.feature.notifications.NotificationsActivity;
import com.m_learning.utils.AppSharedData;
import com.m_learning.utils.MLearningApp;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class FireBaseMessagingService extends FirebaseMessagingService {

    private final static AtomicInteger c = new AtomicInteger(0);

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Log.e("////remoteMessageSize", remoteMessage.getData().keySet().size() + "");

            for (String kay : remoteMessage.getData().keySet()) {
                Log.e("////remoteMessageKeys", "Kay : " + kay + " :: " + remoteMessage.getData().get(kay));
            }

            String title = getString(R.string.app_name);
            String body = getString(R.string.newNotification);
            Intent intent = SplashActivity.newInstance(getApplicationContext(), FROM_NOTIFICATION);
            if (remoteMessage.getData().size() > 0) {
                String type = remoteMessage.getData().get("type");
                title = remoteMessage.getData().get("title");
                body = remoteMessage.getData().get("body");
                String key = remoteMessage.getData().get("key");
                String uid = remoteMessage.getData().get("uid");
                String eventId = remoteMessage.getData().get("eventId");
                if (AppSharedData.isUserLogin() && AppSharedData.getUserData() != null && !TextUtils.isEmpty(type)) {
                    String myId = AppSharedData.getUserData().getUserId();
                    AppSharedData.setBadgeCount(myId, AppSharedData.getBadgeCount(myId + 1));
                    switch (type) {
                        case TYPE_CHAT:
                            if (!MLearningApp.isChatActivityOpen()) {
                                sendChatNotification(title, body, eventId, uid);
                            }
                            break;
                        case TYPE_COURSE_REGISTER:
                        case TYPE_COURSE_VIEW:
                        case TYPE_VIEW_LECTURE:
                        case TYPE_ADD_ASSIGNMENT:
                            sendNotificationToLecturer(type, intent, title, body, getID());
                            break;
                        case TYPE_ADD_LECTURE:
                        case TYPE_EDIT_LECTURE:
                        case TYPE_DELETE_LECTURE:
                            sendNotificationToLecture(type, intent, title, body, getID());
                            break;
                        default:
                            sendNotification(intent, title, body, getID());
                            break;

                    }
                } else {
                    intent = SplashActivity.newInstance(getApplicationContext(), FROM_NOTIFICATION);
                    sendNotification(intent, title, body, getID());
                }
            } else {
                sendNotification(intent, title, body, getID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendChatNotification(String title, String message, String roomId, String receiverUid) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(FROM_WHERE, FROM_CHAT);
        if (AppSharedData.isUserLogin() && AppSharedData.getUserData() != null)
            intent = ChatActivity.getIntent(getApplicationContext(), roomId, receiverUid, FROM_NOTIFICATION);
        int id = getID();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        send(pendingIntent, title, message, id);
    }


    private void sendNotificationToLecturer(String type, Intent resultIntent, String title, String message, int id) {
        try {
            if (AppSharedData.getUserData() != null && AppSharedData.getUserData().getUserType().equals(TYPE_LECTURER)) {
                resultIntent = new Intent(this, LecturerMainActivity.class);
                resultIntent.putExtra(FROM_WHERE, FROM_NOTIFICATION);
                resultIntent.setAction(type);
            }
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, id
                    , resultIntent, PendingIntent.FLAG_ONE_SHOT);
            send(resultPendingIntent, title, message, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendNotificationToLecture(String type, Intent resultIntent, String title, String message, int id) {
        try {
            if (AppSharedData.getUserData() != null && AppSharedData.getUserData().getUserType().equals(TYPE_USER)) {
                resultIntent = new Intent(this, CourseDetailsActivity.class);
                resultIntent.putExtra(FROM_WHERE, FROM_NOTIFICATION);
                resultIntent.setAction(type);
            }
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, id
                    , resultIntent, PendingIntent.FLAG_ONE_SHOT);
            send(resultPendingIntent, title, message, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendNotification(Intent resultIntent, String title, String message, int id) {
        try {
            if (!AppSharedData.isUserLogin()) {
                resultIntent = new Intent(this, NotificationsActivity.class);
                resultIntent.putExtra(FROM_WHERE, FROM_NOTIFICATION);
            }
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, id
                    , resultIntent, PendingIntent.FLAG_ONE_SHOT);
            send(resultPendingIntent, title, message, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void send(PendingIntent pendingIntent, String title, String message, int id) {
        int notificationNumber = 0;
        if (AppSharedData.isUserLogin() && AppSharedData.getUserData() != null) {
            notificationNumber = AppSharedData.getBadgeCount(AppSharedData.getUserData().getUserId());
        }
        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = Objects.requireNonNull(notifyManager).getNotificationChannel(String.valueOf(id));
            if (mChannel == null) {
                mChannel = new NotificationChannel(String.valueOf(id), title, importance);
                mChannel.setDescription(message);
                mChannel.enableVibration(true);
                mChannel.setShowBadge(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifyManager.createNotificationChannel(mChannel);
            }
        }
        builder = new NotificationCompat.Builder(this, String.valueOf(id));
        builder.setContentTitle(title)  // required
                .setSmallIcon(R.mipmap.ic_launcher) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(title + " : " + message)
                .setContentText(message)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        builder.setNumber(notificationNumber)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);

        notifyManager.notify(id, builder.build());
    }

    private int getID() {
        return c.incrementAndGet();
    }

}
