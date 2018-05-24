package com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;

import java.util.List;

/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */

public class BooksAdapter extends BaseAdapter<Book, BooksItemHolder> {

    public BooksAdapter(@NonNull List<Book> items) {
        super(items);
    }

    @Override
    public BooksItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BooksItemHolder.create(parent.getContext());
    }

    @Override
    public void onBindViewHolder(BooksItemHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Book item = getItem(position);
        holder.bind(item);
    }
}
