package com.example.ruslan.curs2project.api;

import android.support.annotation.NonNull;

import com.example.ruslan.curs2project.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiFactory {

    private static volatile BooksService booksService;

    private static volatile MessageService messageService;

    private ApiFactory() {
    }

    @NonNull
    public static BooksService getBooksService() {
        BooksService service = booksService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = booksService;
                if (service == null) {
                    service = booksService = buildRetrofit().create(BooksService.class);
                }
            }
        }
        return service;
    }

    public static void recreate() {
        OkHttpProvider.recreate();
        booksService = buildRetrofit().create(BooksService.class);
    }


    public static void setBooksService(BooksService booksService) {
        ApiFactory.booksService = booksService;
    }

    @NonNull
    public static MessageService getMessageService() {
        MessageService service = messageService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = messageService;
                if (service == null) {
                    service = messageService = buildMessageRetrofit().create(MessageService.class);
                }
            }
        }
        return service;
    }

    @NonNull
    private static Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT)
                .client(OkHttpProvider.provideClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @NonNull
    private static Retrofit buildMessageRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.MESSAGING_URL)
                .client(OkHttpProvider.provideMessageClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
