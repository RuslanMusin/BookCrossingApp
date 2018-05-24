package com.example.ruslan.curs2project.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.member.member_item.PersonalActivity;
import com.example.ruslan.curs2project.ui.fragments.single.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ruslan on 18.02.2018.
 */

public class StartActivity extends BaseActivity implements View.OnClickListener{

    private Button enterBtn;

    private Button registrBtn;

    private TextInputLayout tiUsername;

    private TextInputLayout tiPassword;

    private EditText etUsername;

    private EditText etPassword;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]


    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start);

        enterBtn = findViewById(R.id.btn_enter);
        registrBtn = findViewById(R.id.btn_reg);

        enterBtn.setOnClickListener(this);
        registrBtn.setOnClickListener(this);

        etUsername = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        tiUsername = findViewById(R.id.ti_username);
        tiPassword =findViewById(R.id.ti_password);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.btn_enter:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                signIn(username,password);
                break;

            case R.id.btn_reg:
                goToRegistration();
                break;
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(StartActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void goToBookList(){
        startActivity(PersonalActivity.makeIntent(StartActivity.this));
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etUsername.getText().toString();
        if (TextUtils.isEmpty(email)) {
            tiUsername.setError(getString(R.string.enter_correct_name));
            valid = false;
        } else {
            tiUsername.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            tiPassword.setError(getString(R.string.enter_correct_password));
            valid = false;
        } else {
            tiPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            goToBookList();
        }
    }

    private void goToRegistration() {
        startActivity(RegistrationActivity.makeIntent(StartActivity.this));
    }
}
