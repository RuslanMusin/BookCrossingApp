package com.example.ruslan.curs2project.ui.crossing.crossing_item.fragments.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.ruslan.curs2project.model.Point;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class ImageHelper {

    protected GeoDataClient mGeoDataClient;

    public ImageHelper(GeoDataClient mGeoDataClient) {
        this.mGeoDataClient = mGeoDataClient;
    }

    public void getPhotos(ImageView imageView, Point point) {
        final String placeId = point.getPhotoUrl();
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Integer width = imageView.getWidth() <= 0 ? imageView.getMaxWidth() : imageView.getWidth();
                Integer height = imageView.getHeight() <= 0 ? imageView.getMaxHeight() : imageView.getHeight();

                Log.d(TAG_LOG, " wid = " + width + "  heigh = " + height);


                photoMetadata.getScaledPhoto(mGeoDataClient.asGoogleApiClient(),width,height
                        ).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                    @Override
                    public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
                        imageView.setImageBitmap(placePhotoResult.getBitmap());
                        photoMetadataBuffer.release();
                    }
                });
            }

        });


    }

    private void showItems(ImageView imageView,Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        Log.d(TAG_LOG,throwable.getMessage());
    }
}
