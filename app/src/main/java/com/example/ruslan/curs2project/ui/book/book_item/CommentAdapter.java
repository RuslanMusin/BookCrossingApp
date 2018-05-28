package com.example.ruslan.curs2project.ui.book.book_item;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;

import java.util.List;

public class CommentAdapter extends BaseAdapter<Comment, CommentItemHolder> {

    private OnCommentClickListener listener;

    public CommentAdapter(@NonNull List<Comment> items, OnCommentClickListener listener) {
        super(items);
        this.listener = listener;
    }

    @Override
    public CommentItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CommentItemHolder.create(parent.getContext(),listener);
    }

    @Override
    public void onBindViewHolder(CommentItemHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Comment item = getItem(position);
        holder.bind(item);
    }
}
