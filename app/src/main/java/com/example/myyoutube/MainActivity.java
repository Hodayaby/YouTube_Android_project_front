//package com.example.myyoutube;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    //@Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        EdgeToEdge.enable(this);
////        setContentView(R.layout.activity_login);
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
////            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
////            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
////            return insets;
////        });
////    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.posts);
//
//        RecyclerView lstPosts = findViewById(R.id.lstPosts);
//        final PostsListAdapter adapter = new PostsListAdapter(this);
//        lstPosts.setAdapter(adapter);
//
//        lstPosts.setLayoutManager(new LinearLayoutManager(this));
//
//        List<Post> posts = new ArrayList<>();
//        posts.add(new Post("Alice", "Hello world", R.drawable.lalaland));
//        posts.add(new Post("Alice2", "Hello world2", R.drawable.lalaland));
//        posts.add(new Post("Alice3", "Hello world3", R.drawable.lalaland));
//        posts.add(new Post("Alice4", "Hello world4", R.drawable.lalaland));
//
//        adapter.setPosts(posts);
//    }
//
//}