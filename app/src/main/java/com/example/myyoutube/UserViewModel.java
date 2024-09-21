package com.example.myyoutube;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    public UserViewModel(Application app) {
        super(app);
        this.userRepository = new UserRepository(app);
    }

    public LiveData<Resource<Boolean>> registerUser(String username, String password, Bitmap profilePicture) {
        return userRepository.registerUser(username, password, profilePicture);
    }
}

