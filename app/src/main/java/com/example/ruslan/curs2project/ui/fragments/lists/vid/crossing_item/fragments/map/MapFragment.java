package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CrossingActivity;
import com.example.ruslan.curs2project.utils.FormatterUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public final static String FR_NAME = "MapFragment";

    private SupportMapFragment mapFragment;

    private GoogleMap map;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMarkers();
    }


    public void setMarkers() {
        PolylineOptions rectOptions = new PolylineOptions();

        List<Point> points = getCrossing().getPoints();

        for(Point point : points) {

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(point.getLatLng())
                    .title(getCrossing().getName())
                    .snippet(FormatterUtil.formatFirebaseDay(new Date(point.getDate()))));
            marker.setTag(point.getId());

            rectOptions.add(point.getLatLng());
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(points.get(points.size() - 1).getLatLng()));
        Polyline polyline = map.addPolyline(rectOptions);

    }
}
