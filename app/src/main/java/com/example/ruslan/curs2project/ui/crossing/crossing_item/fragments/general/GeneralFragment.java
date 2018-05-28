package com.example.ruslan.curs2project.ui.crossing.crossing_item.fragments.general;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.example.ruslan.curs2project.ui.book.book_item.CommentAdapter;
import com.example.ruslan.curs2project.ui.book.book_item.OnCommentClickListener;
import com.example.ruslan.curs2project.ui.crossing.add_point.AddPointActivity;
import com.example.ruslan.curs2project.ui.crossing.crossing_item.CrossingActivity;
import com.example.ruslan.curs2project.ui.member.member_item.PersonalActivity;
import com.example.ruslan.curs2project.ui.member.member_list.member.MemberListActivity;
import com.example.ruslan.curs2project.ui.widget.CircularImageView;
import com.example.ruslan.curs2project.ui.widget.EmptyStateRecyclerView;
import com.example.ruslan.curs2project.ui.widget.ExpandableTextView;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.ruslan.curs2project.utils.Const.FOLLOWER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.OWNER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.RESTRICT_OWNER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;
import static com.example.ruslan.curs2project.utils.Const.WATCHER_TYPE;

public class GeneralFragment extends Fragment implements GeneralView,View.OnClickListener,OnCommentClickListener {

    private CircularImageView ivCover;
    private TextView tvName;
    private TextView tvBookName;
    private TextView tvDate;
    private ExpandableTextView tvDescription;
    private TextView tvFollowers;
    private TextView tvDescName;

    private AppCompatButton btnAddMember;
    private AppCompatButton btnAddFollower;

    @InjectPresenter
    GeneralPresenter presenter;

    private String crossingId;

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    private EditText commentEditText;
    private EmptyStateRecyclerView recyclerView;
    private CommentAdapter adapter;

    private String currentMode;

    private List<String> followersId;
    private List<Comment> comments;


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
        initViews(view);
        crossingId = getCrossing().getId();
        followersId = new ArrayList<>();
        presenter = new GeneralPresenter();
        presenter.setView(this);
        currentMode = WATCHER_TYPE;
        btnAddMember.setVisibility(View.GONE);
        presenter.init(crossingId);
        presenter.findPoint(crossingId, UserRepository.getCurrentId());
        initRecycler();
        comments = new ArrayList<>();
        presenter.loadComments(this,crossingId);

        super.onViewCreated(view, savedInstanceState);
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
                PersonalActivity.start(GeneralFragment.this.getActivity(),user);
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
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);
        adapter.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
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
    public void setBookData(BookCrossing bookCrossing){
        Log.d(TAG_LOG,"set book data presenter");

        tvName.setText(bookCrossing.getName());
        tvBookName.setText(bookCrossing.getBookName());
        if(bookCrossing.getDescription().length() > 0) {
            tvDescription.setText(bookCrossing.getDescription());
        } else {
            tvDescName.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
        }
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

    private void initViews(View view) {
        findViews(view);

        btnAddMember.setOnClickListener(this);
        btnAddFollower.setOnClickListener(this);
        tvFollowers.setOnClickListener(this);
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.rv_comics_list);
        ivCover = view.findViewById(R.id.iv_crossing);
        tvBookName = view.findViewById(R.id.tv_book_name);
        tvDate = view.findViewById(R.id.tv_date);
        tvDescription = view.findViewById(R.id.extv_desc);
        tvDescName = view.findViewById(R.id.tv_desc_name);

        btnAddMember = view.findViewById(R.id.btn_add_member);
        btnAddFollower = view.findViewById(R.id.btn_add_follower);

        tvName = view.findViewById(R.id.nameEditText);
        tvFollowers = view.findViewById(R.id.tv_followers);
        commentEditText = (EditText) view.findViewById(R.id.commentEditText);

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
                    sendComment();
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
              break;

          case R.id.tv_followers:
              getCrossing().setFollowersId(followersId);
              MemberListActivity.start(getActivity(),getCrossing());
      }
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
            presenter.createComment(getCrossing().getId(),comment,this);
            commentEditText.setText(null);
            commentEditText.clearFocus();
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

