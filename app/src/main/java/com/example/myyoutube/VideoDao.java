package com.example.myyoutube;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideos(List<Video> videos);

    @Query("SELECT * FROM videos")
    List<Video> getAllVideos();

    @Query("SELECT * FROM videos WHERE id = :videoId")
    Video getVideoById(int videoId);

    @Query("DELETE FROM videos")
    void deleteAllVideos();
}
