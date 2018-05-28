package com.example.ruslan.curs2project.ui.fragments.lists.book.book_item;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.Book;

/**
 * Created by valera071998@gmail.com on 16.03.2018.
 */

public interface BookView extends MvpView {

    void getBookId();

    void setBookData(Book book);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);


}
