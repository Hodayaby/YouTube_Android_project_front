package com.example.myyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserProfileActivity extends AppCompatActivity implements PostsListAdapter.PostsAdapterListener {

    private PostsListAdapter postsListAdapter;
    private RecyclerView recyclerView;
    private TextView userFullName;
    private TextView userNameTextView;

    private VideoViewModel videoViewModel;
    private UserViewModel userViewModel;

    private Button editUserButton;
    private Button deleteUserButton;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Set theme based on ThemeManager
        setDarkMode(ThemeManager.isDarkMode());

        // Set the content view and log the activity start
        setContentView(R.layout.activity_userprofile);

        recyclerView = findViewById(R.id.userVideosRecyclerView);
        userFullName = findViewById(R.id.userFullName);
        userNameTextView = findViewById(R.id.userNameTextView);
        editUserButton = findViewById(R.id.editUserButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);

        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);
        String userName = intent.getStringExtra("userName");

        userFullName.setText(userName);
        userNameTextView.setText(userName);

        postsListAdapter = new PostsListAdapter(this, this, videoViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postsListAdapter);

        userViewModel.getCurrentUser().observe(this, userResource -> {
            if (userResource.isSuccess()) {
                currentUser = userResource.getData();
            } else {
                Toast.makeText(UserProfileActivity.this, userResource.getError(), Toast.LENGTH_SHORT).show();
            }

            if (currentUser == null || currentUser.getUsername() == null) {
                // do nothing
            } else {
                if (currentUser.getId() == userId) {
                    editUserButton.setVisibility(View.VISIBLE);
                    deleteUserButton.setVisibility(View.VISIBLE);
                }
            }
        });

        videoViewModel.getUserVideos(userId).observe(this, resource -> {
            if (resource.isSuccess()) {
                postsListAdapter.setPosts(resource.getData());
            } else {
                Toast.makeText(UserProfileActivity.this, resource.getError(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            Intent homeIntent = new Intent(UserProfileActivity.this, HomeScreenActivity.class);
            startActivity(homeIntent);
            finish();
        });

        deleteUserButton.setOnClickListener(v -> {
            userViewModel.deleteUser(currentUser).observe(this, resource -> {
                if (resource.isSuccess()) {
                    Toast.makeText(UserProfileActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    // Navigate to login screen
                    Intent homeIntent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    startActivity(homeIntent);
                    finish(); // Finish the current activity
                } else {
                    Toast.makeText(UserProfileActivity.this, resource.getError(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Set dark mode based on the current theme
    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onPostsFiltered(int count) {

    }
}
