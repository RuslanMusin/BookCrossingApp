package com.example.ruslan.curs2project.ui.fragments.lists.member.member_item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.example.ruslan.curs2project.model.db_dop_models.UserRelation;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.fragments.single.ChangeUserDataActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;

    private AppCompatButton btnChangeData;

    private TextView tvUsername;
    private TextView tvCountry;
    private TextView tvCity;
    private TextView tvDesc;
    private TextView tvGender;

    private TextView tvNameBar;

    private AppCompatButton btnAddFriend;

    private ImageView ivPhoto;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private static final String TAG = "PersonalActivity";

    private User user;

    private String type;

    public static void start(@NonNull Activity activity, @NonNull User comics) {
        Intent intent = new Intent(activity, PersonalActivity.class);
        Gson gson = new Gson();
        String bookJson = gson.toJson(comics);
       intent.putExtra(USER_KEY,bookJson);
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
        String userJson = getIntent().getStringExtra(USER_KEY);
        if(userJson != null){
            Log.d(TAG_LOG,"not my");
            user = new Gson().fromJson(getIntent().getStringExtra(USER_KEY),User.class);
            if(!user.getId().equals(UserRepository.getCurrentId())) {
                Query query = new UserRepository().checkType(UserRepository.getCurrentId(), user.getId());
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            UserRelation userRelation = dataSnapshot.getValue(UserRelation.class);
                            type = userRelation.getRelation();
                        } else {
                            type = ADD_REQUEST;
                        }
                        initViews();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                query.addListenerForSingleValueEvent(listener);
            } else {
                type = OWNER_TYPE;
            }
        } else {
            type = OWNER_TYPE;
        }

        if(OWNER_TYPE.equals(type)) {
            initViews();
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
                type = ADD_REQUEST;
                btnAddFriend.setText(R.string.add_friend);
                break;

            case REMOVE_REQUEST :
                new UserRepository().removeFriendRequest(UserRepository.getCurrentId(),user.getId());
                type = ADD_FRIEND;
                btnAddFriend.setText(R.string.add_friend);
                break;

        }
    }


    @NonNull
    public static Intent makeIntent(@NonNull Context context){
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private void changeData(){
        startActivity(ChangeUserDataActivity.makeIntent(PersonalActivity.this));
    }

    private void initViews() {
        Log.d(TAG_LOG,"type = " + type);
        findViews();
        supportActionBar(toolbar);

        btnAddFriend.setOnClickListener(this);
//        setBackArrow(toolbar);

    }

    private void findViews() {
        collapsingToolbar = findViewById(R.id.ct_personal);
        toolbar = findViewById(R.id.toolbar);
        tvUsername = findViewById(R.id.tv_username);
        tvCountry = findViewById(R.id.tv_country);
        tvCity = findViewById(R.id.tv_city);
        tvDesc = findViewById(R.id.tv_desc);
        tvGender = findViewById(R.id.tv_gender);
        ivPhoto = findViewById(R.id.iv_crossing);

        btnAddFriend = findViewById(R.id.btn_add_friend);
        tvNameBar = findViewById(R.id.nameEditText);

        if(type.equals(OWNER_TYPE)) {
            UserRepository userRepository = new UserRepository();
            Log.d(TAG, "user id = " + UserRepository.getCurrentId());
            DatabaseReference reference = userRepository.readUser(UserRepository.getCurrentId());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "loadPost:onChange");
                    user = dataSnapshot.getValue(User.class);

                    Log.d(TAG, "user " + user.getId() + " " + user.getUsername());
                    System.out.println("user " + user.getId() + " " + user.getUsername());

                    setUserData();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        } else {
            setUserData();
        }

       /* btnChangeData = findViewById(R.id.btn_change_data);

        btnChangeData.setOnClickListener(this);*/
    }

    private void setUserData() {
        tvUsername.setText(user.getUsername());
        tvCountry.setText(user.getCountry());
        tvCity.setText(user.getCity());
        tvGender.setText(user.getGender());
        tvDesc.setText(user.getDesc());

        tvNameBar.setText(user.getUsername());

//                collapsingToolbar.setTitle(user.getUsername());

        StorageReference imageReference = storageReference.child(user.getPhotoUrl());

        Log.d(TAG, "name " + imageReference.getPath());

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

}
