package com.example.myyoutube;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
//יאללה
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

    // Function to add a new post
    public void addPost(Post post) {
        allPosts.add(0, post);
        User user = getUserByUsername(post.getAuthor());
        if (user != null) {
            user.addUserPost(post);
        }
    }

    // Function to update an existing post
    public void updatePost(Post post) {
        int index = allPosts.indexOf(post);
        if (index != -1) {
            allPosts.set(index, post); // Update existing post
        } else {
            // Optionally, handle the case where the post does not exist
            Log.e("UserListManager", "Post not found: " + post.getVideoUri());
        }
    }

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
            user.removeUserPost(post);
        }

        // Log the size of the list after removal
        Log.d("UserListManager", "Total posts after removal: " + allPosts.size());
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
