package com.example.myyoutube;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    private String username;
    private String content;
    private String timestamp;
    private String profileImageBase64;

    public Comment(String username, String content, String profileImageBase64) {
        this.username = username;
        this.content = content;
        this.timestamp = getCurrentTimestamp();
        this.profileImageBase64 = profileImageBase64;
    }

    public Comment(String username, String content) {
        this.username = username;
        this.content = content;
        this.timestamp = getCurrentTimestamp();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
