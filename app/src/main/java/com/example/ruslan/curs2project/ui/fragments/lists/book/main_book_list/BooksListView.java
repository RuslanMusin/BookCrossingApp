package com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.Book;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by valera071998@gmail.com on 16.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface BooksListView extends MvpView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void showItems(@NonNull List<Book> books);

    void addMoreItems(List<Book> comics);

    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();

    //* Navigation methods*/
    void showDetails(Book item);

}
