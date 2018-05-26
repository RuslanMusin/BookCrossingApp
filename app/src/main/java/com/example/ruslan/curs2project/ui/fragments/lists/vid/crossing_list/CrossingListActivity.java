package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_list;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CrossingActivity;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.BOOK_KEY;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;
import static com.example.ruslan.curs2project.utils.Const.USER_KEY;

public class CrossingListActivity extends NavigationBaseActivity implements CrossingListView,
        BaseAdapter.OnItemClickListener<BookCrossing> {

    private static final String TAG = "TAG";
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EmptyStateRecyclerView recyclerView;
    private TextView tvEmpty;
    private CheckBox cbShowCrossings;

    private CrossingAdapter adapter;

    @InjectPresenter
    CrossingListPresenter presenter;

    private boolean isLoading = false;

   private Book book;

   private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_crossing_list, contentFrameLayout);
        initViews();
        initRecycler();

        String bookJson = getIntent().getStringExtra(BOOK_KEY);
        Gson gson = new Gson();
        if(bookJson != null) {
            book = gson.fromJson(bookJson,Book.class);
        }

        String userJson = getIntent().getStringExtra(USER_KEY);
        if(userJson != null) {
            userId = gson.fromJson(userJson,User.class).getId();
        } else {
            userId = UserRepository.getCurrentId();
        }
    }

    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, CrossingListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static void start(@NonNull Activity activity, @NonNull Book book) {
        Intent intent = new Intent(activity, CrossingListActivity.class);
        String bookJson = new Gson().toJson(book);
        intent.putExtra(BOOK_KEY,bookJson);
        activity.startActivity(intent);
    }

    public static void start(@NonNull Activity activity, @NonNull User user) {
        Intent intent = new Intent(activity, CrossingListActivity.class);
        String userJson = new Gson().toJson(user);
        intent.putExtra(USER_KEY,userJson);
        activity.startActivity(intent);
    }

    @Override
    public void onItemClick(@NonNull BookCrossing item) {
        presenter.onItemClick(item);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG, "error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void showItems(@NonNull Query items) {
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> comments = new ArrayList<>();
                Log.d(TAG_LOG, "query has el? "  + dataSnapshot.getChildren().iterator().hasNext());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String crossingId;
                    Identified crossing = postSnapshot.getValue(ElementId.class);
                    if (crossing != null) {
                        crossingId  = crossing.getId();
                        Log.d(TAG_LOG, "cross id =  " + crossingId);
                        if(crossingId != null) {
                            comments.add(crossingId);
                        }

                    }
                }
                presenter.loadByIds(comments);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });


    }

    public void showItems(@NonNull Map<String, Query> map) {
        Query queryId = map.get("queryId");
        Query queryName = map.get("queryName");
        Set<String> stringSet = new LinkedHashSet<>();
        queryId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Identified crossing = postSnapshot.getValue(ElementId.class);
                    if (crossing != null) {
                        comments.add(crossing.getId());
                    }
                }
                stringSet.addAll(comments);
                loadByName(queryName, stringSet);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void loadByName(Query queryName, Set<String> crossings){
        queryName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Identified crossing = postSnapshot.getValue(ElementId.class);
                    if (crossing != null) {
                        comments.add(crossing.getId());
                    }
                }
                crossings.addAll(comments);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        List<String> list = new ArrayList<>();
        list.addAll(crossings);
        presenter.loadByIds(list);
    }

    public void showItems(@NonNull List<Query> queries){
        List<BookCrossing> bookCrossings = new ArrayList<>();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               BookCrossing crossing = dataSnapshot.getValue(BookCrossing.class);
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
                List<BookCrossing> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    comments.add(postSnapshot.getValue(BookCrossing.class));
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
    public void showDetails(BookCrossing item) {
        CrossingActivity.start(this, item);
    }

    @Override
    public void loadWay() {
        if(book == null){
            presenter.loadBooks();
        } else if(!userId.equals(UserRepository.getCurrentId())) {
            presenter.loadUserCrossings(userId);
            cbShowCrossings.setChecked(true);
        }
        else {
            presenter.loadBooksByBook(book);
        }
    }


    private void initRecycler() {
        adapter = new CrossingAdapter(new ArrayList<>());
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
        cbShowCrossings = findViewById(R.id.cb_show_my_crossings);

        cbShowCrossings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if(checkBox.isChecked()) {
                    presenter.loadUserCrossings(userId);
                } else {
                    presenter.loadBooks();
                }
            }
        });


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
