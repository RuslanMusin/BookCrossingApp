package com.example.ruslan.curs2project.repository.api;

import android.support.annotation.NonNull;

import com.example.ruslan.curs2project.model.Book;

import java.util.List;

import io.reactivex.Single;

public interface BookApiRepository {

    @NonNull
    Single<List<Book>> books(String query);

    Single<Book> book(String id);

    Single<List<Book>> loadDefaultBooks();
}
