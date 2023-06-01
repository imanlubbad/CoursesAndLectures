package com.m_learning.feature.Chat.model;


import static android.text.style.TtsSpan.TYPE_TEXT;

public class Chat {

    public int status;
    public String messageId;
    public String groupId;
    public String senderName;
    public String senderImage;
    public String receiverName;
    public String receiverImage;
    public String senderUid;
    public String receiverUid;
    public long timestamp;
    public String message;
    public String mediaUrl;
    public String type = TYPE_TEXT;

    public Chat() {
    }

    public Chat(String groupId, String senderName, String receiverName, String senderImage, String receiverImage,
                String senderUid, String receiverUid, String message, long timestamp) {
        this.groupId = groupId;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Chat(String type, String senderName, String receiverName, String senderImage, String receiverImage,
                String senderUid, String receiverUid, String message, String mediaUrl, long timestamp, int status) {
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.mediaUrl = mediaUrl;
        this.type = type;
        this.status = status;
    }
}