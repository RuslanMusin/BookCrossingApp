package com.example.ruslan.curs2project.ui.fragments.lists.vid.add_crossing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.repository.json.BookCrossingRepository;
import com.example.ruslan.curs2project.repository.json.UserRepository;
import com.example.ruslan.curs2project.ui.base.BaseActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CrossingActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.ruslan.curs2project.utils.Const.BOOK_KEY;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class AddCrossingActivity extends BaseActivity implements AddCrossingView{

    int PLACE_PICKER_REQUEST = 1;

    private Toolbar toolbar;

    private TextInputLayout tiPhrase;
    private TextInputLayout tiTitle;
    private TextInputLayout tiDescription;
    private TextInputLayout tiDate;
    private TextInputLayout tiPlace;

    private EditText etPhrase;
    private EditText etTitle;
    private EditText etDescription;
    private EditText etDate;
    private EditText etPlace;

    @InjectPresenter
    AddCrossingPresenter presenter;

    private String bookId;
    private String bookName;
    private String bookPhoto;


    private Place place;

    private Book book;

    private BookCrossingRepository crossingRepository = new BookCrossingRepository();

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    public static void start(@NonNull Activity activity, @NonNull Book comics) {
        Intent intent = new Intent(activity, AddCrossingActivity.class);
       /* intent.putExtra(NAME_KEY, comics.getName());
        intent.putExtra(PHOTO_KEY, comics.getPhotoUrl());
        intent.putExtra(ID_KEY, comics.getId());*/
        Gson gson = new Gson();
        String bookJson = gson.toJson(comics);
        intent.putExtra(BOOK_KEY,bookJson);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crossing);
        initViews();
        String bookJson = getIntent().getStringExtra(BOOK_KEY);
        book = new Gson().fromJson(bookJson,Book.class);
        if(book != null) {
            /*bookId = getIntent().getStringExtra(ID_KEY);
            bookName = getIntent().getStringExtra(NAME_KEY);
            bookPhoto = getIntent().getStringExtra(PHOTO_KEY);*/
            bookId = book.getId();
            bookName = book.getName();
            bookPhoto = book.getPhotoUrl();
        }
    }

    private void initViews() {
        findViews();
        setSupportActionBar(toolbar);
        setBackArrow(toolbar);
        setDateListener();
        setPlaceListener();
    }

    private void findViews() {
        toolbar = findViewById(R.id.tb_comics);

        tiPhrase = findViewById(R.id.ti_phrase);
        tiTitle = findViewById(R.id.ti_title);
        tiDescription = findViewById(R.id.ti_desc);
        tiDate = findViewById(R.id.ti_date);
        tiPlace = findViewById(R.id.ti_place);

        etPhrase = findViewById(R.id.et_phrase);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_desc);
        etDate = findViewById(R.id.et_date);
        etPlace = findViewById(R.id.et_place);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);

        MenuItem checkItem = menu.findItem(R.id.action_check);
        checkItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = etTitle.getText().toString();
                String desc = etDescription.getText().toString();
                String key = etPhrase.getText().toString();

                LatLng latLng = place.getLatLng();

                long date = 0;
                try {
                    date = sdf.parse(etDate.getText().toString()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                BookCrossing bookCrossing = new BookCrossing();
                bookCrossing.setBookId(bookId);
                bookCrossing.setBookName(bookName);
                bookCrossing.setBookPhoto(bookPhoto);
                bookCrossing.setBookAuthor(book.getAuthors().get(0));
                bookCrossing.setDate(date);
                bookCrossing.setKeyPhrase(key);
                bookCrossing.setDescription(desc);
                bookCrossing.setName(title);

                Point point = new Point();
                point.setDate(date);
                point.setDesc(desc);
                point.setLatitude(latLng.latitude);
                point.setLongitude(latLng.longitude);
                point.setPhotoUrl(place.getId());
                point.setEditorId(UserRepository.getCurrentId());

                List<Point> points = new ArrayList<>();
                points.add(point);

                bookCrossing.setPoints(points);

                crossingRepository.createCrossing(bookCrossing, UserRepository.getCurrentId());

                CrossingActivity.start(AddCrossingActivity.this,bookCrossing);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setPlaceListener(){
        etPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(AddCrossingActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                etPlace.setText(place.getAddress());

            }
        }
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
                new DatePickerDialog(AddCrossingActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(Calendar calendar) {
        String date = sdf.format(calendar.getTime());
        Log.d(TAG_LOG,"calendar = " + date);
        etDate.setText(date);
    }
}
