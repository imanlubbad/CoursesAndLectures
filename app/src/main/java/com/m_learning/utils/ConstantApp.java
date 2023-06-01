package com.m_learning.utils;

import android.annotation.SuppressLint;
import android.provider.Settings;

public interface ConstantApp {
    int SPLASH_TIME_OUT = 3000;

    @SuppressLint("HardwareIds")
    String HEADER_UUID_PHONE_VALUE = Settings.Secure.getString(MLearningApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);// Device UUID

    String ADMIN_ID = "0";


    String TYPE_VIDEO = "video";
    String TYPE_IMAGE = "image";


    String FROM_WHERE = "from_where";
    String USER_INFO = "user_info";
    String COURSE_INFO = "course_info";
    String LECTURE = "lecture";
    String ACTION_CLOSE = "close";
    String ACTION_NOTHING = "nothing";
    int ACTION_ADD = 0;
    int ACTION_EDIT = 1;
    int ACTION_DELETE = 2;

    String ANDROID = "android";

    // user type and sp type
    String TYPE_USER = "user";
    String TYPE_LECTURER = "lecturer";

    //file types
    String VIDEO_TYPE = "video";
    String TYPE_CHAT = "chat";
    String TYPE_COURSE_REGISTER = "course_register";
    String TYPE_COURSE_VIEW = "course_view";
    String TYPE_ADD_ASSIGNMENT = "add_assignment";
    String TYPE_VIEW_LECTURE = "view_lecture";
    String TYPE_ADD_LECTURE = "add_lecture";
    String TYPE_EDIT_LECTURE = "edit_lecture";
    String TYPE_DELETE_LECTURE = "delete_lecture";


    // permissions and activity result actions
    int REQUEST_PERMISSIONS_R_W_STORAGE_CAMERA = 1001;
    int REQUEST_SINGLE_IMAGE_NEW = 1002;
    int REQUEST_IMAGE_CAPTURE = 1003;
    int REQUEST_CODE_CLICK_IMAGE = 1004;
    int ACTION_PIC_VIDEO = 1005;
    int PICKFILE_RESULT_CODE = 1006;
    int ACTION_CAPTURE_VIDEO = 1007;

    int ACTION_OPEN_URL = 1029;

    // screens
    int FROM_OTHERS = 0;
    int FROM_SPLASH = 1;
    int FROM_LOGIN = 3;
    int FROM_SIGN_UP = 4;
    int FROM_CHAT = 6;
    int FROM_HOME = 8;
    int FROM_NOTIFICATION = 9;
    int FROM_MY_COURSES = 10;
    int FROM_COURSES = 11;
    int FROM_LECTURES = 12;
    int FROM_STUDENTS = 13;

    int FROM_NOTIFICATION_LIST = 21;


    // for chat
    String RECEIVER = "receiver";
    String RECEIVER_ID = "receiverId";
    String TB_CHAT_ROOMS = "ChatRooms";
    String TB_RECENT = "Recent";
    String TB_SEEN = "Seen";
    String TYPE_TEXT = "text";
    String TB_Users = "Users";


}
