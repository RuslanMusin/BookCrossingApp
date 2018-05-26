package com.example.ruslan.curs2project.api;

import com.example.ruslan.curs2project.model.pojo.Message;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface MessageService {

    @POST("send")
    Single<String> sendMessage(@Body Message messageJson);
}
