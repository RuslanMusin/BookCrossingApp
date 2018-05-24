package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_list;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;

import java.util.List;

/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */

public class CrossingAdapter extends BaseAdapter<BookCrossing, CrossingItemHolder> {

    public CrossingAdapter(@NonNull List<BookCrossing> items) {
        super(items);
    }

    @Override
    public CrossingItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CrossingItemHolder.create(parent.getContext());
    }

    @Override
    public void onBindViewHolder(CrossingItemHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        BookCrossing item = getItem(position);
        holder.bind(item);
    }
}
