package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.reader;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.api.RepositoryProvider;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */
@InjectViewState
public class ReaderListPresenter extends MvpPresenter<ReaderListView> {

    @SuppressLint("CheckResult")
    public void loadReadersByQuery(String query) {
        RepositoryProvider.getUserRepository()
                .loadReadersByQuery(query)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().setReaders(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadFriendsByQuery(String query, String userId) {
        RepositoryProvider.getUserRepository()
                .loadFriendsByQuery(query,userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().setFriendsByQuery(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadRequestByQuery(String query, String userId) {
        RepositoryProvider.getUserRepository()
                .loadRequestByQuery(query, userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().setRequestsByQuery(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadFriends(String userId) {
        RepositoryProvider.getUserRepository()
                .findFriends(userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().setFriends(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadRequests(String userId) {
        RepositoryProvider.getUserRepository()
                .findRequests(userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().setRequests(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadReaders() {
        Log.d(TAG,"load books");
        RepositoryProvider.getUserRepository()
                .loadDefaultUsers()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().setReaders(s), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadNextElements(int page) {
        Log.d(TAG,"load books");
        RepositoryProvider.getUserRepository()
                .loadDefaultUsers()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().setReaders(s), getViewState()::handleError);

    }

    @SuppressLint("CheckResult")
    public void loadByIds(List<String> usersId, String type) {
        RepositoryProvider.getUserRepository()
                .loadByIds(usersId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().showItems(s,type), getViewState()::handleError);
    }

    public void onItemClick(User comics) {
        getViewState().showDetails(comics);
    }
}
