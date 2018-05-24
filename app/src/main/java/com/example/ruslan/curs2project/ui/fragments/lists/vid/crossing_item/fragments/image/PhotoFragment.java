package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.fragments.image;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.CrossingActivity;

import java.util.List;

public class PhotoFragment extends Fragment {

    public final static String FR_NAME = "PhotoFragment";


    private BookCrossing getCrossing(){
        return ((CrossingActivity)getActivity()).getBookCrossing();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cros_photo, container, false);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        ImageAdapter adapter = new ImageAdapter(this.getActivity());
        List<Point> points = getCrossing().getPoints();
        adapter.setImagesId(points);
        gridview.setAdapter(adapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ImageActivity.start(PhotoFragment.this.getActivity(),points.get(position));

            }
        });

        return view;
    }
}
