package com.example.ruslan.curs2project.ui.member.member_item;


import android.util.Log;

import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.UserRelation;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import static com.example.ruslan.curs2project.utils.Const.ADD_REQUEST;
import static com.example.ruslan.curs2project.utils.Const.OWNER_TYPE;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;
import static com.example.ruslan.curs2project.utils.Const.USER_KEY;

public class PersonalPresenter {

    private PersonalActivity personalActivity;

    public PersonalPresenter(PersonalActivity personalActivity) {
        this.personalActivity = personalActivity;
    }

    public void setUserRelationAndView(String userJson) {
        if(userJson != null){
            Log.d(TAG_LOG,"not my");
            User user = new Gson().fromJson(personalActivity.getIntent().getStringExtra(USER_KEY),User.class);
            personalActivity.setUser(user);
            if(!user.getId().equals(UserRepository.getCurrentId())) {
                Query query = new UserRepository().checkType(UserRepository.getCurrentId(), user.getId());
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            UserRelation userRelation = dataSnapshot.getValue(UserRelation.class);
                            personalActivity.setType(userRelation.getRelation());
                        } else {
                            personalActivity.setType(ADD_REQUEST);
                        }
                        personalActivity.initViews();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                query.addListenerForSingleValueEvent(listener);
            } else {
                personalActivity.setType(OWNER_TYPE);
            }
        } else {
            personalActivity.setType(OWNER_TYPE);
        }

        if(OWNER_TYPE.equals(personalActivity.getType())) {
            personalActivity.initViews();
        }
    }
}
