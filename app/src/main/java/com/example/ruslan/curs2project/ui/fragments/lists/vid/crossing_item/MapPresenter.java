package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.repository.api.RepositoryProvider;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> {

    private MapView view;

    public MapView getView() {
        return view;
    }

    public void setView(MapView view) {
        this.view = view;
    }

    public MapPresenter(MapView view) {
        this.view = view;
    }

    public MapPresenter() {
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
