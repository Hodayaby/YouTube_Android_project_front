package com.example.myyoutube;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Video.class, Comment.class, User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();
    public abstract CommentDao commentDao();
    public abstract UserDao userDao();
}

