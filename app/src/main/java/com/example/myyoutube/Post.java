package com.example.myyoutube;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String author;
    private String content;
    private int likes;
    private int pic;
    private int channelImage;
    private String views;
    private String uploadTime;
    private String videoUri;
    private int dislikes;
    private List<Comment> comments;

    public Post() {
        this.pic = R.drawable.lalaland;
        this.channelImage = R.drawable.lalachanel;
        this.comments = new ArrayList<>();
    }

    public Post(String author, String content, int pic, int channelImage, String views, String uploadTime, String videoUri) {
        this.author = author;
        this.content = content;
        this.pic = pic;
        this.channelImage = channelImage;
        this.views = views;
        this.uploadTime = uploadTime;
        this.videoUri = videoUri;
        this.comments = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getPic() { return pic; }
    public void setPic(int pic) { this.pic = pic; }

    public int getChannelImage() { return channelImage; }
    public void setChannelImage(int channelImage) { this.channelImage = channelImage; }

    public String getViews() { return views; }
    public void setViews(String views) { this.views = views; }

    public String getUploadTime() { return uploadTime; }
    public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }

    public String getVideoUri() { return videoUri; }
    public void setVideoUri(String videoUri) { this.videoUri = videoUri; }

    public void addLike() {
        this.likes++;
    }

    public void addDislike() {
        this.dislikes++;
    }

    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() {
        return dislikes;
    }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }

    // Add getter and setter for comments
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Add a method to add a comment
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    // Add a method to remove a comment
    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }
}
