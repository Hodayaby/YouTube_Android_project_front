package com.example.myyoutube;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final Context context;
    private VideoApi videoApi;

    public UserRepository(Context context) {
        this.context = context;
        videoApi = RetrofitClient.getRetrofitInstance().create(VideoApi.class);
    }

    public LiveData<Resource<Boolean>> registerUser(String username, String password, Bitmap profilePicture) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();

        // Prepare the form fields
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);

        // Convert Bitmap to ByteArray
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.JPEG, 100, bos); // You can change the format and quality
        byte[] imageBytes = bos.toByteArray();

        // Create the RequestBody for the profile picture
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        String id = UUID.randomUUID().toString();
        MultipartBody.Part profilePicturePart = MultipartBody.Part.createFormData("profilePicture", "profile_" + id + ".jpg", requestFile);

        // Make the network call
        videoApi.registerUser(usernameBody, passwordBody, profilePicturePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.postValue(new Resource<>(true, null));
                } else {
                    result.postValue(new Resource<>(false, "Registration failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.postValue(new Resource<>(false, t.getMessage()));
            }
        });

        return result;
    }
}

