package com.example.ruslan.curs2project.ui.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.start.login.LoginPresenter;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    private Button enterBtn;
    private TextView tvRegistration;
    private TextInputLayout tiUsername;
    private TextInputLayout tiPassword;
    private EditText etUsername;
    private EditText etPassword;

    private FirebaseAuth fireAuth;

    private LoginPresenter presenter;


    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_settings);

        Switch swSendNotif = (Switch) findViewById(R.id.sw_send_notif);
        if (swSendNotif != null) {
            swSendNotif.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "on" : "off"),
                Toast.LENGTH_SHORT).show();
        if(isChecked) {

        } else {

        }
    }
}
