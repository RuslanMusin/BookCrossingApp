package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.general;

import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.BookCrossingRepository;
import com.example.ruslan.curs2project.repository.json.CrossingMembersRepository;
import com.example.ruslan.curs2project.ui.fragments.lists.book.book_item.OnCommentClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

/**
 * Created by Ruslan on 02.03.2018.
 */
@InjectViewState
public class CrossingPresenter extends MvpPresenter<CrossingView> {

    BookCrossingRepository crossingRepository;

    CrossingMembersRepository crossingMembersRepository;

    private CrossingView view;

    public CrossingView getView() {
        return view;
    }

    public void setView(CrossingView view) {
        this.view = view;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d(TAG_LOG,"attach presenter");
        getView().getBookId();
    }

    @SuppressLint("CheckResult")
    @VisibleForTesting
    public void init(String id) {
        Log.d(TAG_LOG,"init presenter");
        crossingRepository = new BookCrossingRepository();
        crossingMembersRepository = new CrossingMembersRepository();
        DatabaseReference reference = crossingRepository.readCrossing(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG_LOG, "loadPost:onChange");
                BookCrossing crossing = dataSnapshot.getValue(BookCrossing.class);

                Log.d(TAG_LOG,"crossing " + crossing.getId() + " " + crossing.getBookName());
                getView().setBookData(crossing);

                findFollowers(crossing.getId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG_LOG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @SuppressLint("CheckResult")
    public void findFollowers(String crossingId) {
        RepositoryProvider.getBookCrossingRepository()
                .findFollowers(crossingId)
                .subscribe(getView():: setFollowers, getView()::handleError);
    }

    @SuppressLint("CheckResult")
    public void findPoint(String crossingId, String userId) {
        RepositoryProvider.getPointRepository()
                .findPoint(crossingId, userId)
                .subscribe(getView():: setUserPoint, getView()::handleError);
    }

    @SuppressLint("CheckResult")
    public void findIsFollower(String crossingId, String userId) {
        RepositoryProvider.getPointRepository()
                .findPoint(crossingId, userId)
                .subscribe(getView():: setUserPoint, getView()::handleError);
    }


    public void addMember(String userId, String crossingId) {
        crossingMembersRepository.createMember(userId,crossingId);
    }

    public void addFollower(BookCrossing crossing, String userId) {
        crossingRepository.addFollower(crossing, userId);

        FirebaseMessaging.getInstance().subscribeToTopic(crossing.getId());

    }

    public void removeFollower(BookCrossing crossing, String userId) {
        crossingRepository.removeFollower(crossing, userId);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(crossing.getId());
    }

    @SuppressLint("CheckResult")
    public void loadComments(OnCommentClickListener listener, String crossingId) {
        RepositoryProvider.getCrossingCommentRepository(crossingId)
                .getComments(listener);
    }

    public void createComment(String crossingId, Comment comment, OnCommentClickListener listener) {
        RepositoryProvider.getCrossingCommentRepository(crossingId).createComment(comment,listener);
    }
}
