package com.example.ruslan.curs2project.ui.crossing.crossing_list;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.google.firebase.database.Query;

import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface CrossingListView extends MvpView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void showItems(@NonNull Query query);

    void showItems(@NonNull Map<String, Query> queryMap);

    void showItems(@NonNull List<Query> queries);

    void addMoreItems(Query comics);

    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();

    //* Navigation methods*/
    void showDetails(BookCrossing item);

    void loadWay();
}
