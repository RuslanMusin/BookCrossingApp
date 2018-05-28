package com.example.ruslan.curs2project.ui.member.member_list.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.member.member_item.PersonalActivity;
import com.example.ruslan.curs2project.ui.member.member_list.MemberAdapter;
import com.example.ruslan.curs2project.ui.member.member_list.fragment.ReaderListFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.FRIEND_LIST;
import static com.example.ruslan.curs2project.utils.Const.READER_LIST;
import static com.example.ruslan.curs2project.utils.Const.REQUEST_LIST;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class ReaderListActivity extends NavigationBaseActivity implements ReaderListView {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MemberAdapter adapter;

    @InjectPresenter
    ReaderListPresenter presenter;

    private boolean isLoading = false;
    private String currentType;

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, ReaderListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_crossing, contentFrameLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        supportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        setTabListener();
    }

    private void setTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG_LOG, "on tab selected");
                viewPager.setCurrentItem(tab.getPosition());
                ReaderListActivity.this.changeAdapter(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void changeAdapter(int position) {
        ReaderListFragment fragment = ((ViewPagerAdapter) viewPager.getAdapter()).getFragmentForChange(position);
        fragment.changeDataInAdapter();
    }

    private void setupViewPager(ViewPager viewPager) {
        ReaderListActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ReaderListFragment.newInstance(READER_LIST, this), READER_LIST);
        adapter.addFragment(ReaderListFragment.newInstance(FRIEND_LIST, this), FRIEND_LIST);
        adapter.addFragment(ReaderListFragment.newInstance(REQUEST_LIST, this), REQUEST_LIST);
        this.currentType = READER_LIST;
        viewPager.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            SearchView finalSearchView = searchView;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    switch (currentType) {
                        case READER_LIST:
                            presenter.loadReadersByQuery(query);
                            break;

                        case FRIEND_LIST:
                            presenter.loadFriendsByQuery(query, UserRepository.getCurrentId());
                            break;

                        case REQUEST_LIST:
                            presenter.loadRequestByQuery(query, UserRepository.getCurrentId());
                    }
                    if (!finalSearchView.isIconified()) {
                        finalSearchView.setIconified(true);
                    }
                    searchItem.collapseActionView();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
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

        public ReaderListFragment getFragmentForChange(int position) {
            return (ReaderListFragment) mFragmentList.get(position);
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

    @Override
    public void onItemClick(@NonNull User item) {
        presenter.onItemClick(item);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG_LOG, "error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void changeDataSet(List<User> users) {
        adapter.changeDataSet(users);
    }

    @Override
    public void setAdapter(MemberAdapter adapter) {
        Log.d(TAG_LOG, "set adapter");
        Log.d(TAG_LOG, "type adapter =  " + currentType);
        this.adapter = adapter;
    }

    @Override
    public void loadRequests(String currentId) {
        Log.d(TAG_LOG, "load requests");
        presenter.loadRequests(currentId);
    }

    @Override
    public void loadFriends(String currentId) {
        Log.d(TAG_LOG, "load friends");
        presenter.loadFriends(currentId);

    }

    @Override
    public void loadReaders() {
        Log.d(TAG_LOG, "load readers");
        presenter.loadReaders();
    }

    @Override
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void setNotLoading() {
        isLoading = false;
    }

    public void showLoading(Disposable disposable) {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showDetails(User item) {
        PersonalActivity.start(this, item);
    }

    @Override
    public void loadNextElements(int i) {
        presenter.loadNextElements(i);
    }

    @Override
    public void setCurrentType(String type) {
        Log.d(TAG_LOG, "current type = " + type);
        this.currentType = type;
    }

    public String getCurrentType() {
        return currentType;
    }
}
