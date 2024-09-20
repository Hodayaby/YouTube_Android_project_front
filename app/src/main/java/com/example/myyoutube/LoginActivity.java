package com.example.myyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView loginErrorTextView;
    private TextView usernameErrorTextView;
    private TextView passwordErrorTextView;
    private UserListManager userListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme based on ThemeManager
        setDarkMode(ThemeManager.isDarkMode());

        setContentView(R.layout.activity_login);

        // Initialize UserListManager
        userListManager = UserListManager.getInstance();

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginErrorTextView = findViewById(R.id.login_error);
        usernameErrorTextView = findViewById(R.id.username_error);
        passwordErrorTextView = findViewById(R.id.password_error);

        // Login button click listener
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Validate username and password
            if (validateLogin(username, password)) {
                // Load current user to UserListManager
                loadCurrentUser(username);

                // Proceed to HomeScreenActivity
                Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish(); // Finish LoginActivity to prevent going back to it
            }
        });

        // Register link click listener
        Button registerButton = findViewById(R.id.registerMoveBtn);
        registerButton.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
    }

    // Validate login credentials
    private boolean validateLogin(String username, String password) {
        boolean isValid = true;
        boolean userExists = false;

        // Validate username
        if (TextUtils.isEmpty(username)) {
            usernameErrorTextView.setText("Username is required");
            isValid = false;
        } else {
            usernameErrorTextView.setText("");
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordErrorTextView.setText("Password is required");
            isValid = false;
        } else if (password.length() < 8) {
            passwordErrorTextView.setText("Password must be at least 8 characters long");
            isValid = false;
        } else {
            passwordErrorTextView.setText("");
        }

        // If both fields are valid, further validation can be added
        if (isValid) {
            User user = userListManager.getUserByUsername(username);
            if (user != null) {
                userExists = true;
                if (user.getPassword().equals(password)) {
                    return true;
                } else {
                    passwordErrorTextView.setText("Incorrect password");
                    return false;
                }
            }
            if (!userExists) {
                usernameErrorTextView.setText("Username does not exist");
            }
        }

        return false;
    }

    // Load current user details into UserListManager
    private void loadCurrentUser(String username) {
        User user = userListManager.getUserByUsername(username);
        if (user != null) {
            userListManager.setCurrentUser(user);
        }
    }

    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
