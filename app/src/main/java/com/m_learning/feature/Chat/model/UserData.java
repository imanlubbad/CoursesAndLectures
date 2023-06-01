package com.m_learning.feature.Chat.model;


import static com.m_learning.utils.ConstantApp.ANDROID;

import com.m_learning.network.model.UserInfo;
import com.m_learning.utils.AppSharedData;

public class UserData {
    private String myIdMember;
    private String userFullName;
    private String deviceToken;
    private String userOs;
    private String userImage;
    private int status;

    public String getMyIdMember() {
        return myIdMember;
    }

    public void setMyIdMember(String myIdMember) {
        this.myIdMember = myIdMember;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUserOs() {
        return userOs;
    }

    public void setUserOs(String userOs) {
        this.userOs = userOs;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserData() {
    }

    public UserData(UserInfo userData) {
        this.myIdMember = userData.getUserId() + "";
        this.userFullName = userData.getFirstName()+" "+userData.getLastName();
        this.userImage = userData.getUserImage();
        this.deviceToken = userData.getDeviceToken();
        this.userOs = ANDROID;
    }

    public UserData(String userId, String userName, String userImage, String deviceToken, String userOs, int status) {
        this.myIdMember = userId;
        this.userFullName = userName;
        this.userImage = userImage;
        this.deviceToken = deviceToken;
        this.userOs = userOs;
        this.status = status;
    }

    public UserData(String userId, String userName, String userImage) {
        this.myIdMember = userId;
        this.userFullName = userName;
        this.userImage = userImage;
    }
}
