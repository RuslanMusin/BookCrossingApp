package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.google.firebase.database.Query;

import io.reactivex.disposables.Disposable;

public interface MapView extends MvpView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void showPoints(@NonNull Query books);

    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();
}
