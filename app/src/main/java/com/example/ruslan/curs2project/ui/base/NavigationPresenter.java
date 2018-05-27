package com.example.ruslan.curs2project.ui.base;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ruslan.curs2project.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NavigationPresenter {

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        NavigationPresenter.currentUser = currentUser;
    }

    public void loadUserPhoto(ImageView photoView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(currentUser.getPhotoUrl());

        Glide.with(photoView.getContext())
                .load(storageReference)
                .into(photoView);
    }
}
