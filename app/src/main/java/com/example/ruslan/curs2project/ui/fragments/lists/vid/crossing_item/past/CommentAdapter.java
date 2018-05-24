package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.past;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.ruslan.curs2project.model.CommentTwo;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;

import java.util.List;

public class CommentAdapter extends BaseAdapter<CommentTwo, CommentItemHolder> {

    public CommentAdapter(@NonNull List<CommentTwo> items) {
        super(items);
    }

    @Override
    public CommentItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CommentItemHolder.create(parent.getContext());
    }

    @Override
    public void onBindViewHolder(CommentItemHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        CommentTwo item = getItem(position);
        holder.bind(item);
    }
}
