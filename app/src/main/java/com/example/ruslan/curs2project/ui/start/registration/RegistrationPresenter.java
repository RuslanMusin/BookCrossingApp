package com.example.ruslan.curs2project.ui.start.registration;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.ui.base.NavigationPresenter;
import com.example.ruslan.curs2project.utils.ApplicationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.ruslan.curs2project.utils.Const.IMAGE_START_PATH;
import static com.example.ruslan.curs2project.utils.Const.SEP;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class RegistrationPresenter {

    private RegistrationActivity regView;

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    public RegistrationPresenter(RegistrationActivity regView) {
        this.regView = regView;
    }

    void createAccount(String email, String password) {
        Log.d(TAG_LOG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        regView.showProgressDialog();

        regView.getFireAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(regView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_LOG, "createUserWithEmail:success");
                            FirebaseUser user = regView.getFireAuth().getCurrentUser();
                            createInDatabase(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_LOG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(regView, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        regView.hideProgressDialog();
                    }
                });
    }

    private void createInDatabase(FirebaseUser firebaseUser) {
        User user = new User();

        int selectedId = regView.getRadioSexGroup().getCheckedRadioButtonId();
        RadioButton radioSexButton = (RadioButton) regView.findViewById(selectedId);
        String gender = radioSexButton.getText().toString();

        long date = 0;
        try {
            date = sdf.parse(regView.getEtDate().getText().toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String username = regView.getEtUsername().getText().toString();
        String email = regView.getEtEmail().getText().toString();

        user.setEmail(email);
        user.setUsername(username);
        user.setCity(regView.getCity());
        user.setCountry(regView.getCountry());
        user.setBirthday(date);
        user.setGender(gender);
        user.setId(firebaseUser.getUid());
        user.setDesc("");

        String path = IMAGE_START_PATH + user.getId() + SEP
                + regView.getImageUri().getLastPathSegment();

        user.setPhotoUrl(path);

        regView.setUser(user);

        StorageReference childRef = ApplicationHelper.getStorageReference().child(user.getPhotoUrl());

        //uploading the image
        UploadTask uploadTask = childRef.putFile(regView.getImageUri());

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(regView, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                pd.dismiss();
                Toast.makeText(regView, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            }
        });

        RepositoryProvider.getUserRepository().createUser(user);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = regView.getEtEmail().getText().toString();
        if (TextUtils.isEmpty(email)) {
            regView.getTiEmail().setError(regView.getString(R.string.enter_correct_name));
            valid = false;
        } else {
            regView.getTiEmail().setError(null);
        }

        String password = regView.getEtPassword().getText().toString();
        if (TextUtils.isEmpty(password)) {
            regView.getTiPassword().setError(regView.getString(R.string.enter_correct_password));
            valid = false;
        } else {
            regView.getTiPassword().setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser firebaseUser) {
        regView.hideProgressDialog();
        if (firebaseUser != null) {
            NavigationPresenter.setCurrentUser(regView.getUser());
            regView.goToBookList();
        }
    }

    public String formatDate(Calendar calendar){
        return sdf.format(calendar.getTime());
    }
}
