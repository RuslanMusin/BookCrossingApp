package com.example.ruslan.curs2project.ui.fragments.single;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list.BooksListActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener{

    private static final int RESULT_LOAD_IMG = 0;
    private final String SHOW_DATE_DIALOG = "date_dialog";

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;
    private EditText etDate;

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    private AppCompatButton btnRegistrNewReader;
    private AppCompatButton btnUpload;

    private TextInputLayout tiEmail;
    private TextInputLayout tiPassword;
    private TextInputLayout tiUsername;
    private TextInputLayout tiDate;

    private Date birthday;

    private String country;
    private String city;
    private Uri imageUri;

    private static final String TAG = "EmailPassword";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    UserRepository userRepository = new UserRepository();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);
        etDate = findViewById(R.id.et_date);

        tiEmail = findViewById(R.id.ti_email);
        tiPassword = findViewById(R.id.ti_password);
        tiUsername = findViewById(R.id.ti_username);
        tiDate = findViewById(R.id.ti_date);

        radioSexGroup = findViewById(R.id.radioGrp);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment_city);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getId());
                String address = (String) place.getAddress();
                String[] parts = address.split(",");
                city = parts[0].trim();
                country = parts[1].trim();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        btnRegistrNewReader = findViewById(R.id.btn_sign_up);
        btnUpload = findViewById(R.id.btn_upload);

//        setEditListener();

        btnRegistrNewReader.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        setDateListener();

//        setCityListener();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

//        createProba();
        //
    }

    private void createProba(){
        User user = new User();
        user.setUsername("rus");
        user.setCountry("Mosc");
        FirebaseDatabase.getInstance().getReference().child("users").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG,"all is ok,saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"fail happened = " + e.getMessage());
            }
        });
    }

    private void setDateListener(){
        Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(Calendar calendar) {
        String date = sdf.format(calendar.getTime());
        Log.d(TAG,"calendar = " + date);
        etDate.setText(date);
    }

    @NonNull
    public static Intent makeIntent(@NonNull Context context){
        Intent intent = new Intent(context, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_up:
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                createAccount(username,password);
                break;

            case R.id.btn_upload:
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            /*try {
                imageUri = data.getData();
                *//*final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                iv.setImageBitmap(selectedImage);*//*
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
*/
            imageUri = data.getData();
        }else {
            Toast.makeText(RegistrationActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createInDatabase(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void createInDatabase(FirebaseUser firebaseUser) {
        User user = new User();

        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);
        String gender = radioSexButton.getText().toString();

        long date = 0;
        try {
            date = sdf.parse(etDate.getText().toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();

        user.setEmail(email);
        user.setUsername(username);
        user.setCity(city);
        user.setCountry(country);
        user.setBirthday(date);
        user.setGender(gender);
        user.setId(firebaseUser.getUid());
        user.setDesc("");

        Log.d(TAG,"User data : " + email + " " + username + "( " + city + ", " + country + ") " + date + " " + gender  + " id = " + user.getId());
        System.out.print("User data : " + email + " " + username + "( " + city + ", " + country + ") " + date + " " + gender + " id = " + user.getId());

        String path = "images/users/" + user.getId() + "/"
                + imageUri.getLastPathSegment();

        user.setPhotoUrl(path);

        StorageReference childRef = storageReference.child(user.getPhotoUrl());

        //uploading the image
        UploadTask uploadTask = childRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegistrationActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                pd.dismiss();
                Toast.makeText(RegistrationActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            }
        });

        userRepository.createUser(user);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            tiEmail.setError(getString(R.string.enter_correct_name));
            valid = false;
        } else {
            tiEmail.setError(null);
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

    private void goToBookList(){
        startActivity(BooksListActivity.makeIntent(RegistrationActivity.this));
    }
}
