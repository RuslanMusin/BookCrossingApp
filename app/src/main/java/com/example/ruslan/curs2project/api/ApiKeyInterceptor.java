package com.example.ruslan.curs2project.api;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.ruslan.curs2project.BuildConfig.MESSAGING_SERVER_KEY;
import static com.example.ruslan.curs2project.BuildConfig.STANDART_CONTENT_TYPE;
import static com.example.ruslan.curs2project.utils.Const.MESSAGING_KEY;
import static com.example.ruslan.curs2project.utils.Const.MESSAGING_TYPE;

public final class ApiKeyInterceptor implements Interceptor {

    private ApiKeyInterceptor() {
    }

    @NonNull
    public static Interceptor create() {
        return new ApiKeyInterceptor();
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        return chain.proceed(original.newBuilder()
                .addHeader(MESSAGING_KEY,MESSAGING_SERVER_KEY)
                .addHeader(MESSAGING_TYPE,STANDART_CONTENT_TYPE)
                .build());
    }
}
