package com.example.ruslan.curs2project.ui.fragments.lists.book.book_item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.dialogs.EditCommentDialog;
import com.example.ruslan.curs2project.enums.ProfileStatus;
import com.example.ruslan.curs2project.managers.CommentManager;
import com.example.ruslan.curs2project.managers.ProfileManager;
import com.example.ruslan.curs2project.managers.listeners.OnDataChangedListener;
import com.example.ruslan.curs2project.managers.listeners.OnTaskCompleteListener;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.CommentTwo;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.add_crossing.AddCrossingActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CommentsAdapter;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_list.CrossingListActivity;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;
import com.example.ruslan.curs2project.utils.views.ExpandableTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.BOOK_KEY;
import static com.example.ruslan.curs2project.utils.Const.ID_KEY;
import static com.example.ruslan.curs2project.utils.Const.NAME_KEY;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;


/**
 * Created by Nail Shaykhraziev on 25.02.2018.
 */
public class BookActivity extends NavigationBaseActivity implements BookView,EditCommentDialog.CommentDialogCallback {

    private static final String TAG = "BaseAcivity";
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView ivCover;
    private TextView tvAuthors;
    private ExpandableTextView tvDescription;
    private TextView tvMark;

    private TextView namedTextView;

    private CommentAdapter adapter;

    private boolean isLoading = false;

    @InjectPresenter
    BookPresenter presenter;

    private String id;

    private Book book;

    //comment
    public static final String AUTHOR_ANIMATION_NEEDED_EXTRA_KEY = "PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY";
    private static final int TIME_OUT_LOADING_COMMENTS = 30000;

    private EditText commentEditText;

    private ScrollView scrollView;
    private TextView commentsLabel;
    private ProgressBar commentsProgressBar;
    private RecyclerView commentsRecyclerView;
    private TextView warningCommentsTextView;

    private boolean attemptToLoadComments = false;

    private CommentManager commentManager;
    private ProfileManager profileManager;
    private boolean isPostExist;

