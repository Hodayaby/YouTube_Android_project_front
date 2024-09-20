package com.example.myyoutube;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class VideoViewModel extends AndroidViewModel {
    private VideoRepository videoRepository;

    public VideoViewModel(Application app) {
        super(app);
        videoRepository = new VideoRepository(app);
    }

    public LiveData<Resource<List<Video>>> getAllVideos() {
        return videoRepository.getAllVideos();
    }

    public LiveData<Resource<Boolean>> downloadFile(Video video, FileType fileType) {
        return videoRepository.downloadFile(video, fileType);
    }
}
