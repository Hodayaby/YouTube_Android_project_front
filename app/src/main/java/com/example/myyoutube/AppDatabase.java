package com.example.myyoutube;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Video.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();
}

