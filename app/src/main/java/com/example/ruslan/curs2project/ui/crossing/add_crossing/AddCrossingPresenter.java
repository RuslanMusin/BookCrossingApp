package com.example.ruslan.curs2project.ui.crossing.add_crossing;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

@InjectViewState
public class AddCrossingPresenter extends MvpPresenter<AddCrossingView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d(TAG_LOG,"attach presenter");
    }
}
