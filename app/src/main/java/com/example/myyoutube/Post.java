package com.example.myyoutube;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Post {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String author;
    private String content;
    private String imageUri; // שינוי ל-String כדי לתמוך ב-URI של תמונה
    private int channelImage;
    private String views;
    private String uploadTime;
    private String videoUri;

    // Static map to store comments for each post
    private static Map<String, List<Comment>> commentsMap = new HashMap<>();

    public Post() {
        // Default constructor
    }

    public Post(String author, String content, String imageUri, int channelImage, String views, String uploadTime, String videoUri) {
        this.author = author;
        this.content = content;
        this.imageUri = imageUri;
        this.channelImage = channelImage;
        this.views = views;
        this.uploadTime = uploadTime;
        this.videoUri = videoUri;
        commentsMap.putIfAbsent(videoUri, new ArrayList<>());
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public int getChannelImage() { return channelImage; }
    public void setChannelImage(int channelImage) { this.channelImage = channelImage; }

    public String getViews() { return views; }
    public void setViews(String views) { this.views = views; }

    public String getUploadTime() { return uploadTime; }
    public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }

    public String getVideoUri() { return videoUri; }
    public void setVideoUri(String videoUri) { this.videoUri = videoUri; }

    // Get comments from the static map
    public List<Comment> getComments() {
        return commentsMap.get(videoUri);
    }

    // Add a comment to the static map
    public void addComment(Comment comment) {
        commentsMap.get(videoUri).add(comment);
    }

    // Remove a comment from the static map
    public void removeComment(Comment comment) {
        commentsMap.get(videoUri).remove(comment);
    }
}
