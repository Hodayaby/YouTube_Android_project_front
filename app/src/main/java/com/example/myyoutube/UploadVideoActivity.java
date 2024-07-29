package com.example.myyoutube;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UploadVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private EditText videoTitleEditText;
    private EditText videoDescriptionEditText;
    private Button uploadVideoButton;
    private Button uploadImageButton;
    private Button submitUploadButton;
    private ImageView previewImageView;
    private ImageView uploadVideoSuccess;
    private ImageView uploadImageSuccess;
    private Uri videoUri;
    private Uri imageUri;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadvideo);

        // Initialize UI components
        videoTitleEditText = findViewById(R.id.uploadVidName);
        videoDescriptionEditText = findViewById(R.id.videoDes);
        uploadVideoButton = findViewById(R.id.uploadVideoBtn);
        uploadImageButton = findViewById(R.id.uploadImageBtn);
        submitUploadButton = findViewById(R.id.submitUploadBtn);
        previewImageView = findViewById(R.id.previewImage);
        uploadVideoSuccess = findViewById(R.id.uploadVideoSuccess);
        uploadImageSuccess = findViewById(R.id.uploadImageSuccess);

        // Get current logged-in user
        currentUser = UserListManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            // If no user is logged in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set onClick listeners for buttons
        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoPicker();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        submitUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUpload();
            }
        });
    }

    // Open video picker intent
    private void openVideoPicker() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    // Open image picker intent
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    // Handle the result from the file pickers
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();

            // Log the URI to debug
            Log.d("UploadVideoActivity", "Selected Video URI: " + videoUri.toString());

            uploadVideoSuccess.setVisibility(View.VISIBLE); // Show success indicator
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            previewImageView.setImageURI(imageUri);
            uploadImageSuccess.setVisibility(View.VISIBLE); // Show success indicator
        }
    }

    // Upload video and create new post
    private void handleUpload() {
        String videoTitle = videoTitleEditText.getText().toString().trim();
        String videoDescription = videoDescriptionEditText.getText().toString().trim();

        // Check if the video title is entered
        if (TextUtils.isEmpty(videoTitle)) {
            Toast.makeText(this, "Please enter a video title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the video is selected
        if (videoUri == null) {
            Toast.makeText(this, "Please select a video", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the image is selected
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique file name for the video
        String vidName = "video_" + System.currentTimeMillis() + ".mp4";
        File vidFile = vidToFile(videoUri, vidName);

        // Check if video file creation was successful
        if (vidFile == null) {
            Toast.makeText(this, "Failed to save video file.", Toast.LENGTH_SHORT).show();
            return;
        }

        String videoFilePath = vidFile.getAbsolutePath();

        // Get user details
        String author = currentUser.getUsername();
        String uploadTime = "Just now";
        String views = "0 views";
        String imageUriStr = imageUri.toString();

        // Log the video file path to debug
        Log.d("UploadVideoActivity", "Video File Path: " + videoFilePath);

        // Create a new Post object with the video details
        Post newPost = new Post(author, videoDescription, imageUriStr, currentUser.getProfileImage() != null ? R.drawable.ic_profile : 0, views, uploadTime, videoFilePath);

        // Add the post to the global post list
        UserListManager.getInstance().addPost(newPost);

        // Notify the user of successful upload
        Toast.makeText(this, "Video uploaded successfully!", Toast.LENGTH_SHORT).show();

        // Update the adapter to reflect the new post
        HomeScreenActivity.postsListAdapter.notifyItemInserted(0);
        HomeScreenActivity.recyclerView.scrollToPosition(0); // Scroll to the new post

        // Redirect to home screen
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);

        finish();
    }

    // Convert URI to File
    private File vidToFile(Uri videoUri, String vidName) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(videoUri);
            File videoFile = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), vidName);
            OutputStream outputStream = new FileOutputStream(videoFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return videoFile;
        } catch (Exception e) {
            Log.e("UploadVideoActivity", "Error converting video URI to file", e);
            return null;
        }
    }
}
