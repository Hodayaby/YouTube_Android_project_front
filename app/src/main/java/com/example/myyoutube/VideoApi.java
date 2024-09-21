package com.example.myyoutube;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST("/api/tokens")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Multipart
    @POST("/api/users/{id}/videos")
    Call<UploadResponse> uploadVideo(
            @Header("Authorization") String token,
            @Path("id") int userId,
            @Part MultipartBody.Part videoFile,
            @Part MultipartBody.Part thumbnail,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );

    @POST("/api/videos/{id}/like")
    Call<ResponseBody> likeDislikeVideo(
            @Header("Authorization") String token,
            @Path("id") int videoId,
            @Body LikeRequest likeRequest
    );

    @DELETE("/api/users/{userId}/videos/{videoId}")
    Call<ResponseBody> deleteVideo(
            @Header("Authorization") String token,
            @Path("userId") int userId,
            @Path("videoId") int videoId
    );

    @Multipart
    @PUT("/api/users/{userId}/videos/{videoId}")
    Call<ResponseBody> editVideo(
            @Header("Authorization") String token,
            @Path("userId") int userId,
            @Path("videoId") int videoId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part videoFile, // Optional, pass null if not editing the video file
            @Part MultipartBody.Part thumbnail  // Optional, pass null if not editing the thumbnail
    );

    @POST("/api/users/{userId}/videos/{videoId}/comment")
    @FormUrlEncoded
    Call<ResponseBody> addComment(
            @Header("Authorization") String token,
            @Path("userId") int userId,
            @Path("videoId") int videoId,
            @Field("user") String username,
            @Field("text") String commentText
    );
}

