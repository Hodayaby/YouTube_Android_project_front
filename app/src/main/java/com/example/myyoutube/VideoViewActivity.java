package com.example.myyoutube;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

public class VideoViewActivity extends AppCompatActivity {

    private TextView videoTitleTextView;
    private TextView videoAuthorTextView;
    private TextView videoViewsTextView;
    private ImageView videoChannelImageView;
    private VideoView videoView;
    private EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        // Initialize views
        videoTitleTextView = findViewById(R.id.videoTitle);
        videoAuthorTextView = findViewById(R.id.videoArtist);
        videoViewsTextView = findViewById(R.id.videoDetails);
        videoChannelImageView = findViewById(R.id.videoChannelImage);
        videoView = findViewById(R.id.videoView);
        commentEditText = findViewById(R.id.commentBox); // Ensure this matches the ID in XML

        // Get data from intent
        String videoTitle = getIntent().getStringExtra("videoTitle");
        String videoAuthor = getIntent().getStringExtra("videoAuthor");
        String videoViews = getIntent().getStringExtra("videoViews");
        String videoUploadTime = getIntent().getStringExtra("videoUploadTime");
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
    }
}
