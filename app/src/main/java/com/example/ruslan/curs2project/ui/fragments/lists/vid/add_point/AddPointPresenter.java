package com.example.ruslan.curs2project.ui.fragments.lists.vid.add_point;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.repository.api.RepositoryProvider;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

@InjectViewState
public class AddPointPresenter extends MvpPresenter<AddPointView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d(TAG_LOG,"attach presenter");
    }

    public void createPoint(BookCrossing crossing, Point point) {
        RepositoryProvider.getPointRepository()
                .createPoint(crossing,point);
    }

    public boolean validateKey(BookCrossing crossing, String key) {
        String crossingKey = crossing.getKeyPhrase();
        if(key.equals(crossingKey)) {
            return true;
        }
        return false;
    }
}
