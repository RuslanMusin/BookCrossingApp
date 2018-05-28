package com.example.ruslan.curs2project.ui.fragments.lists.book.book_item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_item.PersonalActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.add_crossing.AddCrossingActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_list.CrossingListActivity;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;
import com.example.ruslan.curs2project.utils.views.ExpandableTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.BOOK_KEY;
import static com.example.ruslan.curs2project.utils.Const.ID_KEY;
import static com.example.ruslan.curs2project.utils.Const.NAME_KEY;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class BookActivity extends NavigationBaseActivity implements BookView, OnCommentClickListener {

    private Toolbar toolbar;
    private ImageView ivCover;
    private TextView tvAuthors;
    private ExpandableTextView tvDescription;
    private TextView tvDescName;
    private TextView tvName;
    private EditText commentEditText;
    private EmptyStateRecyclerView recyclerView;
    private CommentAdapter adapter;

    @InjectPresenter
    BookPresenter presenter;

    private String bookId;
    private Book book;
    private List<Comment> comments;

    public static void start(@NonNull Activity activity, @NonNull Book comics) {
        Intent intent = new Intent(activity, BookActivity.class);
        Gson gson = new Gson();
        String bookJson = gson.toJson(comics);
        intent.putExtra(BOOK_KEY,bookJson);
        intent.putExtra(NAME_KEY, comics.getName());
        intent.putExtra(ID_KEY, comics.getId());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_book, contentFrameLayout);
        initViews();
        book = new Gson().fromJson(getIntent().getStringExtra(BOOK_KEY),Book.class);
        bookId = book.getId();
        initRecycler();
        comments = new ArrayList<>();
        presenter.loadComments(this,bookId);
    }

    @Override
    public void onReplyClick(int position) {
        commentEditText.setEnabled(true);
        Comment comment = comments.get(position);
        String commentString = comment.getAuthorName() + ", ";
        commentEditText.setText(commentString);
    }

    @Override
    public void onAuthorClick(String authorId) {
        DatabaseReference reference = RepositoryProvider.getUserRepository().readUser(authorId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                PersonalActivity.start(BookActivity.this,user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        adapter.changeDataSet(comments);
    }

    private void initRecycler() {
        adapter = new CommentAdapter(new ArrayList<>(),this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getBookId() {
        presenter.init(bookId);
        Log.d(TAG_LOG,"load comments");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                CrossingListActivity.start(this,book);
                break;

            case R.id.action_add:
                AddCrossingActivity.start(this,book);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG_LOG,"error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setBookData(Book book){
        Log.d(TAG_LOG,"set book data presenter");

        tvName.setText(book.getName());
        setAuthors(book.getAuthors());
        if(book.getDesc().length() > 0) {
            String text = book.getDesc().replaceAll("<.+>","");
            tvDescription.setText(text);
        } else {
            tvDescName.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
        }
        setImage(book);
    }

   public void setAuthors(List<String> authors){
       StringBuilder authorsStr = new StringBuilder();
       for(String author : authors){
           authorsStr.append(author).append("\n");
       }
       tvAuthors.setText(authorsStr);
   }

    public void setImage(Book comics) {
        if (comics.getPhotoUrl() != null) {
           ImageLoadHelper.loadPicture(ivCover,comics.getPhotoUrl());
        } else {
            ImageLoadHelper.loadPictureByDrawable(ivCover, R.drawable.ic_book);
        }
    }


    private void initViews() {
        findViews();
        supportActionBar(toolbar);
    }

    private void findViews() {
        recyclerView = findViewById(R.id.rv_comics_list);
        toolbar = findViewById(R.id.toolbar);
        ivCover = findViewById(R.id.iv_crossing);
        tvAuthors = findViewById(R.id.tv_authors);
        tvDescription = findViewById(R.id.extv_desc);
        tvName = findViewById(R.id.nameEditText);
        tvDescName = findViewById(R.id.tv_desc_name);
        commentEditText = (EditText) findViewById(R.id.commentEditText);

        final Button sendButton = (Button) findViewById(R.id.sendButton);

        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendButton.setEnabled(charSequence.toString().trim().length() > 0);
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasInternetConnection()) {
                    sendComment();
                } else {
                    showSnackBar(R.string.internet_connection_failed);
                }
            }
        });
    }

    private void sendComment() {
        String commentText = commentEditText.getText().toString();
        Log.d(TAG_LOG, "send comment = " + commentText);
        if (commentText.length() > 0) {
            Comment comment = new Comment();
            User user = NavigationPresenter.getCurrentUser();
            comment.setText(commentText);
            comment.setAuthorId(user.getId());
            comment.setAuthorName(user.getUsername());
            comment.setAuthorPhotoUrl(user.getPhotoUrl());
            comment.setCreatedDate(new Date().getTime());
            presenter.createComment(bookId,comment,this);
            commentEditText.setText(null);
            commentEditText.clearFocus();
            hideKeyboard();
        }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        adapter.changeDataSet(comments);
    }

    @Override
    public void onItemClick(@NonNull Comment item) {

    }
}