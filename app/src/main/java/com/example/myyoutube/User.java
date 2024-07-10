package com.example.myyoutube;

import android.graphics.Bitmap;

public class User {
    private String username;
    private String password;
    private String displayName;
    private transient Bitmap profileImage; // transient to avoid serialization
    private String profileImageBase64; // Base64 representation of the profile image

    private static final User INSTANCE = new User(); // Singleton instance

    // Private constructor to prevent instantiation from other classes
    private User() {}

    public static User getInstance() {
        return INSTANCE;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }
}
