package com.example.ruslan.curs2project.ui.crossing.crossing_item.fragments.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.ui.crossing.crossing_item.CrossingActivity;
import com.example.ruslan.curs2project.ui.crossing.crossing_item.fragments.image.ImageHelper;
import com.example.ruslan.curs2project.utils.FormatterUtil;
import com.example.ruslan.curs2project.utils.RxUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;

import static com.example.ruslan.curs2project.utils.Const.INFORMATION_TYPE;
import static com.example.ruslan.curs2project.utils.Const.PATH_TYPE;
import static com.example.ruslan.curs2project.utils.Const.PHOTO_TYPE;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    private SupportMapFragment mapFragment;

    private MaterialSpinner spinner;
    private MaterialDialog dialog;
    private ImageHelper imageHelper;
    private ImageView selectedImage;
    private String type;

    private Polyline polylineUser;
    private Marker userMarker;

    private List<Point> points;
    private Point selectedPoint;

    private List<Thread> threads;

    private GoogleMap map;

    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    public MapFragment() {
    }

    public BookCrossing getCrossing() {
        return ((CrossingActivity) getActivity()).getBookCrossing();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cros_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        threads = new ArrayList<>();
        createLocationRequest();
        imageHelper = new ImageHelper(Places.getGeoDataClient(this.getActivity()));
        View viewFull = getLayoutInflater().inflate(R.layout.activity_full_image, null);
        selectedImage = (ImageView) viewFull.findViewById(R.id.selectedImage);
        dialog = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.title)
                .customView(viewFull, true).build();

        spinner = (MaterialSpinner) view.findViewById(R.id.spinner);
        spinner.setItems(getString(R.string.show_info), getString(R.string.show_photo), getString(R.string.show_path));
        type = INFORMATION_TYPE;
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                switch (position) {
                    case 0:
                        type = INFORMATION_TYPE;
                        stopLocationUpdates();
                        break;

                    case 1:
                        type = PHOTO_TYPE;
                        stopLocationUpdates();
                        break;

                    case 2:
                        type = PATH_TYPE;
                        startLocationUpdates();
                        break;

                }
            }
        });
    }

    private void checkAfterType() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this.getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this.getActivity(), null);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        map = googleMap;
        setMarkers();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                boolean flag = false;
                switch (type) {
                    case PHOTO_TYPE:
                        if (!marker.equals(userMarker)) {
                            imageHelper.getPhotos(selectedImage, (Point) marker.getTag());
                            dialog.show();
                            flag = true;
                            break;
                        }

                    case PATH_TYPE:
                        if (!marker.equals(userMarker)) {
                            selectedPoint = (Point) marker.getTag();
                            readLineForUser();
                            flag = true;
                            break;
                        }

                }
                return flag;
            }
        });
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mFusedLocationProviderClient.asGoogleApiClient()
                , mLocationRequest, this);

    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mFusedLocationProviderClient.asGoogleApiClient(), this);
    }

    public void setMarkers() {
        PolylineOptions rectOptions = new PolylineOptions();

        points = getCrossing().getPoints();

        for(int i = 0; i < points.size(); i++) {

            Point point = points.get(i);

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(point.getLatLng())
                    .title(getCrossing().getName())
                    .snippet(FormatterUtil.formatFirebaseDay(new Date(point.getDate()))));
            if(i == points.size()-1) {
                map.moveCamera(CameraUpdateFactory.newLatLng(point.getLatLng()));
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_book));
            }
            marker.setTag(point);

            rectOptions.add(point.getLatLng());
        }

        Polyline polyline = map.addPolyline(rectOptions.color(Color.BLUE)
                .geodesic(true));

        selectedPoint = points.get(points.size()-1);

        updateLocationUI();
        getDeviceLocation();

    }


    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            if(userMarker != null) {
                                userMarker.remove();
                            }
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            userMarker = map.addMarker(new MarkerOptions().position(latLng));
                            userMarker.setPosition(latLng);

                            readLineForUser();


                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void readLineForUser() {
        Log.d(TAG_LOG,"read line");
        if(polylineUser != null) {
            polylineUser.remove();
        }
        if(userMarker != null) {
            userMarker.remove();
        }
        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        userMarker = map.addMarker(new MarkerOptions().position(latLng));
        userMarker.setPosition(latLng);
        userMarker.setTitle(getString(R.string.person_place));
        userMarker.setSnippet(FormatterUtil.formatFirebaseDay(new Date()));
        userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name));
        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.add(selectedPoint.getLatLng());
        rectOptions.add(userMarker.getPosition());
        polylineUser = map.addPolyline(rectOptions.color(Color.RED)
                .geodesic(true));

        map.moveCamera(CameraUpdateFactory.newLatLng(userMarker.getPosition()));

    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;

        Toast.makeText(this.getActivity(), "Location changed!",
                Toast.LENGTH_SHORT).show();
        Single.just(userMarker).compose(RxUtils.asyncSingle()).subscribe(this::readLineForUser,this::handleError);
//        readLineForUser();
    }

    private void handleError(Throwable throwable) {
    }

    private void readLineForUser(Marker marker) {
        readLineForUser();
    }
}
