package com.example.ruslan.curs2project.ui.fragments.lists.book.book_item;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.Book;
import com.google.firebase.database.Query;

import io.reactivex.disposables.Disposable;

/**
 * Created by valera071998@gmail.com on 16.03.2018.
 */

public interface BookView extends MvpView {

    void getBookId();

    void setBookData(Book book);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void showItems(@NonNull Query books);

    void addMoreItems(Query comics);

    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();
}
