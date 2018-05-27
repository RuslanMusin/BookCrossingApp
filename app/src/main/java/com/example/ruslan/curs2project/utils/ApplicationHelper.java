/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.ruslan.curs2project.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ruslan.curs2project.Application;
import com.example.ruslan.curs2project.managers.DatabaseHelper;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list.BooksListActivity;
import com.example.ruslan.curs2project.ui.start.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

/**
 * Created by Kristina on 10/28/16.
 */

public class ApplicationHelper {

    private static final String TAG = ApplicationHelper.class.getSimpleName();
    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static void initDatabaseHelper(android.app.Application application) {
        databaseHelper = DatabaseHelper.getInstance(application);
        databaseHelper.init();
    }

    public static StorageReference getStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }


    public static void initUserState(Application application) {
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    Log.d(TAG_LOG,"logout");
                    LoginActivity.start(application);
                } else {
                    Log.d(TAG_LOG,"try to login");
                    DatabaseReference reference = RepositoryProvider.getUserRepository().readUser(UserRepository.getCurrentId());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            NavigationPresenter.setCurrentUser(user);
                            BooksListActivity.start(application.getApplicationContext());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }
}
