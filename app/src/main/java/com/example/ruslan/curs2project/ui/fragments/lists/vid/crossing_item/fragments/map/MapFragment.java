package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.map;

import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CrossingActivity;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.image.ImageHelper;
import com.example.ruslan.curs2project.utils.FormatterUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
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

import java.util.Date;
import java.util.List;

import static com.example.ruslan.curs2project.utils.Const.INFORMATION_TYPE;
import static com.example.ruslan.curs2project.utils.Const.PATH_TYPE;
import static com.example.ruslan.curs2project.utils.Const.PHOTO_TYPE;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public final static String FR_NAME = "MapFragment";
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


    public MapFragment() {
    }

    public BookCrossing getCrossing(){
        return ((CrossingActivity)getActivity()).getBookCrossing();
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

        imageHelper = new ImageHelper(Places.getGeoDataClient(this.getActivity()));
        View viewFull = getLayoutInflater().inflate(R.layout.activity_full_image,null);
        selectedImage = (ImageView) viewFull.findViewById(R.id.selectedImage);
        dialog = new MaterialDialog.Builder(this.getActivity())
                .title(R.string.title)
                .customView(viewFull, true).build();

        spinner = (MaterialSpinner) view.findViewById(R.id.spinner);
        spinner.setItems(getString(R.string.show_info), getString(R.string.show_photo), getString(R.string.show_path));
        type = INFORMATION_TYPE;
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                switch (position) {
                    case 0 :
                        type = INFORMATION_TYPE;
                        checkAfterType();
                        break;

                    case 1:
                        type = PHOTO_TYPE;
                        checkAfterType();
                        break;

                    case 2:
                        type = PATH_TYPE;
                        changeUserMarker();
                        break;

                }
            }
        });
    }

    private void checkAfterType() {
        for(Thread thread : threads) {
            thread.interrupt();
        }
    }

    private void changeUserMarker() {
        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        readLineForUser();
                        Thread.sleep(1000*30); //1000 - 1 сек
                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
                        Log.d(TAG_LOG,"has ex = " + ex.getMessage());
                    }
                }
            }
        });
        threads.add(run);
        run.start();
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
                        if (!userMarker.equals(marker)) {
                            imageHelper.getPhotos(selectedImage, (Point) marker.getTag());
                            dialog.show();
                            flag =  true;
                            break;
                        }

                    case PATH_TYPE :
                        if(!userMarker.equals(marker)) {
                            selectedPoint = (Point) marker.getTag();
                            readLineForUser();
                            flag = true;
                            break;
                        }

                }
                return flag;
            }
        });
//        showCurrentPlace();
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
        userMarker = map.addMarker(new MarkerOptions().position(selectedPoint.getLatLng()));
        getDeviceLocation();
         userMarker.setTitle(getString(R.string.person_place));
         userMarker.setSnippet(FormatterUtil.formatFirebaseDay(new Date(mLastKnownLocation.getTime())));
        userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_black_24dp));


        readLineForUser();

        changeUserMarker();

    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            userMarker.remove();
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            map.addMarker(new MarkerOptions().position(latLng));
                            userMarker.setPosition(latLng);

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void readLineForUser() {
        if(polylineUser != null) {
            polylineUser.remove();
        }
        getDeviceLocation();
        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.add(selectedPoint.getLatLng());
        rectOptions.add(userMarker.getPosition());
        polylineUser = map.addPolyline(rectOptions.color(Color.RED)
                .geodesic(true));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(),DEFAULT_ZOOM));

    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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

    /**
     * Handles the result of the request for location permissions.
     */
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

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            map.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                map.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
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
}
