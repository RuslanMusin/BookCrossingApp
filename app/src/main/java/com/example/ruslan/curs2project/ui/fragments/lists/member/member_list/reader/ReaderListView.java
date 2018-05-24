package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.reader;

import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.MemberAdapter;
import com.google.firebase.database.Query;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by valera071998@gmail.com on 16.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ReaderListView extends MvpView,BaseAdapter.OnItemClickListener<User> {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void setReaders(@NonNull Query books);

    void setFriendsByQuery(@NonNull List<Query> books);

    void setRequestsByQuery(@NonNull List<Query> books);

    void setFriends(@NonNull Query books);

    void setRequests(@NonNull Query books);

    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();

    void showItems(List<Query> queries, String type);

    void showDetails(User comics);

    void loadNextElements(int i);

    void setCurrentType(String type);

    void setAdapter(MemberAdapter adapter);

    void loadRequests(String currentId);

    void loadFriends(String currentId);

    void loadReaders();

    void setProgressBar(ProgressBar progressBar);

    void changeAdapter(int position);
}
