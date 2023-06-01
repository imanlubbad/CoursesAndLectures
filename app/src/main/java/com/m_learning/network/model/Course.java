package com.m_learning.network.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {

    private String lecturerName;

    private String lecturerId;

    private String courseCode;

    private Long courseDeadline;

    private String courseDescriptions;

    private String courseId;

    private String courseImage;

    public ArrayList<String> getCourseFiles() {
        return courseFiles;
    }

    public void setCourseFiles(ArrayList<String> courseFiles) {
        this.courseFiles = courseFiles;
    }

    private ArrayList<String> courseFiles;


    private String courseName;

    private Long createdAt;

    private Boolean isDeleted;

    private Boolean haveAssignment=true;

    private Integer isActive;

    private Integer studentsNo=0;

    private Integer viewsNo=0;

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getCourseDeadline() {
        return courseDeadline;
    }

    public void setCourseDeadline(Long courseDeadline) {
        this.courseDeadline = courseDeadline;
    }

    public String getCourseDescriptions() {
        return courseDescriptions;
    }

    public void setCourseDescriptions(String courseDescriptions) {
        this.courseDescriptions = courseDescriptions;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getHaveAssignment() {
        return haveAssignment;
    }

    public void setHaveAssignment(Boolean haveAssignment) {
        this.haveAssignment = haveAssignment;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getStudentsNo() {
        return studentsNo;
    }

    public void setStudentsNo(Integer studentsNo) {
        this.studentsNo = studentsNo;
    }

    public Integer getViewsNo() {
        return viewsNo;
    }

    public void setViewsNo(Integer viewsNo) {
        this.viewsNo = viewsNo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Course.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("lecturerName");
        sb.append('=');
        sb.append(((this.lecturerName == null) ? "<null>" : this.lecturerName));
        sb.append(',');
        sb.append("lecturerId");
        sb.append('=');
        sb.append(((this.lecturerId == null) ? "<null>" : this.lecturerId));
        sb.append(',');
        sb.append("courseCode");
        sb.append('=');
        sb.append(((this.courseCode == null) ? "<null>" : this.courseCode));
        sb.append(',');
        sb.append("courseDeadline");
        sb.append('=');
        sb.append(((this.courseDeadline == null) ? "<null>" : this.courseDeadline));
        sb.append(',');
        sb.append("courseDescriptions");
        sb.append('=');
        sb.append(((this.courseDescriptions == null) ? "<null>" : this.courseDescriptions));
        sb.append(',');
        sb.append("courseId");
        sb.append('=');
        sb.append(((this.courseId == null) ? "<null>" : this.courseId));
        sb.append(',');
        sb.append("courseImage");
        sb.append('=');
        sb.append(((this.courseImage == null) ? "<null>" : this.courseImage));
        sb.append(',');
        sb.append("courseName");
        sb.append('=');
        sb.append(((this.courseName == null) ? "<null>" : this.courseName));
        sb.append(',');
        sb.append("createdAt");
        sb.append('=');
        sb.append(((this.createdAt == null) ? "<null>" : this.createdAt));
        sb.append(',');
        sb.append("isDeleted");
        sb.append('=');
        sb.append(((this.isDeleted == null) ? "<null>" : this.isDeleted));
        sb.append(',');
        sb.append("haveAssignment");
        sb.append('=');
        sb.append(((this.haveAssignment == null) ? "<null>" : this.haveAssignment));
        sb.append(',');
        sb.append("isActive");
        sb.append('=');
        sb.append(((this.isActive == null) ? "<null>" : this.isActive));
        sb.append(',');
        sb.append("studantsNo");
        sb.append('=');
        sb.append(((this.studentsNo == null) ? "<null>" : this.studentsNo));
        sb.append(',');
        sb.append("viewsNo");
        sb.append('=');
        sb.append(((this.viewsNo == null) ? "<null>" : this.viewsNo));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}