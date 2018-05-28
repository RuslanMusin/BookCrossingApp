package com.example.ruslan.curs2project.ui.start.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.member.member_item.PersonalActivity;
import com.example.ruslan.curs2project.ui.start.registration.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ruslan on 18.02.2018.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button enterBtn;
    private TextView tvRegistration;
    private TextInputLayout tiUsername;
    private TextInputLayout tiPassword;
    private EditText etUsername;
    private EditText etPassword;

    private FirebaseAuth fireAuth;

    private LoginPresenter presenter;

    public static void start(@NonNull Context activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterBtn = findViewById(R.id.btn_enter);
        tvRegistration = findViewById(R.id.link_signup);

        enterBtn.setOnClickListener(this);
        tvRegistration.setOnClickListener(this);

        etUsername = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        tiUsername = findViewById(R.id.ti_username);
        tiPassword =findViewById(R.id.ti_password);

        presenter = new LoginPresenter(this);

        fireAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.btn_enter:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                presenter.signIn(username,password);
                break;

            case R.id.link_signup:
                goToRegistration();
                break;
        }
    }

    void goToBookList(){
        PersonalActivity.start(this);
    }

    private void goToRegistration() {
        RegistrationActivity.start(this);
    }

    public void showError() {
        tiUsername.setError(getString(R.string.enter_correct_name));
        tiPassword.setError(getString(R.string.enter_correct_password));
    }

    //get-set

    public FirebaseAuth getFireAuth() {
        return fireAuth;
    }

    public void setFireAuth(FirebaseAuth fireAuth) {
        this.fireAuth = fireAuth;
    }

    public TextInputLayout getTiUsername() {
        return tiUsername;
    }

    public void setTiUsername(TextInputLayout tiUsername) {
        this.tiUsername = tiUsername;
    }

    public TextInputLayout getTiPassword() {
        return tiPassword;
    }

    public void setTiPassword(TextInputLayout tiPassword) {
        this.tiPassword = tiPassword;
    }

    public EditText getEtUsername() {
        return etUsername;
    }

    public void setEtUsername(EditText etUsername) {
        this.etUsername = etUsername;
    }

    public EditText getEtPassword() {
        return etPassword;
    }

    public void setEtPassword(EditText etPassword) {
        this.etPassword = etPassword;
    }
}
