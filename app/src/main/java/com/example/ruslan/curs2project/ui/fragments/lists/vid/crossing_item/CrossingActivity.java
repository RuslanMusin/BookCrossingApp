package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.general.GeneralFragment;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.image.PhotoFragment;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.map.MapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.CROSSING_KEY;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class CrossingActivity extends NavigationBaseActivity implements MapView {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private BookCrossing bookCrossing;

    @InjectPresenter
    MapPresenter presenter;

    private int[] tabIcons = {
            R.drawable.ic_book_white_24dp,
            R.drawable.ic_place_white,
            R.drawable.ic_instagram_white
    };

    public BookCrossing getBookCrossing() {
        return bookCrossing;
    }

    public void setBookCrossing(BookCrossing bookCrossing) {
        this.bookCrossing = bookCrossing;
    }

    public static void start(@NonNull Activity activity, @NonNull BookCrossing comics) {
        Intent intent = new Intent(activity, CrossingActivity.class);
        String crossingJson = new Gson().toJson(comics);
        intent.putExtra(CROSSING_KEY,crossingJson);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossing);

        bookCrossing = new Gson().fromJson(getIntent().getStringExtra(CROSSING_KEY),BookCrossing.class);
        presenter.loadPoints(bookCrossing.getId());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        supportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GeneralFragment(), getString(R.string.general));
        adapter.addFragment(new MapFragment(), getString(R.string.map));
        adapter.addFragment(new PhotoFragment(), getString(R.string.photo));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void showPoints(@NonNull Query items) {
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Point> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Point point = postSnapshot.getValue(Point.class);
                    comments.add(point);

                }
                bookCrossing.setPoints(comments);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LOG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void setNotLoading() {

    }

    @Override
    public void showLoading(Disposable disposable) {

    }

    @Override
    public void hideLoading() {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void hideKeyBoard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}