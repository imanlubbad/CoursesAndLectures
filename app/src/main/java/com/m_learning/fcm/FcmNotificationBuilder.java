package com.m_learning.fcm;


import static com.m_learning.utils.ConstantApp.TYPE_CHAT;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AAAA_A2mwH4:APA91bG9hpAlMSU3reSGvRq4IBTHQz4GUXEKjioVx-TmWlNG5fYxtCFf8bZ8B6k3Hc2u_JXKxtkGHHgJUTYvwvQw8ERcxG21y72FbZqq0lYX-wuDpOiSrBYHmQi3gnMZ674wM6dLBl3T";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String SENDER_ID = "1082560790654";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";

    private static final String KEY_BODY = "body";

    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DATA = "data";
    private static final String KEY_EVENT_ID = "eventId";
    private static final String KEY_UID = "uid";
    private static final String KEY_KEY = "key";
    private static final String KEY_BADGE = "badge";

    private String mType;
    private String mOS;
    private String mTitle;
    private String mMessage;
    private String mUid;
    private String mBadge = "0";
    private String mEventId;
    private String mReceiverFirebaseToken;
    private ArrayList<String> mReceiverUsersFirebaseToken;
    private String mkey = "";

    private FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }

    public FcmNotificationBuilder type(String type) {
        mType = type;
        return this;
    }

    public FcmNotificationBuilder os(String OS) {
        mOS = OS;
        return this;
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }


    public FcmNotificationBuilder uid(String uid) {
        mUid = uid;
        return this;
    }

    public FcmNotificationBuilder key(String key) {
        mkey = key;
        return this;
    }

    public FcmNotificationBuilder eventId(String eventId) {
        mEventId = eventId;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public void send() {
        RequestBody requestBody = null;
        try {
            if (mType.equals(TYPE_CHAT)) {
                requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
            } else requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());


            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, AUTH_KEY)
                    .url(FCM_URL)
                    .post(requestBody)
                    .build();

            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "onResponse: " + response.body().string());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);
        jsonObjectBody.put(KEY_PRIORITY, "high");
        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TYPE, mType);
        jsonObjectData.put(KEY_TITLE, mTitle);
        jsonObjectData.put(KEY_BODY, mMessage);
        jsonObjectData.put(KEY_UID, mUid);
        jsonObjectData.put(KEY_BADGE, mBadge);
        jsonObjectData.put(KEY_KEY, mkey);
        jsonObjectData.put(KEY_EVENT_ID, mEventId);
        jsonObjectBody.put(KEY_DATA, jsonObjectData);
        Log.e("FCM notification: ", jsonObjectBody.toString());
        return jsonObjectBody;
    }
}
