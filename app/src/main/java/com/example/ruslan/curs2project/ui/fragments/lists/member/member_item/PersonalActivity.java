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
import com.example.ruslan.curs2project.api.ApiFactory;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.UserRelation;
import com.example.ruslan.curs2project.model.pojo.Message;
import com.example.ruslan.curs2project.model.pojo.Notification;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_list.CrossingListActivity;
import com.example.ruslan.curs2project.ui.fragments.single.ChangeUserDataActivity;
import com.example.ruslan.curs2project.utils.FormatterUtil;
import com.example.ruslan.curs2project.utils.RxUtils;
import com.example.ruslan.curs2project.utils.views.ExpandableTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.Date;

import io.reactivex.Single;

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

    private TextView tvCountry;
    private TextView tvCity;
    private ExpandableTextView tvDesc;
    private TextView tvGender;
    private TextView tvCrossings;

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
        Message message = new Message();
        Notification notification = new Notification();
        notification.setTitle("In " + "gop" + " changed date");
        notification.setBody("new data : " + FormatterUtil.formatFirebaseDay(new Date(1527220800000L)) +
                " and owner : " + UserRepository.getCurrentId());

        message.setNotification(notification);
        message.setTo("/topics/-LDM3zALaf-tOwcT_vns");

        String messageJson = new Gson().toJson(message);

        Log.d(TAG_LOG, "messageGson =  " + messageJson);

        Single<String> response = ApiFactory.getMessageService()
                .sendMessage(message).compose(RxUtils.asyncSingle());;

        response.subscribe(this::show,this::handle);

    }

    private void handle(Throwable throwable) {
        Log.d(TAG_LOG,throwable.getMessage());
        throwable.printStackTrace();
    }

    private void show(String s) {
        Log.d(TAG_LOG,"resp = " + s);
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
            user.setId(UserRepository.getCurrentId());
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
        tvCrossings.setOnClickListener(this);
//        setBackArrow(toolbar);

    }

    private void findViews() {
        collapsingToolbar = findViewById(R.id.ct_personal);
        toolbar = findViewById(R.id.toolbar);
        tvCountry = findViewById(R.id.tv_country);
        tvCity = findViewById(R.id.tv_city);
        tvDesc = findViewById(R.id.extv_desc);
        tvGender = findViewById(R.id.tv_gender);
        tvCrossings = findViewById(R.id.tv_crossings);
        ivPhoto = findViewById(R.id.iv_crossing);

        btnAddFriend = findViewById(R.id.btn_add_friend);
        tvNameBar = findViewById(R.id.nameEditText);

        if(type.equals(OWNER_TYPE)) {
            UserRepository userRepository = new UserRepository();
            Log.d(TAG, "user id = " + UserRepository.getCurrentId());
            DatabaseReference reference = userRepository.readUser(UserRepository.getCurrentId());
            user = NavigationPresenter.getCurrentUser();
            setUserData();
        } else {
            setUserData();
        }

       /* btnChangeData = findViewById(R.id.btn_change_data);

        btnChangeData.setOnClickListener(this);*/
    }

    private void setUserData() {
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
