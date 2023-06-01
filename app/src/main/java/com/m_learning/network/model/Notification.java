package com.m_learning.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notification implements Serializable {

    @SerializedName("id")
    @Expose
    private int notificationId;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("action")
    @Expose
    private String notificationAction;
    @SerializedName("action_id")
    @Expose
    private Integer actionId;
    @SerializedName("text")
    @Expose
    private String notificationMsg="";
    @SerializedName("seen")
    @Expose
    private Integer seen;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("sender_name")
    @Expose
    private String senderName;
    @SerializedName("sender_image")
    @Expose
    private String image;
    @SerializedName("sender_image100")
    @Expose
    private String image100;

    private String notificationImg;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getNotificationAction() {
        return notificationAction;
    }

    public void setNotificationAction(String notificationAction) {
        this.notificationAction = notificationAction;
    }

    public Integer getActionId() {
        return actionId != null ? actionId : 0;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }

    public void setNotificationMsg(String notificationMsg) {
        this.notificationMsg = notificationMsg;
    }

    public Integer getSeen() {
        return seen;
    }

    public void setSeen(Integer seen) {
        this.seen = seen;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getNotificationImg() {
        return notificationImg;
    }

    public void setNotificationImg(String notificationImg) {
        this.notificationImg = notificationImg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage100() {
        return image100;
    }

    public void setImage100(String image100) {
        this.image100 = image100;
    }
}
