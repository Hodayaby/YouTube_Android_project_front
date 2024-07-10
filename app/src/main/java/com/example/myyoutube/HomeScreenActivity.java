package com.example.myyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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

public class HomeScreenActivity extends AppCompatActivity {

    private List<Post> postsList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        postsListAdapter = new PostsListAdapter(this, count -> {
            // Show or hide the no results text view based on the filter result count
            if (count == 0) {
                noResultsText.setVisibility(View.VISIBLE);
            } else {
                noResultsText.setVisibility(View.GONE);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postsListAdapter);

        // Load videos from JSON file=
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
        });

        // Set up profile button click listener (future implementation)
        profileButton.setOnClickListener(v -> {
            // Handle profile button click
            // Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            // startActivity(intent);
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
                // Handle favorite videos click
                Toast.makeText(this, "Favorite Videos Clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_dark_mode) {
                // Handle dark mode click
                Toast.makeText(this, "Dark Mode Clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                // Handle logout click
                Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
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
                        jsonObject.getString("videoUri") // הוספת ה-URI של הווידאו
                );
                postsList.add(post);
            }

            // Update adapter with the posts
            postsListAdapter.setPosts(postsList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateUserDetails() {
        User currentUser = User.getInstance();
        if (currentUser.getProfileImage() != null) {
            profileImageView.setImageBitmap(currentUser.getProfileImage());
            profileButton.setImageBitmap(currentUser.getProfileImage()); // עדכון תמונת המשתמש בכפתור החשבון
        }

        if (currentUser.getUsername() != null) {
            usernameTextView.setText("username: " + currentUser.getUsername());
        }

        if (currentUser.getDisplayName() != null) {
            displayNameTextView.setText("Welcome " + currentUser.getDisplayName());
        }
    }
}
