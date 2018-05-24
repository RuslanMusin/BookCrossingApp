package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_item.PersonalActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.MemberAdapter;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.CROSSING_KEY;
import static com.example.ruslan.curs2project.utils.Const.DEFAULT_TYPE;

public class MemberListActivity extends NavigationBaseActivity implements MemberListView,
        BaseAdapter.OnItemClickListener<User> {

    private static final String TAG = "TAG";
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EmptyStateRecyclerView recyclerView;
    private TextView tvEmpty;

    private MemberAdapter adapter;

    @InjectPresenter
    MemberListPresenter presenter;

    private boolean isLoading = false;

    private BookCrossing bookCrossing;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_books_list, contentFrameLayout);
        bookCrossing = new Gson().fromJson(getIntent().getStringExtra(CROSSING_KEY),BookCrossing.class);
        initViews();
        initRecycler();
    }


    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, MemberListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void start(@NonNull Context context, BookCrossing crossing) {
        Intent intent = new Intent(context, MemberListActivity.class);
        String crossingJson = new Gson().toJson(crossing);
        intent.putExtra(CROSSING_KEY,crossingJson);
        context.startActivity(intent);
    }

    @Override
    public void loadWay() {
        if(bookCrossing == null){
            presenter.loadBooks();
        } else {
            presenter.loadByIds(bookCrossing.getFollowersId());
        }
    }

    @Override
    public void onItemClick(@NonNull User item) {
        presenter.onItemClick(item);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG, "error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showItems(@NonNull Query query, String type) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(type.equals(DEFAULT_TYPE)) {
                    List<User> comments = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User crossing = postSnapshot.getValue(User.class);
                        if (crossing != null) {
                            comments.add(crossing);
                        }
                    }
                    adapter.changeDataSet(comments);
                } else {
                    List<String> comments = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Identified crossing = postSnapshot.getValue(ElementId.class);
                        if (crossing != null) {
                            comments.add(crossing.getId());
                        }
                    }
                    presenter.loadByIds(comments);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void showItems(@NonNull List<Query> queries) {
        List<User> bookCrossings = new ArrayList<>();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User crossing = dataSnapshot.getValue(User.class);
                bookCrossings.add(crossing);
                if(bookCrossings.size() == queries.size()){
                    adapter.changeDataSet(bookCrossings);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        for(Query query : queries){
            query.addListenerForSingleValueEvent(listener);
        }
//        adapter.changeDataSet(bookCrossings);
    }


    @Override
    public void addMoreItems(Query items) {
        items.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    comments.add(postSnapshot.getValue(User.class));
                }
                adapter.addAll(comments);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
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


    private void initRecycler() {
        adapter = new MemberAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setEmptyView(tvEmpty);
        adapter.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentPage = 0;
            // обычно бывает флаг последней страницы, но я че т его не нашел, если не найдется, то можно удалить, всегда тру
            private boolean isLastPage = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 20) {
                        isLoading = true;
                        presenter.loadNextElements(++currentPage);
                    }
                }
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.tb_books_list);
        supportActionBar(toolbar);
        progressBar = findViewById(R.id.pg_comics_list);
        recyclerView = findViewById(R.id.rv_comics_list);
        tvEmpty = findViewById(R.id.tv_empty);

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
                    presenter.loadBooksByQuery(query);
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
}
