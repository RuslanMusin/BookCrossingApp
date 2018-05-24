package com.example.ruslan.curs2project.ui.fragments.single;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_item.PersonalActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ChangeUserDataActivity  extends BaseActivity implements View.OnClickListener {

    private EditText etUsername;

    private EditText etCity;

    private EditText etDesc;

    private ImageView photo;

    private AppCompatButton btnChangeData;

    private TextInputLayout tiUsername;

    private TextInputLayout tiCity;

    private TextInputLayout tiDesc;

    private static final String TAG = "EmailPassword";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_change_data);

        etUsername = findViewById(R.id.et_name);
        etCity = findViewById(R.id.et_city);
        etDesc = findViewById(R.id.et_desc);
        photo = findViewById(R.id.personal_photo);
        btnChangeData = findViewById(R.id.btn_change_data);

        btnChangeData.setOnClickListener(this);

        tiUsername  = findViewById(R.id.ti_username);
        tiCity = findViewById(R.id.ti_city);
        tiDesc = findViewById(R.id.ti_desc);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_change_data:
                changeData();
                break;
        }
    }


    @NonNull
    public static Intent makeIntent(@NonNull Context context){
        Intent intent = new Intent(context, ChangeUserDataActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private void changeData(){
        String username = etCity.getText().toString();
        String city = etCity.getText().toString();
        String desc = etDesc.getText().toString();
        startActivity(PersonalActivity.makeIntent(ChangeUserDataActivity.this));
    }
}
