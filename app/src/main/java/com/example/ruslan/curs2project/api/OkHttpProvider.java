package com.example.ruslan.curs2project.api;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;

/**
 * Created by Nail Shaykhraziev on 25.03.2018.
 */
public class OkHttpProvider {

    private static volatile OkHttpClient sClient;

    private static volatile OkHttpClient messageClient;

    private OkHttpProvider() {
    }

    @NonNull
    public static OkHttpClient provideClient() {
        OkHttpClient client = sClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = sClient;
                if (client == null) {
                    client = sClient = buildClient();
                }
            }
        }
        return client;
    }

    @NonNull
    public static OkHttpClient provideMessageClient() {
        OkHttpClient client = messageClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = messageClient;
                if (client == null) {
                    client = messageClient = buildMessageClient();
                }
            }
        }
        return client;
    }

    public static void recreate() {
        sClient = null;
        sClient = buildClient();
    }

    @NonNull
    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder().build();
    }

    @NonNull
    private static OkHttpClient buildMessageClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor.create())
//                .addInterceptor(LoggingInterceptor.create())
                .build();
    }
}
