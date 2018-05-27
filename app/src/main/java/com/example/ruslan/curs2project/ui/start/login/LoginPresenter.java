package com.example.ruslan.curs2project.ui.start.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class LoginPresenter {

    private LoginActivity logView;

    public LoginPresenter(LoginActivity logView) {
        this.logView = logView;
    }

    public void signIn(String email, String password) {
        Log.d(TAG_LOG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        logView.showProgressDialog();

        logView.getFireAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(logView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_LOG, "signInWithEmail:success");
                            FirebaseUser user = logView.getFireAuth().getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_LOG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(logView, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        logView.hideProgressDialog();
                    }
                });
    }

    public boolean validateForm() {
        boolean valid = true;

        String email = logView.getEtUsername().getText().toString();
        if (TextUtils.isEmpty(email)) {
            logView.getTiUsername().setError(logView.getString(R.string.enter_correct_name));
            valid = false;
        } else {
            logView.getTiUsername().setError(null);
        }

        String password = logView.getEtPassword().getText().toString();
        if (TextUtils.isEmpty(password)) {
            logView.getTiPassword().setError(logView.getString(R.string.enter_correct_password));
            valid = false;
        } else {
            logView.getTiPassword().setError(null);
        }

        return valid;
    }

    public void updateUI(FirebaseUser firebaseUser) {
        logView.hideProgressDialog();
        if (firebaseUser != null) {
            DatabaseReference reference = RepositoryProvider.getUserRepository().readUser(UserRepository.getCurrentId());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    NavigationPresenter.setCurrentUser(user);
                    logView.goToBookList();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            logView.showError();
        }
    }
}
