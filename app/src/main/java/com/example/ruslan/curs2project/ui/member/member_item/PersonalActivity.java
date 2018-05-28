package com.example.ruslan.curs2project.ui.member.member_item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.example.ruslan.curs2project.ui.crossing.crossing_list.CrossingListActivity;
import com.example.ruslan.curs2project.ui.start.ChangeUserDataActivity;
import com.example.ruslan.curs2project.ui.widget.ExpandableTextView;
import com.example.ruslan.curs2project.utils.ApplicationHelper;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import static com.example.ruslan.curs2project.utils.Const.ADD_FRIEND;
import static com.example.ruslan.curs2project.utils.Const.ADD_REQUEST;
import static com.example.ruslan.curs2project.utils.Const.OWNER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.REMOVE_FRIEND;
import static com.example.ruslan.curs2project.utils.Const.REMOVE_REQUEST;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;
import static com.example.ruslan.curs2project.utils.Const.USER_KEY;

public class PersonalActivity extends NavigationBaseActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private TextView tvCountry;
    private TextView tvCity;
    private ExpandableTextView tvDesc;
    private TextView tvGender;
    private TextView tvCrossings;
    private TextView tvName;
    private AppCompatButton btnAddFriend;
    private ImageView ivPhoto;
    private TextView tvDescName;

    private User user;
    private String type;

    private PersonalPresenter presenter;

    public static void start(@NonNull Activity activity, @NonNull User comics) {
        Intent intent = new Intent(activity, PersonalActivity.class);
        Gson gson = new Gson();
        String userJson = gson.toJson(comics);
        intent.putExtra(USER_KEY,userJson);
        activity.startActivity(intent);
    }

    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, PersonalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_profile, contentFrameLayout);

        presenter = new PersonalPresenter(this);

        String userJson = getIntent().getStringExtra(USER_KEY);
        presenter.setUserRelationAndView(userJson);

    }

    void initViews() {
        Log.d(TAG_LOG,"type = " + type);
        findViews();
        supportActionBar(toolbar);

        btnAddFriend.setOnClickListener(this);
        tvCrossings.setOnClickListener(this);
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        tvCountry = findViewById(R.id.tv_country);
        tvCity = findViewById(R.id.tv_city);
        tvDesc = findViewById(R.id.extv_desc);
        tvGender = findViewById(R.id.tv_gender);
        tvCrossings = findViewById(R.id.tv_crossings);
        ivPhoto = findViewById(R.id.iv_crossing);

        btnAddFriend = findViewById(R.id.btn_add_friend);
        tvName = findViewById(R.id.nameEditText);
        tvDescName = findViewById(R.id.tv_desc_name);

        if (type.equals(OWNER_TYPE)) {
            user = NavigationPresenter.getCurrentUser();
            setUserData();
        } else {
            setUserData();
        }
    }

    private void setUserData() {
        tvCountry.setText(user.getCountry());
        tvCity.setText(user.getCity());
        tvGender.setText(user.getGender());
        tvDesc.setText(user.getDesc());

        tvName.setText(user.getUsername());

        if(user.getDesc().length() > 0) {
            tvDesc.setText(user.getDesc());
        } else {
            tvDescName.setVisibility(View.GONE);
            tvDesc.setVisibility(View.GONE);
        }

        StorageReference imageReference = ApplicationHelper.getStorageReference().child(user.getPhotoUrl());

        Log.d(TAG_LOG, "name " + imageReference.getPath());

        Glide.with(PersonalActivity.this)
                .load(imageReference)
                .into(ivPhoto);

        switch (type) {
            case ADD_FRIEND:
                btnAddFriend.setText(R.string.add_friend);
                break;

            case ADD_REQUEST:
                btnAddFriend.setText(R.string.add_friend);
                break;

            case REMOVE_FRIEND:
                btnAddFriend.setText(R.string.remove_friend);
                break;

            case REMOVE_REQUEST:
                btnAddFriend.setText(R.string.remove_request);
                break;

            case OWNER_TYPE :
                btnAddFriend.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_change_data:
                changeData();
                break;

            case R.id.btn_add_friend :
                actWithUser();
                break;

            case R.id.tv_crossings :
                showCrossings();
        }
    }

    private void showCrossings() {
        User myUser = new User();
        if(type.equals(OWNER_TYPE)) {
            myUser.setId(UserRepository.getCurrentId());
            CrossingListActivity.start(this,myUser);
        } else {
            CrossingListActivity.start(this,user);
        }
    }

    private void actWithUser() {
        switch (type) {
            case ADD_FRIEND :
                new UserRepository().addFriend(UserRepository.getCurrentId(),user.getId());
                type = REMOVE_FRIEND;
                btnAddFriend.setText(R.string.remove_friend);
                break;

            case ADD_REQUEST :
                new UserRepository().addFriendRequest(UserRepository.getCurrentId(),user.getId());
                type = REMOVE_REQUEST;
                btnAddFriend.setText(R.string.remove_request);
                break;

            case REMOVE_FRIEND :
                new UserRepository().removeFriend(UserRepository.getCurrentId(),user.getId());
                type = ADD_FRIEND;
                btnAddFriend.setText(R.string.add_friend);
                break;

            case REMOVE_REQUEST :
                new UserRepository().removeFriendRequest(UserRepository.getCurrentId(),user.getId());
                type = ADD_REQUEST;
                btnAddFriend.setText(R.string.add_friend);
                break;

        }
    }

    private void changeData(){
        startActivity(ChangeUserDataActivity.makeIntent(PersonalActivity.this));
    }

    //SET-GET


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
