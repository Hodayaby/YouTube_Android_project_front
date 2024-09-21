package com.example.myyoutube;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRepository {
    private final Context context;
    private VideoApi videoApi;
    private VideoDao videoDao;
    private CommentDao commentDao;

    public VideoRepository(Context context) {
        this.context = context;
        videoApi = RetrofitClient.getRetrofitInstance().create(VideoApi.class);
        AppDatabase appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        videoDao = appDatabase.videoDao();
        commentDao = appDatabase.commentDao();
    }

    public LiveData<Resource<List<Video>>> getAllVideos() {
        MutableLiveData<Resource<List<Video>>> videosLiveData = new MutableLiveData<>();

        videoApi.getAllVideos().enqueue(new Callback<VideosResult>() {
            @Override
            public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                if (response.isSuccessful()) {
                    // Save videos to Room and update LiveData
                    new Thread(() -> {
                        List<Video> videos = response.body().getVideos();
                        try {
                            videoDao.insertVideos(videos);
                            ArrayList<Comment> comments = new ArrayList<>();
                            for (Video video : videos) {
                                for (Comment comment : video.getComments()) {
                                    comment.setVideoId(video.getId());
                                    comments.add(comment);
                                }
                            }
                            commentDao.insertComments(comments);
                        } catch (Exception e) {
                        }
                        videosLiveData.postValue(Resource.success(videos));
                    }).start();
                } else {
                    new Thread(() -> {
                        List<Video> allVideos = getVideos();
                        videosLiveData.postValue(Resource.success(allVideos));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<VideosResult> call, Throwable t) {
//                videosLiveData.postValue(Resource.error(t.getMessage()));
                new Thread(() -> {
                    List<Video> allVideos = getVideos();
                    videosLiveData.postValue(Resource.success(allVideos));
                }).start();
            }
        });

        return videosLiveData;
    }

    private List<Video> getVideos() {
        List<Video> videos = videoDao.getAllVideos();
        for (Video video : videos) {
            List<Comment> comments = commentDao.getCommentsByVideoId(video.getId());
            video.setComments(comments);
        }
        return videos;
    }

    private void downloadFile(MutableLiveData<Resource<Boolean>> result, File file, String filepath) {
        String uploadsUrl = "http://localhost:8000/";
        if (filepath.startsWith(uploadsUrl)) {
            filepath = filepath.substring(uploadsUrl.length());
        }

        videoApi.downloadFile(filepath).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        result.setValue(Resource.success(true)); // Return success if download completes
                    } catch (IOException e) {
                        result.setValue(Resource.error("File download failed"));
                    }
                } else {
                    result.setValue(Resource.error("Error downloading file"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(Resource.error("Download failed: " + t.getMessage()));
            }
        });
    }

    public LiveData<Resource<Boolean>> downloadFile(User user) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();

        // Generate the filename based on the video ID and file type
        String fileName = user.getId() + "_" + FileType.PROFILE.name();
        File file = new File(context.getFilesDir(), fileName);

        // Check if the file exists
        if (file.exists()) {
            // If the file exists, return true immediately
            result.setValue(Resource.success(true));
            return result;
        }

        String filepath = user.getProfilePicture();
        downloadFile(result, file, filepath);

        return result;
    }

    public LiveData<Resource<Boolean>> downloadFile(Video video, FileType fileType) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();

        // Generate the filename based on the video ID and file type
        String fileName = video.getId() + "_" + fileType.name();
        File file = new File(context.getFilesDir(), fileName);

        // Check if the file exists
        if (file.exists()) {
            // If the file exists, return true immediately
            result.setValue(Resource.success(true));
            return result;
        }

        String filepath = fileType == FileType.VIDEO ? video.getUrl() : video.getThumbnail();
        downloadFile(result, file, filepath);

        return result;
    }

    public LiveData<Resource<Video>> getVideoById(int id) {
        MutableLiveData<Resource<Video>> liveData = new MutableLiveData<>();
        new Thread(() -> {
            Video video = videoDao.getVideoById(id);
            if (video == null) {
                liveData.postValue(Resource.error("Video not found for id " + id));
            } else {
                List<Comment> comments = commentDao.getCommentsByVideoId(id);
                video.setComments(comments);
                liveData.postValue(Resource.success(video));
            }
        }).start();
        return liveData;
    }

    public LiveData<Resource<Video>> uploadVideo(User currentUser, File videoFile, File thumbnail, String title, String description) {
        MutableLiveData<Resource<Video>> result = new MutableLiveData<>();

        // Prepare video file as Multipart
        RequestBody requestVideoFile = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("videoFile", videoFile.getName(), requestVideoFile);

        // Prepare thumbnail as Multipart
        RequestBody requestThumbnail = RequestBody.create(MediaType.parse("image/*"), thumbnail);
        MultipartBody.Part thumbnailPart = MultipartBody.Part.createFormData("thumbnail", thumbnail.getName(), requestThumbnail);

        // Prepare title and description as RequestBody
        RequestBody requestTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody requestDescription = RequestBody.create(MediaType.parse("text/plain"), description);

        // Make the API call
        videoApi.uploadVideo(currentUser.getToken(), currentUser.getId(), videoPart, thumbnailPart, requestTitle, requestDescription).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Video video = response.body().getVideo();
                    new Thread(() -> {
                        videoDao.insert(video);
                        result.postValue(Resource.success(video));
                    }).start();
                } else {
                    result.postValue(Resource.error("Failed to upload video"));
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                result.postValue(Resource.error(t.getMessage()));
            }
        });

        return result;
    }
}
