package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.reader;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */
@InjectViewState
public class ReaderListPresenter extends MvpPresenter<ReaderListView> {

    @SuppressLint("CheckResult")
    public void loadReadersByQuery(String query) {
        RepositoryProvider.getUserRepository()
                .loadReadersByQuery(query)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(this::setReaders, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadFriendsByQuery(String query, String userId) {
        RepositoryProvider.getUserRepository()
                .loadFriendsByQuery(query,userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(this::setFriendsByQuery, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadRequestByQuery(String query, String userId) {
        RepositoryProvider.getUserRepository()
                .loadRequestByQuery(query, userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(this::setRequestsByQuery, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadFriends(String userId) {
        RepositoryProvider.getUserRepository()
                .findFriends(userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(this::setFriends, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadRequests(String userId) {
        RepositoryProvider.getUserRepository()
                .findRequests(userId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(this::setRequests, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadReaders() {
        Log.d(TAG,"load books");
        RepositoryProvider.getUserRepository()
                .loadDefaultUsers()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(this::setReaders, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadNextElements(int page) {
        Log.d(TAG,"load books");
        RepositoryProvider.getUserRepository()
                .loadDefaultUsers()
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(this::setReaders, getViewState()::handleError);

    }

    @SuppressLint("CheckResult")
    public void loadByIds(List<String> usersId) {
        RepositoryProvider.getUserRepository()
                .loadByIds(usersId)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .subscribe(this::showItems, getViewState()::handleError);
    }


    //work with DB
    public void setFriends(@NonNull Query books) {
        books.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> friendsId = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Identified elementId = snapshot.getValue(ElementId.class);
                    friendsId.add(elementId.getId());
                }
                loadByIds(friendsId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setRequests(@NonNull Query books) {
        books.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> friendsId = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Identified elementId = snapshot.getValue(ElementId.class);
                    friendsId.add(elementId.getId());
                }
                loadByIds(friendsId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setReaders(@NonNull Query books) {
        books.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User reader = snapshot.getValue(User.class);
                    if (reader != null) {
                        users.add(reader);
                    }
                }
                getViewState().changeDataSet(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setFriendsByQuery(@NonNull List<Query> queries) {
        List<User> friends = new ArrayList<>();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User reader = snapshot.getValue(User.class);
                    friends.add(reader);
                    if (friends.size() == queries.size()) {
                        getViewState().changeDataSet(friends);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        for (Query query : queries) {
            query.addListenerForSingleValueEvent(listener);
        }

    }

    public void setRequestsByQuery(@NonNull List<Query> queries) {
        List<User> requests = new ArrayList<>();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User reader = snapshot.getValue(User.class);
                    requests.add(reader);
                    if (requests.size() == queries.size()) {
                        getViewState().changeDataSet(requests);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        for (Query query : queries) {
            query.addListenerForSingleValueEvent(listener);
        }

    }

    public void showItems(@NonNull List<Query> queries) {
        List<User> users = new ArrayList<>();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User crossing = dataSnapshot.getValue(User.class);
                users.add(crossing);
                if (users.size() == queries.size()) {
                    getViewState().changeDataSet(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LOG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        for (Query query : queries) {
            query.addListenerForSingleValueEvent(listener);
        }
    }


    public void onItemClick(User comics) {
        getViewState().showDetails(comics);
    }
}
