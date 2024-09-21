package com.example.myyoutube;

import android.content.Context;

import java.io.File;

public enum FileType {
    THUMBNAIL,
    VIDEO;

    public String getFileName(Video video) {
        return video.getId() + "_" + this.name();
    }

    public File getFilePath(Context context, Video video) {
        return new File(context.getFilesDir(), getFileName(video));
    }
}
