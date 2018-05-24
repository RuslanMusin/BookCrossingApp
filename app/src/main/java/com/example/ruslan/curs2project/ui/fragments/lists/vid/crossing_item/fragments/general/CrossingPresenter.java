package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.general;

import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.repository.api.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.BookCrossingRepository;
import com.example.ruslan.curs2project.repository.json.CrossingMembersRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
                .doOnSubscribe(getView()::showLoading)
                .doAfterTerminate(getView()::hideLoading)
                .subscribe(getView():: setFollowers, getView()::handleError);
    }

    @SuppressLint("CheckResult")
    public void findPoint(String crossingId, String userId) {
        RepositoryProvider.getPointRepository()
                .findPoint(crossingId, userId)
                .doOnSubscribe(getView()::showLoading)
                .doAfterTerminate(getView()::hideLoading)
                .subscribe(getView():: setUserPoint, getView()::handleError);
    }

    @SuppressLint("CheckResult")
    public void findIsFollower(String crossingId, String userId) {
        RepositoryProvider.getPointRepository()
                .findPoint(crossingId, userId)
                .doOnSubscribe(getView()::showLoading)
                .doAfterTerminate(getView()::hideLoading)
                .subscribe(getView():: setUserPoint, getView()::handleError);
    }

   /* @SuppressLint("CheckResult")
    public void loadNextComments(int page, String bookId) {
        RepositoryProvider.getCrossingCommentRepository(bookId)
                .getComments(page * PAGE_SIZE, PAGE_SIZE, DEFAULT_COMICS_SORT)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::addMoreItems, getViewState()::handleError);
    }

    @SuppressLint("CheckResult")
    public void loadComments(String bookId) {
        RepositoryProvider.getCrossingCommentRepository(bookId)
                .getComments(ZERO_OFFSET, PAGE_SIZE, DEFAULT_COMICS_SORT)
                .doOnSubscribe(getViewState()::showLoading)
                .doAfterTerminate(getViewState()::hideLoading)
                .doAfterTerminate(getViewState()::setNotLoading)
                .subscribe(getViewState()::showItems, getViewState()::handleError);
    }
*/
    public void addMember(String userId, String crossingId) {
        crossingMembersRepository.createMember(userId,crossingId);
    }

    public void addFollower(BookCrossing crossing, String userId) {
        crossingRepository.addFollower(crossing, userId);
    }

    public void removeFollower(BookCrossing crossing, String userId) {
        crossingRepository.removeFollower(crossing, userId);
    }
}
