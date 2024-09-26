package com.example.myyoutube;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VideoApi {

    @GET("/api/videos/allVideos")
    Call<VideosResult> getAllVideos();

    @GET("/api/videos")
    Call<VideosResult> getPopularVideos();

    @GET("/api/videos/{id}")
    Call<Video> getVideoById(@Path("id") int id);

    @GET("{path}")
    Call<ResponseBody> downloadFile(@Path(value = "path", encoded = true) String path);
}

