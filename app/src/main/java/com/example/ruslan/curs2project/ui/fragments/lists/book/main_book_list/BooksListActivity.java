package com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list;

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
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.book.book_item.BookActivity;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class BooksListActivity extends NavigationBaseActivity implements BooksListView,
        BaseAdapter.OnItemClickListener<Book> {

    private static final String TAG = "TAG";
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EmptyStateRecyclerView recyclerView;
    private TextView tvEmpty;

    private BooksAdapter adapter;

    @InjectPresenter
    BooksListPresenter presenter;

    private boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_books_list, contentFrameLayout);
        initViews();
        initRecycler();
    }

    @NonNull
    public static Intent makeIntent(@NonNull Context context) {
        Intent intent = new Intent(context, BooksListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public void onItemClick(@NonNull Book item) {
        presenter.onItemClick(item);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG, "error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showItems(@NonNull List<Book> items) {
        adapter.changeDataSet(items);
    }

    @Override
    public void addMoreItems(List<Book> items) {
        adapter.addAll(items);
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
    public void showDetails(Book item) {
        BookActivity.start(this, item);
    }


    private void initRecycler() {
        adapter = new BooksAdapter(new ArrayList<>());
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
