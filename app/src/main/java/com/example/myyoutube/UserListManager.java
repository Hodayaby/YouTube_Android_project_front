package com.example.myyoutube;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListManager {
    private static final UserListManager INSTANCE = new UserListManager();
    private static List<User> userList = new ArrayList<>();
    private static List<Post> allPosts = new ArrayList<>(); // Static list to hold all posts
    private User currentUser;

    private UserListManager() {
    }

    public static UserListManager getInstance() {
        return INSTANCE;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void addUser(User user) {
        userList.add(user);
    }

    public void removeUser(User user) {
        userList.remove(user);
    }

    public User getUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean isUsernameTaken(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Post> getAllPosts() {
        return allPosts;
    }

    public void addPost(Post post) {
        allPosts.add(0, post);
        User user = getUserByUsername(post.getAuthor());
        if (user != null) {
            user.addUserPost(post);
        }
    }

    public void removePost(Post post) {
        allPosts.remove(post);
        User user = getUserByUsername(post.getAuthor());
        if (user != null) {
            user.removeUserPost(post);
        }
    }

    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
