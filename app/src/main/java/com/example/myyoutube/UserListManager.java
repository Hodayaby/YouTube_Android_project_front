package com.example.myyoutube;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListManager {

    // Singleton instance of UserListManager
    private static final UserListManager INSTANCE = new UserListManager();

    // Static lists to hold users and posts
    private static List<User> userList = new ArrayList<>();
    private static List<Post> allPosts = new ArrayList<>(); // Static list to hold all posts

    // Variable to track the current user
    private User currentUser;

    // Private constructor to prevent instantiation
    private UserListManager() {
    }

    // Method to get the singleton instance of UserListManager
    public static UserListManager getInstance() {
        return INSTANCE;
    }

    // Method to get the list of all users
    public List<User> getUserList() {
        return userList;
    }

    // Method to add a new user
    public void addUser(User user) {
        userList.add(user);
    }

    // Method to remove a user
    public void removeUser(User user) {
        userList.remove(user);
    }

    // Method to get a user by their username
    public User getUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Method to check if a username is already taken
    public boolean isUsernameTaken(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Method to get the currently logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Method to set the currently logged-in user
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // Method to get the list of all posts
    public List<Post> getAllPosts() {
        return allPosts;
    }

    // Method to add a new post
    public void addPost(Post post) {
        allPosts.add(0, post); // Add the post to the beginning of the list
        User user = getUserByUsername(post.getAuthor());
        if (user != null) {
            user.addUserPost(post); // Add the post to the user's list of posts
        }
    }

    // Method to update an existing post
    public void updatePost(Post post) {
        int index = allPosts.indexOf(post);
        if (index != -1) {
            allPosts.set(index, post); // Update the existing post
        } else {
            // Optionally, handle the case where the post does not exist
            Log.e("UserListManager", "Post not found: " + post.getVideoUri());
        }
    }

    // Method to remove a post
    public void removePost(Post post) {
        Log.d("UserListManager", "Removing post: " + post.getVideoUri());

        boolean removed = allPosts.removeIf(p -> p.getVideoUri().equals(post.getVideoUri()));
        if (removed) {
            Log.d("UserListManager", "Post removed: " + post.getVideoUri());
        } else {
            Log.d("UserListManager", "Post not found in allPosts: " + post.getVideoUri());
        }

        User user = getUserByUsername(post.getAuthor());
        if (user != null) {
            user.removeUserPost(post); // Remove the post from the user's list of posts
        }

        // Log the size of the list after removal
        Log.d("UserListManager", "Total posts after removal: " + allPosts.size());
    }

    // Method to encode a Bitmap to a Base64 string
    public String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Method to decode a Base64 string to a Bitmap
    public Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
