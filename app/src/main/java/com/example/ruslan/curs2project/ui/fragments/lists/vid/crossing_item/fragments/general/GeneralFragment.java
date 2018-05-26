package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.general;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_list.member.MemberListActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.add_point.AddPointActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CommentsAdapter;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CrossingActivity;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;
import com.example.ruslan.curs2project.utils.views.CircularImageView;
import com.example.ruslan.curs2project.utils.views.ExpandableTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;

import static com.example.ruslan.curs2project.utils.Const.FOLLOWER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.OWNER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.RESTRICT_OWNER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;
import static com.example.ruslan.curs2project.utils.Const.WATCHER_TYPE;

public class GeneralFragment extends Fragment implements CrossingView,View.OnClickListener,EditCommentDialog.CommentDialogCallback{

    public final static String FR_NAME = "GeneralFragment";

    private CircularImageView ivCover;

    private TextView tvName;
    private TextView tvBookName;
    private TextView tvDate;
    private ExpandableTextView tvDescription;

    private TextView tvFollowers;

    private TextView namedTextView;

    private AppCompatButton btnAddMember;
    private AppCompatButton btnAddFollower;

    @InjectPresenter
    CrossingPresenter presenter;

    private String crossingId;

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

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

    private String currentMode;

    private List<String> followersId;

    private BookCrossing getCrossing(){
        return ((CrossingActivity)getActivity()).getBookCrossing();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cros_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        isPostExist = true;
        initViews(view);
        crossingId = getCrossing().getId();
        Log.d(TAG_LOG,"crossingId = " + crossingId);
        initRecycler();
        followersId = new ArrayList<>();
        presenter = new CrossingPresenter();
        Log.d(TAG_LOG,"init2 = " + crossingId);
        presenter.setView(this);
        currentMode = WATCHER_TYPE;
        btnAddMember.setVisibility(View.GONE);
        presenter.init(crossingId);
        presenter.findPoint(crossingId, UserRepository.getCurrentId());

        super.onViewCreated(view, savedInstanceState);
    }

