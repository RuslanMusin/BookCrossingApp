package com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.repository.api.RepositoryProvider;

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
        Log.d(TAG,"load books");
        RepositoryProvider.getBookApiRepository()
                .loadDefaultBooks()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
       /* RepositoryProvider.getBookApiRepository()
                .comics(page * PAGE_SIZE, PAGE_SIZE, DEFAULT_COMICS_SORT)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::addMoreItems, getViewState()::handleError);*/
    }

    public void onItemClick(Book comics) {
        getViewState().showDetails(comics);
    }
}
