package com.example.myyoutube;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRepository {
    private final Context context;
    private VideoApi videoApi;
    private VideoDao videoDao;

    public VideoRepository(Context context) {
        this.context = context;
        videoApi = RetrofitClient.getRetrofitInstance().create(VideoApi.class);
        videoDao = DatabaseClient.getInstance(context).getAppDatabase().videoDao();
    }

    public LiveData<Resource<List<Video>>> getAllVideos() {
        MutableLiveData<Resource<List<Video>>> videosLiveData = new MutableLiveData<>();

        videoApi.getAllVideos().enqueue(new Callback<VideosResult>() {
            @Override
            public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                if (response.isSuccessful()) {
                    // Save videos to Room and update LiveData
                    new Thread(() -> {
                        try {
                            videoDao.insertVideos(response.body().getVideos());
                        } catch (Exception e) {
                        }
                        videosLiveData.postValue(Resource.success(response.body().getVideos()));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<VideosResult> call, Throwable t) {
                videosLiveData.postValue(Resource.error(t.getMessage()));
            }
        });

        return videosLiveData;
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
        String uploadsUrl = "http://localhost:8000/";
        if (filepath.startsWith(uploadsUrl)) { // fixing server bug
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

        return result;
    }
}