    public void setUserPoint(Query query){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Point point = dataSnapshot.getValue(Point.class);

                    if (getCrossing().getLastPointId().equals(point.getId())) {
                        currentMode = RESTRICT_OWNER_TYPE;
                    } else {
                        currentMode = OWNER_TYPE;
                    }
                    btnAddMember.setVisibility(View.GONE);
                    btnAddFollower.setText(R.string.unsubscribe);
                    Log.d(TAG_LOG,"user point mode = " + currentMode);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setFollowers(Query query){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Identified followerId = postSnapshot.getValue(ElementId.class);
                    followersId.add(followerId.getId());
                    if(!(OWNER_TYPE.equals(currentMode) || RESTRICT_OWNER_TYPE.equals(currentMode))) {
                        if (followerId.getId().equals(UserRepository.getCurrentId())) {
                            currentMode = FOLLOWER_TYPE;
                            btnAddFollower.setText(R.string.unsubscribe);
                            btnAddMember.setVisibility(View.VISIBLE);
                        }
                    }

                }
                Log.d(TAG_LOG,"followers  mode = " + currentMode);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void getBookId() {
        Log.d(TAG_LOG,"init pr please");
        presenter.init(crossingId);
        Log.d(TAG_LOG,"load comments");
        }

    @Override
    public void handleError(Throwable error) {
        Log.d(TAG_LOG,"error = " + error.getMessage());
        error.printStackTrace();
        Toast.makeText(this.getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setNotLoading() {

//        isLoading = false;
    }

    public void showLoading(Disposable disposable) {
//        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
//        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setBookData(BookCrossing bookCrossing){
        Log.d(TAG_LOG,"set book data presenter");

        tvName.setText(bookCrossing.getName());
        tvBookName.setText(bookCrossing.getBookName());
        tvDescription.setText(bookCrossing.getDescription());
        tvDate.setText(sdf.format(bookCrossing.getDate()));
        setImage(bookCrossing);

        namedTextView.setText(bookCrossing.getName());
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

        commentManager.getCommentsList(this.getActivity(), crossingId, createOnCommentsChangedDataListener());
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
            mActionMode =  ((NavigationBaseActivity)getActivity()).startSupportActionMode(new ActionModeCallback(selectedComment));
        }
    }



    private boolean hasAccessToEditComment(String commentAuthorId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null && commentAuthorId.equals(currentUser.getUid());
    }

    private void initViews(View view) {
        findViews(view);

        btnAddMember.setOnClickListener(this);
        btnAddFollower.setOnClickListener(this);
        tvFollowers.setOnClickListener(this);
    }

    private void findViews(View view) {
        ivCover = view.findViewById(R.id.iv_crossing);
        tvName = view.findViewById(R.id.tv_name);
        tvBookName = view.findViewById(R.id.tv_book_name);
        tvDate = view.findViewById(R.id.tv_date);
        tvDescription = view.findViewById(R.id.extv_desc);

        btnAddMember = view.findViewById(R.id.btn_add_member);
        btnAddFollower = view.findViewById(R.id.btn_add_follower);

        namedTextView = view.findViewById(R.id.nameEditText);
        tvFollowers = view.findViewById(R.id.tv_followers);

        profileManager = ProfileManager.getInstance(this.getActivity());
        commentManager = CommentManager.getInstance(this.getActivity());

        isAuthorAnimationRequired =  ((NavigationBaseActivity)getActivity()).getIntent().getBooleanExtra(AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, false);


        commentsRecyclerView = (RecyclerView) view.findViewById(R.id.commentsRecyclerView);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        commentsLabel = (TextView) view.findViewById(R.id.commentsLabel);
        commentEditText = (EditText) view.findViewById(R.id.commentEditText);


        commentsProgressBar = (ProgressBar) view.findViewById(R.id.commentsProgressBar);
        warningCommentsTextView = (TextView) view.findViewById(R.id.warningCommentsTextView);



        final Button sendButton = (Button) view.findViewById(R.id.sendButton);

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
                if (((NavigationBaseActivity)getActivity()).hasInternetConnection()) {
                    ProfileStatus profileStatus = ProfileManager.getInstance(GeneralFragment.this.getActivity()).checkProfile();
                    sendComment();


                if (profileStatus.equals(ProfileStatus.PROFILE_CREATED)) {
                        sendComment();
                    } else {
//                        doAuthorization(profileStatus);
                    }

                } else {
                    ((NavigationBaseActivity)getActivity()).showSnackBar(R.string.internet_connection_failed);
                }
            }
        });


    }




    @Override
    public void onClick(View v) {
      switch (v.getId()){

          case R.id.btn_add_member:
//              presenter.addMember(UserRepository.getCurrentId(), crossingId);
              AddPointActivity.start(getActivity(),getCrossing());
              break;

          case R.id.btn_add_follower:
              String userId = UserRepository.getCurrentId();
              if(currentMode.equals(RESTRICT_OWNER_TYPE)) {
                  Toast.makeText(this.getActivity(),R.string.return_book_please,Toast.LENGTH_LONG).show();
              }
              if(currentMode.equals(FOLLOWER_TYPE) || currentMode.equals(OWNER_TYPE)){
                  followersId.remove(userId);
                  presenter.removeFollower(getCrossing(),userId);
                  btnAddFollower.setText(R.string.subscribe);
                  btnAddMember.setVisibility(View.GONE);

              } else {
                  followersId.add(userId);
                  presenter.addFollower(getCrossing(), userId);
                  btnAddFollower.setText(R.string.unsubscribe);
                  btnAddMember.setVisibility(View.VISIBLE);
              }


          case R.id.tv_followers:
              getCrossing().setFollowersId(followersId);
              MemberListActivity.start(getActivity(),getCrossing());
      }
    }

    private void openProfileActivity(String userId, View view) {

    }


    private void sendComment() {

        String commentText = commentEditText.getText().toString();
        Log.d(TAG_LOG, "send comment = " + commentText);
        if (commentText.length() > 0 && isPostExist) {
            commentManager.createOrUpdateComment(commentText, crossingId, new OnTaskCompleteListener() {
                @Override
                public void onTaskComplete(boolean success) {
                    if (success) {
                        scrollToFirstComment();
                    }
                }
            });
            commentEditText.setText(null);
            commentEditText.clearFocus();
            ((NavigationBaseActivity)getActivity()).hideKeyboard();
        }
    }

    private void scrollToFirstComment() {
        scrollView.smoothScrollTo(0, commentsLabel.getScrollY());

    }

    private void removeComment(String commentId, final ActionMode mode, final int position) {
        ((NavigationBaseActivity)getActivity()).showProgress();
        commentManager.removeComment(commentId, crossingId, new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(boolean success) {
                ((NavigationBaseActivity)getActivity()).hideProgress();
                mode.finish(); // Action picked, so close the CAB
                ((NavigationBaseActivity)getActivity()).showSnackBar(R.string.message_comment_was_removed);
            }
        });
    }

    private void openEditCommentDialog(Comment comment) {
        EditCommentDialog editCommentDialog = new EditCommentDialog();
        Bundle args = new Bundle();
        args.putString(EditCommentDialog.COMMENT_TEXT_KEY, comment.getText());
        args.putString(EditCommentDialog.COMMENT_ID_KEY, comment.getId());
        editCommentDialog.setArguments(args);
        editCommentDialog.show(((NavigationBaseActivity)getActivity()).getFragmentManager(), EditCommentDialog.TAG);
    }

    private void updateComment(String newText, String commentId) {
        ((NavigationBaseActivity)getActivity()).showProgress();
        commentManager.updateComment(commentId, newText, crossingId, new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(boolean success) {
                ((NavigationBaseActivity)getActivity()).hideProgress();
                ((NavigationBaseActivity)getActivity()).showSnackBar(R.string.message_comment_was_edited);
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

            menu.findItem(R.crossingId.editMenuItem).setVisible(hasAccessToEditComment(selectedComment.getAuthorId()));*/

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

