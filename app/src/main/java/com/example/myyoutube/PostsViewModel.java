//package com.example.myyoutube;
//
//
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
//
//import java.util.List;
//
//public class PostsViewModel  {
//
//    private PostsRepository mRepository;
//
//    private LiveData<List<Post>> posts;
//
//    public PostsViewModel() {
//        mRepository = new PostsRepository();
//        posts = mRepository.getAll();
//    }
//
//    public LiveData<List<Post>> get() {
//        return posts;
//    }
//
//    public void add(Post post) {
//        mRepository.add(post);
//    }
//
//    public void delete(Post post) {
//        mRepository.delete(post);
//    }
//
//    public void reload() {
//        mRepository.reload();
//    }
//}
//
