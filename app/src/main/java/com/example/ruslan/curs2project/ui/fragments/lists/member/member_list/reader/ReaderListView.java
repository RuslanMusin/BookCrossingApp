package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.reader;

import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.MemberAdapter;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by valera071998@gmail.com on 16.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ReaderListView extends MvpView,BaseAdapter.OnItemClickListener<User> {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void handleError(Throwable throwable);

    void setNotLoading();

    void showLoading(Disposable disposable);

    void hideLoading();

    void showDetails(User comics);

    void loadNextElements(int i);

    void setCurrentType(String type);

    void setAdapter(MemberAdapter adapter);

    void loadRequests(String currentId);

    void loadFriends(String currentId);

    void loadReaders();

    void setProgressBar(ProgressBar progressBar);

    void changeAdapter(int position);

    void changeDataSet(List<User> friends);
}
