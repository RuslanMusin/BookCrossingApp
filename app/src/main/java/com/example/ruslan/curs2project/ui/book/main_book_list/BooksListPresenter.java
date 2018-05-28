package com.example.ruslan.curs2project.ui.book.main_book_list;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.repository.RepositoryProvider;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */
@InjectViewState
public class BooksListPresenter extends MvpPresenter<BooksListView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadBooks();
    }

    @SuppressLint("CheckResult")
    public void loadBooksByQuery(String query) {
        RepositoryProvider.getBookApiRepository()
                .books(query)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadBooks() {
        Log.d(TAG,"load books");
        RepositoryProvider.getBookApiRepository()
                .loadDefaultBooks()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadNextElements(int page) {
        Log.d(TAG, "load books");
        RepositoryProvider.getBookApiRepository()
                .loadDefaultBooks()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    public void onItemClick(Book book) {
        getViewState().showDetails(book);
    }
}
