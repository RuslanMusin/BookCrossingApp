package com.example.ruslan.curs2project.ui.start.registration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.book.main_book_list.BooksListActivity;
import com.example.ruslan.curs2project.ui.start.login.LoginActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener{

    private static final int RESULT_LOAD_IMG = 0;

    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private AppCompatButton btnRegistrNewReader;
    private TextView tvAddPhoto;
    private LinearLayout liAddPhoto;
    private TextView tvLogin;
    private TextInputLayout tiEmail;
    private TextInputLayout tiPassword;
    private TextInputLayout tiUsername;
    private TextInputLayout tiDate;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;
    private EditText etDate;

    private User user;
    private FirebaseAuth fireAuth;

    private String country;
    private String city;
    private Uri imageUri;

    private RegistrationPresenter presenter;

    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);
        etDate = findViewById(R.id.et_date);

        tiEmail = findViewById(R.id.ti_email);
        tiPassword = findViewById(R.id.ti_password);
        tiUsername = findViewById(R.id.ti_username);
        tiDate = findViewById(R.id.ti_date);

        radioSexGroup = findViewById(R.id.radioGrp);
        btnRegistrNewReader = findViewById(R.id.btn_sign_up);
        liAddPhoto = findViewById(R.id.li_add_photo);
        tvAddPhoto = findViewById(R.id.tv_add_photo);
        tvLogin = findViewById(R.id.link_login);
        setPlaceFragment();

        btnRegistrNewReader.setOnClickListener(this);
        liAddPhoto.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        presenter = new RegistrationPresenter(this);

        setDateListener();

        fireAuth = FirebaseAuth.getInstance();

    }

    private void setPlaceFragment(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment_city);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint(getString(R.string.select_place));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG_LOG, "Place: " + place.getId());
                String address = (String) place.getAddress();
                String[] parts = address.split(",");
                city = parts[0].trim();
                country = parts[1].trim();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG_LOG, "An error occurred: " + status);
            }
        });
    }

    private void setDateListener(){
        Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(Calendar calendar) {
        String date = presenter.formatDate(calendar);
        Log.d(TAG_LOG,"calendar = " + date);
        etDate.setText(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_up:
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                presenter.createAccount(username,password);
                break;

            case R.id.li_add_photo:
                addPhoto();
                break;

            case R.id.link_login:
                LoginActivity.start(this);
        }
    }

    private void addPhoto(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        tvAddPhoto.setText(R.string.photo_uploaded);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
        }else {
            Toast.makeText(RegistrationActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    void goToBookList(){
        BooksListActivity.start(this);
    }

    //set-get

    public FirebaseAuth getFireAuth() {
        return fireAuth;
    }

    public void setFireAuth(FirebaseAuth fireAuth) {
        this.fireAuth = fireAuth;
    }

    public RadioGroup getRadioSexGroup() {
        return radioSexGroup;
    }

    public void setRadioSexGroup(RadioGroup radioSexGroup) {
        this.radioSexGroup = radioSexGroup;
    }

    public RadioButton getRadioSexButton() {
        return radioSexButton;
    }

    public void setRadioSexButton(RadioButton radioSexButton) {
        this.radioSexButton = radioSexButton;
    }

    public AppCompatButton getBtnRegistrNewReader() {
        return btnRegistrNewReader;
    }

    public void setBtnRegistrNewReader(AppCompatButton btnRegistrNewReader) {
        this.btnRegistrNewReader = btnRegistrNewReader;
    }

    public TextView getTvAddPhoto() {
        return tvAddPhoto;
    }

    public void setTvAddPhoto(TextView tvAddPhoto) {
        this.tvAddPhoto = tvAddPhoto;
    }

    public LinearLayout getLiAddPhoto() {
        return liAddPhoto;
    }

    public void setLiAddPhoto(LinearLayout liAddPhoto) {
        this.liAddPhoto = liAddPhoto;
    }

    public TextView getTvLogin() {
        return tvLogin;
    }

    public void setTvLogin(TextView tvLogin) {
        this.tvLogin = tvLogin;
    }

    public TextInputLayout getTiEmail() {
        return tiEmail;
    }

    public void setTiEmail(TextInputLayout tiEmail) {
        this.tiEmail = tiEmail;
    }

    public TextInputLayout getTiPassword() {
        return tiPassword;
    }

    public void setTiPassword(TextInputLayout tiPassword) {
        this.tiPassword = tiPassword;
    }

    public TextInputLayout getTiUsername() {
        return tiUsername;
    }

    public void setTiUsername(TextInputLayout tiUsername) {
        this.tiUsername = tiUsername;
    }

    public TextInputLayout getTiDate() {
        return tiDate;
    }

    public void setTiDate(TextInputLayout tiDate) {
        this.tiDate = tiDate;
    }

    public EditText getEtEmail() {
        return etEmail;
    }

    public void setEtEmail(EditText etEmail) {
        this.etEmail = etEmail;
    }

    public EditText getEtPassword() {
        return etPassword;
    }

    public void setEtPassword(EditText etPassword) {
        this.etPassword = etPassword;
    }

    public EditText getEtUsername() {
        return etUsername;
    }

    public void setEtUsername(EditText etUsername) {
        this.etUsername = etUsername;
    }

    public EditText getEtDate() {
        return etDate;
    }

    public void setEtDate(EditText etDate) {
        this.etDate = etDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
