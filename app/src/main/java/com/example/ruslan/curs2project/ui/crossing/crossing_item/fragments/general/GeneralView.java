package com.example.ruslan.curs2project.ui.crossing.crossing_item.fragments.general;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.google.firebase.database.Query;

public interface GeneralView extends MvpView {

    void getBookId();

    void setBookData(BookCrossing bookCrossing);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void setUserPoint(@NonNull Query query);

    void setFollowers(@NonNull Query query);

}
