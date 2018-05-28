package com.example.ruslan.curs2project.ui.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationBaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends NavigationBaseActivity implements CompoundButton.OnCheckedChangeListener{

    private boolean lastCheck;


    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activtiy_settings, contentFrameLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        supportActionBar(toolbar);

        Switch swSendNotif = (Switch) findViewById(R.id.sw_send_notif);
        if (swSendNotif != null) {
            swSendNotif.setOnCheckedChangeListener(this);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "on" : "off"),
                Toast.LENGTH_SHORT).show();
        lastCheck = isChecked;
        RepositoryProvider.getBookCrossingRepository()
                .loadByUser(UserRepository.getCurrentId()).subscribe(this::checkSubs,this::handleError);

    }

    private void checkSubs(Query query) {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        if(lastCheck) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Identified crossing = postSnapshot.getValue(ElementId.class);
                        firebaseMessaging.subscribeToTopic(crossing.getId());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Identified crossing = postSnapshot.getValue(ElementId.class);
                        firebaseMessaging.unsubscribeFromTopic(crossing.getId());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void handleError(Throwable throwable) {

    }
}
