package com.example.ruslan.curs2project.ui.crossing.crossing_list;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.repository.RepositoryProvider;

import java.util.List;

import static android.support.constraint.Constraints.TAG;


@InjectViewState
public class CrossingListPresenter extends MvpPresenter<CrossingListView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().loadWay();
//        loadBooks();
    }


    @SuppressLint("CheckResult")
    public void loadBooksByQuery(String query) {
        RepositoryProvider.getBookCrossingRepository()
                .loadByQuery(query)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().showItems(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadBooksByBook(Book book) {
        RepositoryProvider.getBookCrossingRepository()
                .loadByBook(book)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadBooks() {
        Log.d(TAG,"load books");
        RepositoryProvider.getBookCrossingRepository()
                .loadDefaultCrossings()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().showItems(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadNextElements(int page) {
        Log.d(TAG,"load books");
        RepositoryProvider.getBookCrossingRepository()
                .loadDefaultCrossings()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().showItems(s), getViewState()::handleError);
    }

    public void onItemClick(BookCrossing comics) {
        getViewState().showDetails(comics);
    }

    @SuppressLint("CheckResult")
    public void loadByIds(List<String> crossingsIds) {
        RepositoryProvider.getBookCrossingRepository()
                .loadByIds(crossingsIds)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadUserCrossings(String userId) {
        RepositoryProvider.getBookCrossingRepository()
                .loadByUser(userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().showItems(s), getViewState()::handleError);
    }
}
