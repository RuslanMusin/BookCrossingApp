package com.example.ruslan.curs2project.repository.api;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.example.ruslan.curs2project.repository.json.BookCommentRepository;
import com.example.ruslan.curs2project.repository.json.BookCrossingRepository;
import com.example.ruslan.curs2project.repository.json.CrossingCommentRepository;
import com.example.ruslan.curs2project.repository.json.PointRepository;
import com.example.ruslan.curs2project.repository.json.UserRepository;

/**
 * Created by Nail Shaykhraziev on 07.03.2018.
 */
public class RepositoryProvider {

    private static UserRepository userRepository;

    private static BookApiRepository bookApiRepository;

    private static BookCommentRepository bookCommentRepository;

    private static CrossingCommentRepository crossingCommentRepository;

    private static BookCrossingRepository bookCrossingRepository;

    private static PointRepository pointRepository;

    @MainThread
    public static void init() {
        bookApiRepository = new BookApiRepositoryImpl();
    }

    public static PointRepository getPointRepository() {
        if (pointRepository == null) {
            pointRepository = new PointRepository();
        }
        return pointRepository;
    }

    public static void setPointRepository(PointRepository pointRepository) {
        RepositoryProvider.pointRepository = pointRepository;
    }

    public static UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    public static void setUserRepository(UserRepository userRepository) {
        RepositoryProvider.userRepository = userRepository;
    }

    @NonNull
    public static BookApiRepository getBookApiRepository() {
        if (bookApiRepository == null) {
            bookApiRepository = new BookApiRepositoryImpl();
        }
        return bookApiRepository;
    }

    public static void setBookApiRepository(BookApiRepository bookApiRepository) {
        RepositoryProvider.bookApiRepository = bookApiRepository;
    }

    public static BookCommentRepository getBookCommentRepository(String bookId) {
        if (bookCommentRepository == null) {
            bookCommentRepository = new BookCommentRepository(bookId);
        }
        return bookCommentRepository;
    }

    public static void setBookCommentRepository(BookCommentRepository bookCommentRepository) {
        RepositoryProvider.bookCommentRepository = bookCommentRepository;
    }

    public static CrossingCommentRepository getCrossingCommentRepository(String crossingId) {
        if (crossingCommentRepository == null) {
            crossingCommentRepository = new CrossingCommentRepository(crossingId);
        }
        return crossingCommentRepository;
    }

    public static void setCrossingCommentRepository(CrossingCommentRepository crossingCommentRepository) {
        RepositoryProvider.crossingCommentRepository = crossingCommentRepository;
    }

    public static BookCrossingRepository getBookCrossingRepository() {
        if (bookCrossingRepository == null) {
            bookCrossingRepository = new BookCrossingRepository();
        }
        return bookCrossingRepository;
    }

    public static void setBookCrossingRepository(BookCrossingRepository bookCrossingRepository) {
        RepositoryProvider.bookCrossingRepository = bookCrossingRepository;
    }
}