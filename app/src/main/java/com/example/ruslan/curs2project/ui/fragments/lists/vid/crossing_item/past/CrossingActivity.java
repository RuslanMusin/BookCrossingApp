package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.past;

/*
public class CrossingActivity extends NavigationBaseActivity implements CrossingView,View.OnClickListener,EditCommentDialog.CommentDialogCallback {

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

    private ProgressBar progressBar;

    private boolean isLoading = false;


    @InjectPresenter
    CrossingPresenter presenter;

    private String id;

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    public static final String POST_ID_EXTRA_KEY = "PostDetailsActivity.POST_ID_EXTRA_KEY";
    public static final String AUTHOR_ANIMATION_NEEDED_EXTRA_KEY = "PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY";
    private static final int TIME_OUT_LOADING_COMMENTS = 30000;
    public static final int UPDATE_POST_REQUEST = 1;
    public static final String POST_STATUS_EXTRA_KEY = "PostDetailsActivity.POST_STATUS_EXTRA_KEY";

    private EditText commentEditText;

    private ScrollView scrollView;
    private TextView commentsLabel;
    private TextView commentsCountTextView;
    private TextView watcherCounterTextView;
    private TextView dateTextView;
    private ProgressBar commentsProgressBar;
    private RecyclerView commentsRecyclerView;
    private TextView warningCommentsTextView;

    private boolean attemptToLoadComments = false;

    private MenuItem complainActionMenuItem;
    private MenuItem editActionMenuItem;
    private MenuItem deleteActionMenuItem;

    private String postId;

    private CommentManager commentManager;
    private ProfileManager profileManager;
    private boolean postRemovingProcess = false;
    private boolean isPostExist;
    private boolean authorAnimationInProgress = false;

    private boolean isAuthorAnimationRequired;
    private CommentsAdapter commentsAdapter;
    private ActionMode mActionMode;
    private boolean isEnterTransitionFinished = false;

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
        isPostExist = true;
    }

    @Override
    public void getBookId() {
        presenter.init(id);
        Log.d(TAG_LOG,"load comments");

//        presenter.loadComments(id);
    }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG_LOG,"error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

   */
/**//*


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
            mActionMode = startSupportActionMode(new ActionModeCallback(selectedComment));
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

    private boolean hasAccessToEditComment(String commentAuthorId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null && commentAuthorId.equals(currentUser.getUid());
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

        profileManager = ProfileManager.getInstance(this);
        commentManager = CommentManager.getInstance(this);

        isAuthorAnimationRequired = getIntent().getBooleanExtra(AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, false);
//        postId = getIntent().getStringExtra(POST_ID_EXTRA_KEY);


        commentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        commentsLabel = (TextView) findViewById(R.id.commentsLabel);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
     */
/*   commentsCountTextView = (TextView) findViewById(R.id.commentsCountTextView);
        watcherCounterTextView = (TextView) findViewById(R.id.watcherCounterTextView);*//*

//        dateTextView = (TextView) findViewById(R.id.dateTextView);
        commentsProgressBar = (ProgressBar) findViewById(R.id.commentsProgressBar);
        warningCommentsTextView = (TextView) findViewById(R.id.warningCommentsTextView);



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
                    ProfileStatus profileStatus = ProfileManager.getInstance(CrossingActivity.this).checkProfile();
                    sendComment();

                    */
/*if (profileStatus.equals(ProfileStatus.PROFILE_CREATED)) {
                        sendComment();
                    } else {
//                        doAuthorization(profileStatus);
                    }*//*

                } else {
                    showSnackBar(R.string.internet_connection_failed);
                }
            }
        });


    }




    @Override
    public void onClick(View v) {
      switch (v.getId()){

          case R.id.btn_add_member:
              presenter.addMember(UserRepository.getCurrentId(),id);
              break;
      }
    }

    private void openProfileActivity(String userId, View view) {
       */
/* Intent intent = new Intent(PostDetailsActivity.this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(PostDetailsActivity.this,
                            new android.util.Pair<>(view, getString(R.string.post_author_image_transition_name)));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }*//*

    }


    private void sendComment() {
       */
/* if (post == null) {
            return;
        }*//*


        String commentText = commentEditText.getText().toString();
        Log.d(TAG_LOG, "send comment");
        if (commentText.length() > 0 && isPostExist) {
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
            hideKeyBoard();
        }
    }

    private void scrollToFirstComment() {
       */
/* if (post != null && post.getCommentsCount() > 0) {
            scrollView.smoothScrollTo(0, commentsLabel.getTop());
        }*//*

        scrollView.smoothScrollTo(0, commentsLabel.getTop());

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
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.comment_context_menu, menu);

            menu.findItem(R.id.editMenuItem).setVisible(hasAccessToEditComment(selectedComment.getAuthorId()));

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

*/
