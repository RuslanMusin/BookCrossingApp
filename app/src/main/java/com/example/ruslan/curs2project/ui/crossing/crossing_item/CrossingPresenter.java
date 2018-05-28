package com.example.ruslan.curs2project.ui.crossing.crossing_item;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.repository.RepositoryProvider;

@InjectViewState
public class CrossingPresenter extends MvpPresenter<CrossingView> {

    private CrossingView view;

    public CrossingView getView() {
        return view;
    }

    public void setView(CrossingView view) {
        this.view = view;
    }

    public CrossingPresenter(CrossingView view) {
        this.view = view;
    }

    public CrossingPresenter() {
    }

    @SuppressLint("CheckResult")
    public void loadPoints(String crossingId) {
        RepositoryProvider.getPointRepository()
                .loadPoints(crossingId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(getViewState():: showPoints, getViewState()::handleError);
    }
}