    private boolean isAuthorAnimationRequired;
    private CommentsAdapter commentsAdapter;
    private ActionMode mActionMode;

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
        id = getIntent().getStringExtra(ID_KEY);
        book = new Gson().fromJson(getIntent().getStringExtra(BOOK_KEY),Book.class);
        initRecycler();
    }

    @Override
    public void getBookId() {
        presenter.init(id);
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

    @Override
    public void showLoading(Disposable disposable) {

    }

//    public void showLoading(Disposable disposable) {
//        progressBar.setVisibility(View.VISIBLE);
//    }

    public void hideLoading() {

//        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setBookData(Book book){
        Log.d(TAG_LOG,"set book data presenter");

        setAuthors(book.getAuthors());
        tvDescription.setText(book.getDesc());
        tvMark.setText(String.valueOf(book.getMark()));
        setImage(book);

        namedTextView.setText(book.getName());
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
            ImageLoadHelper.loadPictureByDrawable(ivCover, R.drawable.image_error_marvel_logo);
        }
    }


    private void initViews() {
        findViews();
        supportActionBar(toolbar);
//        setBackArrow(toolbar);

    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        ivCover = findViewById(R.id.iv_crossing);
        tvAuthors = findViewById(R.id.tv_authors);
        tvMark = findViewById(R.id.tv_mark);
        tvDescription = findViewById(R.id.extv_desc);

        namedTextView = findViewById(R.id.nameEditText);

        profileManager = ProfileManager.getInstance(this);
        commentManager = CommentManager.getInstance(this);

        isAuthorAnimationRequired =  getIntent().getBooleanExtra(AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, false);


        commentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        scrollView = findViewById(R.id.scrollView);
        commentsLabel = (TextView) findViewById(R.id.commentsLabel);
        commentEditText = (EditText) findViewById(R.id.commentEditText);


        commentsProgressBar = (ProgressBar) findViewById(R.id.commentsProgressBar);
        warningCommentsTextView = (TextView) findViewById(R.id.warningCommentsTextView);



        final Button sendButton = (Button) findViewById(R.id.sendButton);

        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG_LOG,"char length = " + charSequence.length());
                sendButton.setEnabled(charSequence.toString().trim().length() > 0);
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String charSequence = editable.toString();
                Log.d(TAG_LOG,"after char length = " + charSequence.length());
                sendButton.setEnabled(charSequence.trim().length() > 0);
                Log.d(TAG_LOG, "enabled = " + sendButton.isEnabled());
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasInternetConnection()) {
                    ProfileStatus profileStatus = ProfileManager.getInstance(BookActivity.this).checkProfile();
                    sendComment();


                    if (profileStatus.equals(ProfileStatus.PROFILE_CREATED)) {
                        sendComment();
                    } else {
//                        doAuthorization(profileStatus);
                    }

                } else {
                    showSnackBar(R.string.internet_connection_failed);
                }
            }
        });
    }

    //comment

    private void initRecycler() {
        commentsAdapter = new CommentsAdapter();
        commentsAdapter.setCallback(new CommentsAdapter.Callback() {
            @Override
            public void onLongItemClick(View view, int position) {
                Comment selectedComment = commentsAdapter.getItemByPosition(position);
                startActionMode(selectedComment);
            }

            @Override
            public void onAuthorClick(String authorId, View view) {
                openProfileActivity(authorId, view);
            }
        });
        commentsRecyclerView.setAdapter(commentsAdapter);
        commentsRecyclerView.setNestedScrollingEnabled(false);
        commentsRecyclerView.addItemDecoration(new DividerItemDecoration(commentsRecyclerView.getContext(),
                ((LinearLayoutManager) commentsRecyclerView.getLayoutManager()).getOrientation()));

        commentManager.getCommentsList(this, id, createOnCommentsChangedDataListener());
    }

    private OnDataChangedListener<Comment> createOnCommentsChangedDataListener() {
        attemptToLoadComments = true;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (attemptToLoadComments) {
                    commentsProgressBar.setVisibility(View.GONE);
                    warningCommentsTextView.setVisibility(View.VISIBLE);
                }
            }
        }, TIME_OUT_LOADING_COMMENTS);


        return new OnDataChangedListener<Comment>() {
            @Override
            public void onListChanged(List<Comment> list) {
                attemptToLoadComments = false;
                commentsProgressBar.setVisibility(View.GONE);
                commentsRecyclerView.setVisibility(View.VISIBLE);
                warningCommentsTextView.setVisibility(View.GONE);
                commentsAdapter.setList(list);
            }
        };
    }

    private void startActionMode(Comment selectedComment) {
        if (mActionMode != null) {
            return;
        }

        //check access to modify or remove post
        if (hasAccessToEditComment(selectedComment.getAuthorId())) {
            mActionMode =  startSupportActionMode(new BookActivity.ActionModeCallback(selectedComment));
        }
    }

    private boolean hasAccessToEditComment(String commentAuthorId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null && commentAuthorId.equals(currentUser.getUid());
    }


    private void openProfileActivity(String userId, View view) {

    }


    private void sendComment() {

        String commentText = commentEditText.getText().toString();
        Log.d(TAG_LOG, "send comment = " + commentText);
        if (commentText.length() > 0) {
            commentManager.createOrUpdateComment(commentText, id, new OnTaskCompleteListener() {
                @Override
                public void onTaskComplete(boolean success) {
                    if (success) {
                        scrollToFirstComment();
                    }
                }
            });
            commentEditText.setText(null);
            commentEditText.clearFocus();
            hideKeyboard();
        }
    }

    private void scrollToFirstComment() {
        scrollView.smoothScrollTo(0, commentsLabel.getScrollY());

    }

    private void removeComment(String commentId, final ActionMode mode, final int position) {
        showProgress();
        commentManager.removeComment(commentId, id, new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(boolean success) {
                hideProgress();
                mode.finish(); // Action picked, so close the CAB
                showSnackBar(R.string.message_comment_was_removed);
            }
        });
    }

    private void openEditCommentDialog(Comment comment) {
        EditCommentDialog editCommentDialog = new EditCommentDialog();
        Bundle args = new Bundle();
        args.putString(EditCommentDialog.COMMENT_TEXT_KEY, comment.getText());
        args.putString(EditCommentDialog.COMMENT_ID_KEY, comment.getId());
        editCommentDialog.setArguments(args);
        editCommentDialog.show(getFragmentManager(), EditCommentDialog.TAG);
    }

    private void updateComment(String newText, String commentId) {
        showProgress();
        commentManager.updateComment(commentId, newText, id, new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(boolean success) {
                hideProgress();
                showSnackBar(R.string.message_comment_was_edited);
            }
        });
    }

    @Override
    public void onCommentChanged(String newText, String commentId) {
        updateComment(newText, commentId);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        Comment selectedComment;
        int position;

        ActionModeCallback(Comment selectedComment) {
            this.selectedComment = selectedComment;
        }

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            /*MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.comment_context_menu, menu);

            menu.findItem(R.id.editMenuItem).setVisible(hasAccessToEditComment(selectedComment.getAuthorId()));*/

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editMenuItem:
                    openEditCommentDialog(selectedComment);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.deleteMenuItem:
                    removeComment(selectedComment.getId(), mode, position);
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    }
}