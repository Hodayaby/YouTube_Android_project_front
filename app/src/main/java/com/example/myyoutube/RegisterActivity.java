package com.example.myyoutube;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsernameEditText;
    private EditText registerPasswordEditText;
    private EditText registerConfirmPasswordEditText;
    private EditText displayNameEditText;
    private ImageView profileImageView;
    private TextView imageUploadErrorTextView;
    private Button uploadImageButton;
    private Button registerButton;
    private Bitmap selectedBitmap;
    private UserListManager userListManager;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme based on ThemeManager
        setDarkMode(ThemeManager.isDarkMode());

        setContentView(R.layout.activity_register);

        // Initialize UserListManager
        userListManager = UserListManager.getInstance();

        // Initialize UI elements
        registerUsernameEditText = findViewById(R.id.registerUsername);
        registerPasswordEditText = findViewById(R.id.registerPassword);
        registerConfirmPasswordEditText = findViewById(R.id.registerConfirmPassword);
        displayNameEditText = findViewById(R.id.displayName);
        profileImageView = findViewById(R.id.profileImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        registerButton = findViewById(R.id.registerButton);
        imageUploadErrorTextView = findViewById(R.id.imageUploadError);

        // Set click listener for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input values
                String username = registerUsernameEditText.getText().toString();
                String password = registerPasswordEditText.getText().toString();
                String confirmPassword = registerConfirmPasswordEditText.getText().toString();
                String displayName = displayNameEditText.getText().toString();

                // Validate registration details
                if (validateRegistration(username, password, confirmPassword, displayName)) {
                    // Check if username already exists
                    if (userListManager.isUsernameTaken(username)) {
                        registerUsernameEditText.setError("Username already exists");
                        return;
                    }

                    // Save user details
                    saveUserDetails(username, password, displayName, selectedBitmap);

                    // Display registration success message
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to login screen
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Set click listener for upload image button
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageUploadOptions();
            }
        });
    }

    // Show options to upload an image (either take a photo or choose from gallery)
    private void showImageUploadOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Image")
                .setItems(new String[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Take photo
                            if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
                            } else {
                                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                            }
                        } else {
                            // Choose from gallery
                            Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhotoIntent, PICK_IMAGE_REQUEST);
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri imageUri = data.getData();
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    profileImageView.setImageBitmap(selectedBitmap);
                    // Hide error message if image is uploaded successfully
                    imageUploadErrorTextView.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
                profileImageView.setImageBitmap(selectedBitmap);
                // Hide error message if image is uploaded successfully
                imageUploadErrorTextView.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to validate registration inputs
    private boolean validateRegistration(String username, String password, String confirmPassword, String displayName) {
        boolean isValid = true;

        // Validate username
        if (username.isEmpty()) {
            isValid = false;
            setErrorOnEditText(registerUsernameEditText, "Username is required");
        } else {
            clearErrorOnEditText(registerUsernameEditText);
        }

        // Validate password
        if (password.isEmpty() || password.length() < 8 || !isPasswordValid(password)) {
            isValid = false;
            setErrorOnEditText(registerPasswordEditText, "Password must be at least 8 characters long and include letters and numbers");
        } else {
            clearErrorOnEditText(registerPasswordEditText);
        }

        // Validate confirmPassword
        if (!confirmPassword.equals(password)) {
            isValid = false;
            setErrorOnEditText(registerConfirmPasswordEditText, "Passwords do not match");
        } else {
            clearErrorOnEditText(registerConfirmPasswordEditText);
        }

        // Validate displayName
        if (displayName.isEmpty()) {
            isValid = false;
            setErrorOnEditText(displayNameEditText, "Display Name is required");
        } else {
            clearErrorOnEditText(displayNameEditText);
        }

        // Validate profile picture
        if (selectedBitmap == null) {
            isValid = false;
            Toast.makeText(this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
            imageUploadErrorTextView.setVisibility(View.VISIBLE);
            imageUploadErrorTextView.setText("Please upload a profile picture");
        } else {
            imageUploadErrorTextView.setVisibility(View.GONE);
        }

        return isValid;
    }

    // Method to set error on EditText
    private void setErrorOnEditText(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
    }

    // Method to clear error on EditText
    private void clearErrorOnEditText(EditText editText) {
        editText.setError(null);
    }

    // Method to save user details
    private void saveUserDetails(String username, String password, String displayName, Bitmap profileImage) {
        // Create a new user instance
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDisplayName(displayName);
        user.setProfileImage(profileImage);

        // Add the user to the UserListManager
        userListManager.addUser(user);
    }

    // Method to encode Bitmap to String
    private String encodeBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Method to decode String to Bitmap
    private Bitmap decodeStringToBitmap(String encodedString) {
        byte[] decodedByte = Base64.decode(encodedString, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // Method to check if password is valid (contains letters and numbers)
    private boolean isPasswordValid(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasLetter && hasDigit;
    }

    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
