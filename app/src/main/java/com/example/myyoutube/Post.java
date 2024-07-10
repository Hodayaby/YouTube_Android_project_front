package com.example.myyoutube;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    private String videoUri; // הוספת URI של הווידאו

    public Post() {
        this.pic = R.drawable.lalaland;
        this.channelImage = R.drawable.lalachanel;
    }

    public Post(String author, String content, int pic, int channelImage, String views, String uploadTime, String videoUri) {
        this.author = author;
        this.content = content;
        this.pic = pic;
        this.channelImage = channelImage;
        this.views = views;
        this.uploadTime = uploadTime;
        this.videoUri = videoUri; // אתחול ה-URI של הווידאו
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

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
}
