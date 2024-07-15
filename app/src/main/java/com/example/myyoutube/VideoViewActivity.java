package com.example.myyoutube;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class VideoViewActivity extends AppCompatActivity {

    private TextView videoTitleTextView;
    private TextView videoAuthorTextView;
    private TextView videoViewsTextView;
    private ImageView videoChannelImageView;
    private VideoView videoView;
    private EditText commentEditText;
    private ImageButton likeButton;
    private ImageButton dislikeButton;

    private Post currentPost;
    private UserListManager userListManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme based on ThemeManager
        setDarkMode(ThemeManager.isDarkMode());

        setContentView(R.layout.activity_video_view);

        // Initialize UserListManager
        userListManager = UserListManager.getInstance();
        currentUser = userListManager.getCurrentUser();

        // Initialize views
        videoTitleTextView = findViewById(R.id.videoTitle);
        videoAuthorTextView = findViewById(R.id.videoArtist);
        videoViewsTextView = findViewById(R.id.videoDetails);
        videoChannelImageView = findViewById(R.id.videoChannelImage);
        videoView = findViewById(R.id.videoView);
        commentEditText = findViewById(R.id.commentBox);
        likeButton = findViewById(R.id.likeButton);
        dislikeButton = findViewById(R.id.dislikeButton);

        // Get data from intent
        String videoTitle = getIntent().getStringExtra("videoTitle");
        String videoAuthor = getIntent().getStringExtra("videoAuthor");
        String videoViews = getIntent().getStringExtra("videoViews");
        String videoUploadTime = getIntent().getStringExtra("videoUploadTime");
        int videoPic = getIntent().getIntExtra("videoPic", 0);
        int videoChannelImage = getIntent().getIntExtra("videoChannelImage", 0);
        String videoUri = getIntent().getStringExtra("videoUri");

        // Set data to views
        videoTitleTextView.setText(videoTitle);
        videoAuthorTextView.setText(videoAuthor);
        videoViewsTextView.setText(videoViews + " • " + videoUploadTime);
        videoChannelImageView.setImageResource(videoChannelImage);

        // Set up the VideoView
        videoView.setVideoURI(Uri.parse(videoUri));
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        // Initialize currentPost
        currentPost = new Post(videoAuthor, videoTitle, videoPic, videoChannelImage, videoViews, videoUploadTime, videoUri);

        // Check if the post is liked or disliked and update button colors
        updateLikeDislikeButtons();

        // Set up like button click listener
        likeButton.setOnClickListener(v -> handleLikeClick());

        // Set up dislike button click listener
        dislikeButton.setOnClickListener(v -> handleDislikeClick());
    }

    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateLikeDislikeButtons() {
        if (currentUser != null) {
            if (currentUser.isLiked(currentPost)) {
                likeButton.setColorFilter(getResources().getColor(R.color.blue));
            } else {
                likeButton.clearColorFilter();
            }

            if (currentUser.isDisliked(currentPost)) {
                dislikeButton.setColorFilter(getResources().getColor(R.color.blue));
            } else {
                dislikeButton.clearColorFilter();
            }
        }
    }

    private void handleLikeClick() {
        if (currentUser == null || currentUser.getUsername() == null) {
            Toast.makeText(this, "Please login to like videos", Toast.LENGTH_SHORT).show();
        } else {
            if (currentUser.isLiked(currentPost)) {
                currentUser.removeLikedPost(currentPost);
            } else {
                if (currentUser.isDisliked(currentPost)) {
                    currentUser.removeDislikedPost(currentPost);
                }
                currentUser.addLikedPost(currentPost);
            }
        }
        updateLikeDislikeButtons();
    }

    private void handleDislikeClick() {
        if (currentUser == null || currentUser.getUsername() == null) {
            Toast.makeText(this, "Please login to dislike videos", Toast.LENGTH_SHORT).show();
        } else {
            if (currentUser.isDisliked(currentPost)) {
                currentUser.removeDislikedPost(currentPost);
            } else {
                if (currentUser.isLiked(currentPost)) {
                    currentUser.removeLikedPost(currentPost);
                }
                currentUser.addDislikedPost(currentPost);
            }
        }
        updateLikeDislikeButtons();
    }
}
