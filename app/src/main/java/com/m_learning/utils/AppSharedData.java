package com.m_learning.utils;


import static android.content.Context.MODE_PRIVATE;

import com.google.gson.Gson;
import com.m_learning.network.model.UserInfo;

public class AppSharedData {

    public static final String SHARED_APP_DATA = "MLearning";
    public static final String SHARED_USER_DATA = "user";
    private static final String SHARED_IS_USER_LOGIN = "is_user_login";
    private static final Gson gson = new Gson();

    private static final String SHARED_FCM_TOKEN = "fcm_token";
    private static final String SHARED_BADGE_COUNT = "BadgeCount";


    public static boolean isUserLogin() {
        return MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getBoolean(SHARED_IS_USER_LOGIN, false);
    }

    public static void setUserLogin(boolean login) {
        MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putBoolean(SHARED_IS_USER_LOGIN, login).apply();
    }


    public static void setUserData(UserInfo user) {
        MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .edit().putString(SHARED_USER_DATA, gson.toJson(user)).apply();
    }

    public static UserInfo getUserData() {
        UserInfo mUser = gson.fromJson(MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getString(SHARED_USER_DATA, null), UserInfo.class);
        return mUser;
    }


    public static void setFcmToken(String fcmToken) {
        MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putString(SHARED_FCM_TOKEN, fcmToken).apply();
    }

    public static String getFcmToken() {
        return MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getString(SHARED_FCM_TOKEN, "");
    }   public static void setBadgeCount(String userId, int count) {
        MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE).edit()
                .putInt(SHARED_BADGE_COUNT + userId, count).apply();
    }

    public static int getBadgeCount(String userId) {
        return MLearningApp.getInstance().getSharedPreferences(SHARED_APP_DATA, MODE_PRIVATE)
                .getInt(SHARED_BADGE_COUNT + userId, 0);
    }

}
