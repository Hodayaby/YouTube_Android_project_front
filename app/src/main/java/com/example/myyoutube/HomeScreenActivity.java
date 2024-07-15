package com.example.myyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HomeScreenActivity extends AppCompatActivity implements PostsListAdapter.PostsAdapterListener {

    private List<Post> postsList;
    private List<Post> allPostsList; // For storing all posts separately
    private PostsListAdapter postsListAdapter;
    private EditText searchBar;
    private ImageView profileButton;
    private TextView noResultsText;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    private TextView usernameTextView;
    private TextView displayNameTextView;
    private ImageView profileImageView;

    private UserListManager userListManager;
    private User currentUser;
    private boolean showingFavoriteVideos = false; // To track if we are showing favorite videos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set theme based on ThemeManager
        setDarkMode(ThemeManager.isDarkMode());

        setContentView(R.layout.activity_homescreen);
        Log.d("HomeScreenActivity", "HomeScreenActivity Started");
        Toast.makeText(this, "HomeScreenActivity Started", Toast.LENGTH_SHORT).show();

        // Initialize UserListManager
        userListManager = UserListManager.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.searchBar);
        profileButton = findViewById(R.id.btnAccount);
        noResultsText = findViewById(R.id.noResultsText);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Initialize navigation header views
        View headerView = navigationView.getHeaderView(0);
        usernameTextView = headerView.findViewById(R.id.nav_username);
        displayNameTextView = headerView.findViewById(R.id.nav_greeting);
        profileImageView = headerView.findViewById(R.id.nav_profile_image);

        // Set up drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Set up menu button click listener to open drawer
        findViewById(R.id.menuButton).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        Log.d("HomeScreenActivity", "Views initialized");

        // Load current user profile image and details
        updateUserDetails();

        Log.d("HomeScreenActivity", "Profile image and details loaded");

        // Initialize post list and adapter
        postsList = new ArrayList<>();
        allPostsList = new ArrayList<>(); // Initialize all posts list
        postsListAdapter = new PostsListAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postsListAdapter);

        // Load videos from JSON file
        loadVideosFromJSON();

        Log.d("HomeScreenActivity", "Videos loaded from JSON");

        // Set up search bar text change listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the posts based on search input
                postsListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        // Set up home button click listener to scroll to the top of the list and reset the search
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            searchBar.setText(""); // Clear the search bar
            recyclerView.scrollToPosition(0); // Scroll to the top

            // If showing favorite videos, reload all videos
            if (showingFavoriteVideos) {
                postsListAdapter.setPosts(allPostsList);
                showingFavoriteVideos = false;
            }
        });

        // Set up profile button click listener (future implementation)
        profileButton.setOnClickListener(v -> {
            // Handle profile button click
        });

        // Set up login button click listener
        findViewById(R.id.btnAccount).setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Set up navigation item click listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_favorite_videos) {
                showFavoriteVideos();
            } else if (id == R.id.nav_dark_mode) {
                // Handle dark mode click
                boolean isDarkMode = !ThemeManager.isDarkMode();
                ThemeManager.setDarkMode(isDarkMode);
                setDarkMode(isDarkMode);
                updateMenuTitle(navigationView.getMenu().findItem(R.id.nav_dark_mode), isDarkMode); // Update the menu item
                recreate(); // Recreate the activity to apply the new theme
            } else if (id == R.id.nav_logout) {
                // Handle logout click
                Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Update the menu item initially
        updateMenuTitle(navigationView.getMenu().findItem(R.id.nav_dark_mode), ThemeManager.isDarkMode());
    }

    private void setDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateMenuTitle(MenuItem menuItem, boolean isDarkMode) {
        if (isDarkMode) {
            menuItem.setTitle("Light Mode");
            menuItem.setIcon(R.drawable.ic_sun); // Replace with the sun icon
        } else {
            menuItem.setTitle("Dark Mode");
            menuItem.setIcon(R.drawable.ic_dark); // Replace with the moon icon
        }
    }

    private void loadVideosFromJSON() {
        try {
            // Load JSON file from resources
            InputStream inputStream = getResources().openRawResource(R.raw.videos);
            String json = new Scanner(inputStream).useDelimiter("\\A").next();
            JSONArray jsonArray = new JSONArray(json);

            // Parse JSON and create Post objects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Post post = new Post(
                        jsonObject.getString("author"),
                        jsonObject.getString("content"),
                        getResources().getIdentifier(jsonObject.getString("thumbnail"), "drawable", getPackageName()),
                        getResources().getIdentifier(jsonObject.getString("channelImage"), "drawable", getPackageName()),
                        jsonObject.getString("views"),
                        jsonObject.getString("uploadTime"),
                        jsonObject.getString("videoUri")
                );
                postsList.add(post);
                allPostsList.add(post); // Add to all posts list as well
            }

            // Update adapter with the posts
            postsListAdapter.setPosts(postsList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUserDetails() {
        currentUser = userListManager.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getProfileImage() != null) {
                profileImageView.setImageBitmap(currentUser.getProfileImage());
                profileButton.setImageBitmap(currentUser.getProfileImage());
            } else {
                profileButton.setImageResource(R.drawable.ic_account);
            }
            if (currentUser.getUsername() != null) {
                usernameTextView.setText("username: " + currentUser.getUsername());
            }

            if (currentUser.getDisplayName() != null) {
                displayNameTextView.setText("Welcome " + currentUser.getDisplayName());
            }
        } else {
            usernameTextView.setText("No user logged in");
            displayNameTextView.setText("Welcome");
            profileButton.setImageResource(R.drawable.ic_account);
        }
    }

    private void showFavoriteVideos() {
        currentUser = userListManager.getCurrentUser();
        if (currentUser != null && currentUser.getUsername() != null) {
            List<Post> likedPosts = currentUser.getLikedPosts();
            postsListAdapter.setPosts(likedPosts);
            showingFavoriteVideos = true; // Update the flag
        } else {
            Toast.makeText(this, "Please login to see your favorite videos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostsFiltered(int count) {
        if (count == 0) {
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
        }
    }
}
