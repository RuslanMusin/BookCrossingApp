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

import com.example.ruslan.curs2project.Application;
import com.example.ruslan.curs2project.managers.DatabaseHelper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Kristina on 10/28/16.
 */

public class ApplicationHelper {

    private static final String TAG = ApplicationHelper.class.getSimpleName();
    private static DatabaseHelper databaseHelper;

    private static GoogleCredential googleCredential;

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static void initDatabaseHelper(android.app.Application application) {
        databaseHelper = DatabaseHelper.getInstance(application);
        databaseHelper.init();
    }

    public static void initToken(Application application) throws IOException {
        InputStream stream = application.getResources().getAssets().open("curs2project-firebase-adminsdk-2oldz-8cad1b8e78.json");

        googleCredential = GoogleCredential
            .fromStream(stream)
            .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));


    }

    public static GoogleCredential getCredential() {
        return googleCredential;
    }
}
