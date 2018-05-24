package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.general;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.google.firebase.database.Query;

import io.reactivex.disposables.Disposable;

/**
 * Created by valera071998@gmail.com on 16.03.2018.
 */

public interface CrossingView extends MvpView {

    void getBookId();

    void setBookData(BookCrossing bookCrossing);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

   /* void showItems(@NonNull Query books);

    void addMoreItems(Query comics);*/

    void setUserPoint(@NonNull Query query);

    void setFollowers(@NonNull Query query);


    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();
}
