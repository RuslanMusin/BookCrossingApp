package com.example.ruslan.curs2project.ui.fragments.lists.book.book_item;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.repository.RepositoryProvider;

import static com.example.ruslan.curs2project.utils.Const.DEFAULT_BOOK_SORT;
import static com.example.ruslan.curs2project.utils.Const.PAGE_SIZE;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;
import static com.example.ruslan.curs2project.utils.Const.ZERO_OFFSET;

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
    public void loadNextComments(int page, String bookId) {
        RepositoryProvider.getBookCommentRepository(bookId)
                .getComments(page * PAGE_SIZE, PAGE_SIZE, DEFAULT_BOOK_SORT)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::addMoreItems, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadComments(String bookId) {
        RepositoryProvider.getBookCommentRepository(bookId)
                .getComments(ZERO_OFFSET, PAGE_SIZE, DEFAULT_BOOK_SORT)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }
}
