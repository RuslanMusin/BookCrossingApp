package com.example.ruslan.curs2project.ui.fragments.lists.book.main_book_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;


/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */

public class BooksItemHolder extends RecyclerView.ViewHolder {

    private static final int MAX_LENGTH = 80;
    private static final String MORE_TEXT = "...";

    private TextView name;
    private TextView description;
    private ImageView imageView;

    @NonNull
    public static BooksItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_books, null);
        BooksItemHolder holder = new BooksItemHolder(view);
        return holder;
    }

    public BooksItemHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tv_name);
        description = itemView.findViewById(R.id.tv_description);
        imageView = itemView.findViewById(R.id.iv_cover);
    }

    public void bind(@NonNull Book item) {
        name.setText(item.getName());
        if (item.getDesc() != null) {
            description.setText(cutLongDescription(item.getDesc()));
        } else {
            description.setText(imageView.getContext().getText(R.string.description_default));
        }
        if (item.getPhotoUrl() != null) {
            if(item.getPhotoUrl().equals(String.valueOf(R.drawable.book_default))) {
                ImageLoadHelper.loadPictureByDrawableDefault(imageView,R.drawable.book_default);
            } else {
                ImageLoadHelper.loadPicture(imageView, item.getPhotoUrl());
            }
        }
    }

    private String cutLongDescription(String description) {
        if (description.length() < MAX_LENGTH) {
            return description;
        } else {
            return description.substring(0, MAX_LENGTH - MORE_TEXT.length()) + MORE_TEXT;
        }
    }
}
