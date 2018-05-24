package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Point;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import static com.example.ruslan.curs2project.utils.Const.POINT_KEY;

public class ImageActivity extends AppCompatActivity {
    ImageView selectedImage;

    private ImageHelper imageHelper;



    public static void start(@NonNull Activity activity, @NonNull Point point) {
        Intent intent = new Intent(activity, ImageActivity.class);
        String crossingJson = new Gson().toJson(point);
        intent.putExtra(POINT_KEY,crossingJson);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        imageHelper = new ImageHelper(Places.getGeoDataClient(this));
        selectedImage = (ImageView) findViewById(R.id.selectedImage);
        Point point = new Gson().fromJson(getIntent().getStringExtra(POINT_KEY),Point.class);
        imageHelper.getPhotos(selectedImage,point);
    }
}
