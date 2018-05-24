package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.member;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.api.RepositoryProvider;

import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.example.ruslan.curs2project.utils.Const.DEFAULT_TYPE;
import static com.example.ruslan.curs2project.utils.Const.QUERY_TYPE;

/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */
@InjectViewState
public class MemberListPresenter extends MvpPresenter<MemberListView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().loadWay();
    }

    @SuppressLint("CheckResult")
    public void loadBooksByQuery(String query) {
        RepositoryProvider.getUserRepository()
                .loadReadersByQuery(query)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().showItems(s,DEFAULT_TYPE), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadUsersByCrossing(String crossingId) {
        RepositoryProvider.getUserRepository()
                .loadByCrossing(crossingId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(s -> getViewState().showItems(s,QUERY_TYPE), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadBooks() {
        Log.d(TAG,"load books");
        RepositoryProvider.getUserRepository()
                .loadDefaultUsers()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().showItems(s,DEFAULT_TYPE), getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadNextElements(int page) {
        Log.d(TAG,"load books");
        RepositoryProvider.getUserRepository()
                .loadDefaultUsers()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(s -> getViewState().showItems(s,DEFAULT_TYPE), getViewState()::handleError);

    }

    @SuppressLint("CheckResult")
    public void loadByIds(List<String> usersId) {
        RepositoryProvider.getUserRepository()
                .loadByIds(usersId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }

    public void onItemClick(User comics) {
        getViewState().showDetails(comics);
    }
}
