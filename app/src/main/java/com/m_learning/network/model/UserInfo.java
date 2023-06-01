package com.m_learning.network.model;

import static com.m_learning.utils.ConstantApp.ANDROID;
import static com.m_learning.utils.ConstantApp.TYPE_USER;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInfo implements Serializable {

    private String address = "";

    private String mobile = "";

    private String os = ANDROID;

    private String userId = "";

    private String userImage = "";

    private String userType = "";

    private String firstName;

    private String middleName;

    private String lastName;

    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public String getShortName() {
        return firstName + " " + lastName;
    }


    private Long createdAt;

    private String deviceToken = "";

    private long dob = 0;

    private String email = "";

    private String gender = "Female";

    private Integer isActive = 1;

    public int getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(int courseNo) {
        this.courseNo = courseNo;
    }

    private int courseNo = 0;

//    @PropertyName("myCoursesList")
//    ArrayList<String> myCoursesList = new ArrayList<String>();


    ArrayList<Course> myCourses = new ArrayList<Course>();

    public ArrayList<Course> getMyCourses() {
        return myCourses;
    }

    public void setMyCourses(ArrayList<Course> myCourses) {
        this.myCourses = myCourses;
    }

//    public ArrayList<String> getMyCoursesList() {
//        return myCoursesList;
//    }
//
//    public void setMyCoursesList(ArrayList<String> myCoursesList) {
//        this.myCoursesList = myCoursesList;
//    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("createdAt", createdAt);
        result.put("deviceToken", deviceToken);
        result.put("dob", dob);
        result.put("email", email);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("middleName", middleName);
        result.put("address", address);
        result.put("mobile", mobile);
        result.put("os", ANDROID);
        result.put("userId", userId);
        result.put("userImage", userImage);
        result.put("userType", TYPE_USER);
        result.put("isActive", isActive);
//        result.put("gender", gender);

        return result;
    }
}
