package com.m_learning.utils;

import java.io.Serializable;

public class MediaData implements Serializable {
    private String imagePath;
    private String type;
    private String videoPath;

    public MediaData(String path, String type, String videoPath) {
        this.imagePath = path;
        this.type = type;
        this.videoPath = videoPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoPath() {
        return videoPath;
    }

}
