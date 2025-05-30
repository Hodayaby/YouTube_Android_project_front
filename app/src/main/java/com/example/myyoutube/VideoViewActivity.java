package com.example.myyoutube;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.IOException;
import java.util.List;

public class VideoViewActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;

    private TextView videoTitleTextView;
    private TextView videoAuthorTextView;
    private TextView videoDescTextView;
    private TextView videoViewsTextView;
    private ImageView videoChannelImageView;
    private VideoView videoView;
    private EditText commentEditText;
    private ImageButton likeButton;
    private ImageButton dislikeButton;
    private Button addCommentButton;
    private LinearLayout commentsContainer;
    private ImageButton shareButton;
    private ImageButton editVideoButton;
    private ImageButton deleteVideoButton;

    private Bitmap imageBitmap;
    private ImageView previewImageView;
    private ImageView uploadImageSuccess;

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
        videoDescTextView = findViewById(R.id.videoDesc);
        videoChannelImageView = findViewById(R.id.videoChannelImage);
        videoView = findViewById(R.id.videoView);
        commentEditText = findViewById(R.id.commentBox);
        likeButton = findViewById(R.id.likeButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        addCommentButton = findViewById(R.id.addCommentButton);
        commentsContainer = findViewById(R.id.commentsContainer);
        shareButton = findViewById(R.id.shareButton);
        editVideoButton = findViewById(R.id.editButton);
        deleteVideoButton = findViewById(R.id.deleteButton);
        previewImageView = findViewById(R.id.previewImage);
        uploadImageSuccess = findViewById(R.id.uploadImageSuccess);

        // Get data from intent
        String videoTitle = getIntent().getStringExtra("videoTitle");
        String videoAuthor = getIntent().getStringExtra("videoAuthor");
        String videoDesc = getIntent().getStringExtra("videoDescription");
        String videoViews = getIntent().getStringExtra("videoViews");
        String videoUploadTime = getIntent().getStringExtra("videoUploadTime");
        String videoPic = getIntent().getStringExtra("videoPic");
        int videoChannelImage = getIntent().getIntExtra("videoChannelImage", 0);
        String videoUri = getIntent().getStringExtra("videoUri");


        // Set data to views
        videoTitleTextView.setText(videoTitle);
        videoAuthorTextView.setText(videoAuthor);
        videoDescTextView.setText(videoDesc);
        videoViewsTextView.setText(videoViews + " • " + videoUploadTime);
        videoChannelImageView.setImageResource(videoChannelImage);

        // Set up the VideoView
        videoView.setVideoURI(Uri.parse(videoUri));
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        // Initialize currentPost
        currentPost = new Post(videoAuthor, videoTitle, videoDesc, videoPic, videoChannelImage, videoViews, videoUploadTime, videoUri);

        // Check if the post is liked or disliked and update button colors
        updateLikeDislikeButtons();

        // Load comments
        loadComments();

        // Set up like button click listener
        likeButton.setOnClickListener(v -> handleLikeClick());

        // Set up dislike button click listener
        dislikeButton.setOnClickListener(v -> handleDislikeClick());

        // Set up add comment button click listener
        addCommentButton.setOnClickListener(v -> {
            if (currentUser == null || currentUser.getUsername() == null) {
                Toast.makeText(VideoViewActivity.this, "Please login to add comments", Toast.LENGTH_SHORT).show();
            } else {
                addComment();
            }
        });

        // Set up share button click listener
        shareButton.setOnClickListener(v -> {
            if (currentUser == null || currentUser.getUsername() == null) {
                Toast.makeText(VideoViewActivity.this, "Please login to share videos", Toast.LENGTH_SHORT).show();
            } else {
                shareVideo(videoUri);
            }
        });

        // If user is not logged in, hide the comment box
        if (currentUser == null || currentUser.getUsername() == null) {
            commentEditText.setVisibility(View.GONE);
            addCommentButton.setVisibility(View.GONE);
        } else {
            commentEditText.setVisibility(View.VISIBLE);
            addCommentButton.setVisibility(View.VISIBLE);
        }

        // Set up edit video button click listener
        editVideoButton.setOnClickListener(v -> {
            if (currentUser == null || !videoAuthor.equals(currentUser.getUsername())) {
                Toast.makeText(VideoViewActivity.this, "You cannot edit this video", Toast.LENGTH_SHORT).show();
            } else {
                showEditVideoDialog();
            }
        });

        // Set up delete video button click listener
        deleteVideoButton.setOnClickListener(v -> {
            if (currentUser == null || !videoAuthor.equals(currentUser.getUsername())) {
                Toast.makeText(VideoViewActivity.this, "You cannot delete this video", Toast.LENGTH_SHORT).show();
            } else {
                deleteVideo();
            }
        });
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

    // Load comments and update the view
    private void loadComments() {
        commentsContainer.removeAllViews();
        List<Comment> comments = currentPost.getComments();
        for (Comment comment : comments) {
            addCommentView(comment);
        }
    }

    // Add comment to the post and update the view
    private void addComment() {
        String commentText = commentEditText.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String profileImageBase64 = null;
        if (currentUser.getProfileImage() != null) {
            profileImageBase64 = userListManager.encodeBitmapToBase64(currentUser.getProfileImage());
        }

        Comment newComment = new Comment(currentUser.getUsername(), commentText, profileImageBase64);
        currentPost.addComment(newComment);
        loadComments(); // Reload comments to display the new comment
        commentEditText.setText(""); // Clear the comment box
    }

    // Add a comment view with edit and delete buttons if applicable
    private void addCommentView(Comment comment) {
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_layout, commentsContainer, false);

        TextView usernameTextView = commentView.findViewById(R.id.commentUsername);
        TextView commentTextView = commentView.findViewById(R.id.commentContent);
        TextView timestampTextView = commentView.findViewById(R.id.commentTimestamp);
        ImageView userImageView = commentView.findViewById(R.id.commentUserImage);
        ImageButton editButton = commentView.findViewById(R.id.editCommentButton);
        ImageButton deleteButton = commentView.findViewById(R.id.deleteCommentButton);
        LinearLayout editDeleteContainer = commentView.findViewById(R.id.editDeleteContainer);

        usernameTextView.setText(comment.getUsername());
        commentTextView.setText(comment.getContent());
        timestampTextView.setText(comment.getTimestamp());

        // Set the user image
        if (comment.getProfileImageBase64() != null) {
            Bitmap userImageBitmap = userListManager.decodeBase64ToBitmap(comment.getProfileImageBase64());
            userImageView.setImageBitmap(userImageBitmap);
        } else {
            userImageView.setImageResource(R.drawable.ic_profile); // Default image
        }

        if (currentUser != null && comment.getUsername().equals(currentUser.getUsername())) {
            editDeleteContainer.setVisibility(View.VISIBLE); // Show the container
            editButton.setOnClickListener(v -> editComment(comment, commentTextView));
            deleteButton.setOnClickListener(v -> deleteComment(comment, commentView));
        } else {
            editDeleteContainer.setVisibility(View.GONE); // Hide the container
        }

        commentsContainer.addView(commentView);
    }

    // Edit comment and update the view
    private void editComment(Comment comment, TextView commentTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Comment");

        final EditText input = new EditText(this);
        input.setText(comment.getContent());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String editedComment = input.getText().toString();
            if (!TextUtils.isEmpty(editedComment)) {
                comment.setContent(editedComment);
                comment.setTimestamp(new Comment(currentUser.getUsername(), editedComment).getTimestamp()); // Update timestamp
                commentTextView.setText(editedComment);
            } else {
                Toast.makeText(VideoViewActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Delete comment and remove the view
    private void deleteComment(Comment comment, View commentView) {
        currentPost.removeComment(comment);
        commentsContainer.removeView(commentView);
    }

    // Share video using Android Sharesheet
    private void shareVideo(String videoUri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video: " + videoUri);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // Show custom dialog to edit video details

    private void showEditVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Video");

        View view = getLayoutInflater().inflate(R.layout.edit_video_dialog, null);
        EditText videoTitleInput = view.findViewById(R.id.editVideoTitle);
        EditText videoDescInput = view.findViewById(R.id.editVideoDesc);

        // Set the current video title and description in the input fields
        videoTitleInput.setText(currentPost.getContent());
        videoDescInput.setText(currentPost.getDescription());

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String editedTitle = videoTitleInput.getText().toString();
            String editedDesc = videoDescInput.getText().toString();

            if (!TextUtils.isEmpty(editedTitle) && !TextUtils.isEmpty(editedDesc)) {
                currentPost.setContent(editedTitle);
                currentPost.setDescription(editedDesc);
                videoTitleTextView.setText(editedTitle);
                videoDescTextView.setText(editedDesc);
                userListManager.updatePost(currentPost); // Update post in the manager
                Toast.makeText(VideoViewActivity.this, "Video details updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(VideoViewActivity.this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }




    // Delete video
    private void deleteVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Video");

        builder.setMessage("Are you sure you want to delete this video?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            UserListManager.getInstance().removePost(currentPost);
            Toast.makeText(VideoViewActivity.this, "Video deleted successfully", Toast.LENGTH_SHORT).show();

            // Redirect to home screen
            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    // Open image picker to change the thumbnail
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
            Uri imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                previewImageView.setImageBitmap(imageBitmap);
                uploadImageSuccess.setVisibility(View.VISIBLE); // Show success indicator
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
