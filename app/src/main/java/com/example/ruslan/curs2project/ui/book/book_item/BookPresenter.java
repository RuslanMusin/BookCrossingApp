package com.example.ruslan.curs2project.ui.book.book_item;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.repository.RepositoryProvider;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

/**
 * Created by Ruslan on 02.03.2018.
 */
@InjectViewState
public class BookPresenter extends MvpPresenter<BookView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d(TAG_LOG,"attach presenter");
        getViewState().getBookId();
    }

    @SuppressLint("CheckResult")
    public void init(String id) {
        Log.d(TAG_LOG,"init presenter");
        RepositoryProvider.getBookApiRepository()
                .book(id)
                .subscribe(comics -> {
                    getViewState().setBookData(comics);
                }, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadComments(OnCommentClickListener listener, String bookId) {
        RepositoryProvider.getBookCommentRepository(bookId)
                .getComments(listener);
    }

    public void createComment(String bookId, Comment comment, OnCommentClickListener listener) {
        RepositoryProvider.getBookCommentRepository(bookId).createComment(comment,listener);
    }
}
