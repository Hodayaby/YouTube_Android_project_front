package com.example.myyoutube;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    private int id;
    private String user;
    private String text;

    private String timestamp;
    private String profileImageBase64;

    public Comment(String username, String content, String profileImageBase64) {
        this.user = username;
        this.text = content;
        this.timestamp = getCurrentTimestamp();
        this.profileImageBase64 = profileImageBase64;
    }

    public Comment(String username, String content) {
        this.user = username;
        this.text = content;
        this.timestamp = getCurrentTimestamp();
    }

    public String getUsername() {
        return user;
    }

    public void setUsername(String username) {
        this.user = username;
    }

    public String getContent() {
        return text;
    }

    public void setContent(String content) {
        this.text = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
