package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.image;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.utils.FormatterUtil;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import java.util.Date;
import java.util.List;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private List<Point> imagesId;

    protected GeoDataClient mGeoDataClient;

    private ImageHelper imageHelper;


    public ImageAdapter(Context c) {
        mContext = c;
        mGeoDataClient = Places.getGeoDataClient(mContext);
        imageHelper = new ImageHelper(mGeoDataClient);

    }

    public List<Point> getImagesId() {
        return imagesId;
    }

    public void setImagesId(List<Point> imagesId) {
        this.imagesId = imagesId;
    }

    public int getCount() {
        return imagesId.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG_LOG, "get photo");
        View gridViewAndroid;
        ImageView imageView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.gridview_layout, null);
            TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.android_gridview_text);
            ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.android_gridview_image);
            textViewAndroid.setText(FormatterUtil.formatFirebaseDay(new Date(imagesId.get(position).getDate())));
            imageHelper.getPhotos(imageViewAndroid,imagesId.get(position));

        /*if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);*/
        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }



}