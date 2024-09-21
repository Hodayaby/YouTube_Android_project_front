package com.example.myyoutube;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @Multipart
    @POST("/api/users")
    Call<ResponseBody> registerUser(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part profilePicture
    );
}

