/*
package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.CommentTwo;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.ID_KEY;
import static com.example.ruslan.curs2project.utils.Const.NAME_KEY;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;


*/
/**
 * Created by Nail Shaykhraziev on 25.02.2018.
 *//*

public class CrossingActivity extends NavigationBaseActivity implements View.OnClickListener {

    private static final String TAG = "BaseAcivity";
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView ivCover;

    private TextView tvName;
    private TextView tvBookName;
    private TextView tvDate;
    private TextView tvDescription;

    private AppCompatButton btnAddMember;

    private EmptyStateRecyclerView recyclerView;
    private TextView tvEmpty;

    private CommentAdapter adapter;
    private ProgressBar progressBar;

    private boolean isLoading = false;

    */
/*@InjectPresenter
    CrossingPresenter presenter;*//*


    private String id;

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    public static void start(@NonNull Activity activity, @NonNull BookCrossing comics) {
        Intent intent = new Intent(activity, CrossingActivity.class);
        intent.putExtra(NAME_KEY, comics.getName());
        intent.putExtra(ID_KEY, comics.getId());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_crossing, contentFrameLayout);
        initViews();
        id = getIntent().getStringExtra(ID_KEY);
        initRecycler();
    }

    @Override
    public void getBookId() {
        presenter.init(id);
        Log.d(TAG_LOG,"load comments");

        presenter.loadComments(id);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG_LOG,"error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showItems(@NonNull Query items) {
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CommentTwo> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    comments.add(postSnapshot.getValue(CommentTwo.class));
                }
                adapter.changeDataSet(comments);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });


    }

    @Override
    public void addMoreItems(Query items) {
        items.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CommentTwo> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    comments.add(postSnapshot.getValue(CommentTwo.class));
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
    public void setBookData(BookCrossing bookCrossing){
        Log.d(TAG_LOG,"set book data presenter");

        tvName.setText(bookCrossing.getName());
        tvBookName.setText(bookCrossing.getBookName());
        tvDescription.setText(bookCrossing.getDescription());
        tvDate.setText(sdf.format(bookCrossing.getDate()));
        setImage(bookCrossing);
    }

    public void setImage(BookCrossing comics) {
        if (comics.getBookPhoto() != null) {
           ImageLoadHelper.loadPicture(ivCover,comics.getBookPhoto());
        } else {
            ImageLoadHelper.loadPictureByDrawable(ivCover, R.drawable.book_default);
        }
    }

    private void initRecycler() {
        adapter = new CommentAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setEmptyView(tvEmpty);
        adapter.attachToRecyclerView(recyclerView);
//        adapter.setOnItemClickListener(this);
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
                        presenter.loadNextComments(++currentPage, id);
                    }
                }
            }
        });
    }

    private void initViews() {
        findViews();
        supportActionBar(toolbar);
//        setBackArrow(toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra(NAME_KEY));

        btnAddMember.setOnClickListener(this);
    }

    private void findViews() {
        collapsingToolbar = findViewById(R.id.ct_comics);
        toolbar = findViewById(R.id.tb_comics);
        ivCover = findViewById(R.id.iv_book);
        tvName = findViewById(R.id.tv_name);
        tvBookName = findViewById(R.id.tv_book_name);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.tv_desc);
        btnAddMember = findViewById(R.id.btn_add_member);
        progressBar = findViewById(R.id.pg_comics_list);
        recyclerView = findViewById(R.id.rv_comics_list);
        tvEmpty = findViewById(R.id.tv_empty);
    }


    @Override
    public void onClick(View v) {
      switch (v.getId()){

          case R.id.btn_add_member:
              presenter.addMember(UserRepository.getCurrentId(),id);
              break;
      }
    }
}*/
