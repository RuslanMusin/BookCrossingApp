package com.example.ruslan.curs2project.api;

import com.example.ruslan.curs2project.model.pojo.GBook;
import com.example.ruslan.curs2project.model.pojo.SearchResult;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nail Shaykhraziev on 25.02.2018.
 */

public interface BooksService {

    @GET("./")
    Single<SearchResult> books(@Query("q") String q);

    @GET("./{volumeId}")
    Single<GBook> book(@Path("volumeId") String id);

    @GET("./")
    Single<SearchResult> books(@Query("q") String q,
                               @Query("maxResults") Integer max,
                               @Query("orderBy") String orderBy,
                               @Query("projection") String projection
                               );


}
