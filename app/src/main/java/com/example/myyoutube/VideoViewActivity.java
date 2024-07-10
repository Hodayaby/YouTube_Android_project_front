package com.example.myyoutube;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

public class VideoViewActivity extends AppCompatActivity {

    private TextView videoTitleTextView;
    private TextView videoAuthorTextView;
    private TextView videoViewsTextView;
    private TextView videoUploadTimeTextView;
    private ImageView videoPicImageView;
    private ImageView videoChannelImageView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        // Initialize views
        videoTitleTextView = findViewById(R.id.videoTitle);
        videoAuthorTextView = findViewById(R.id.videoAuthor);
        videoViewsTextView = findViewById(R.id.videoViews);
        videoUploadTimeTextView = findViewById(R.id.videoUploadTime);
        videoPicImageView = findViewById(R.id.videoPic);
        videoChannelImageView = findViewById(R.id.videoChannelImage);
        videoView = findViewById(R.id.videoView);

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
        videoViewsTextView.setText(videoViews);
        videoUploadTimeTextView.setText(videoUploadTime);
        videoPicImageView.setImageResource(videoPic);
        videoChannelImageView.setImageResource(videoChannelImage);

        // Set up the VideoView
        videoView.setVideoURI(Uri.parse(videoUri));
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();
    }
}
