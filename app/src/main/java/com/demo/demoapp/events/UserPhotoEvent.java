package com.demo.demoapp.events;

import android.graphics.Bitmap;

import com.demo.demoapp.domain.ReminderWords;

public class UserPhotoEvent {
    private Bitmap userPhoto;

    public UserPhotoEvent(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Bitmap getUserPhotoBitmap() {
        return userPhoto;
    }
}

