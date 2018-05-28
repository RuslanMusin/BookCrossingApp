package com.example.ruslan.curs2project.ui.crossing.crossing_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CrossingItemHolder extends RecyclerView.ViewHolder {

    private static final int MAX_LENGTH = 80;
    private static final String MORE_TEXT = "...";

    private TextView name;
    private TextView bookName;
    private TextView date;
    private ImageView imageView;

    String myFormat = "dd.MM.yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

    @NonNull
    public static CrossingItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_crossing, null);
        CrossingItemHolder holder = new CrossingItemHolder(view);
        return holder;
    }

    public CrossingItemHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tv_name);
        bookName = itemView.findViewById(R.id.tv_book_name);
        date = itemView.findViewById(R.id.tv_date);
        imageView = itemView.findViewById(R.id.iv_cover);
    }

    public void bind(@NonNull BookCrossing item) {
        name.setText(cutLongDescription(item.getName()));
        bookName.setText(item.getBookName());
        date.setText(sdf.format(item.getDate()));
        if (item.getBookPhoto() != null) {
            if(item.getBookPhoto().equals(String.valueOf(R.drawable.book_default))) {
                ImageLoadHelper.loadPictureByDrawableDefault(imageView,R.drawable.book_default);
            } else {
                ImageLoadHelper.loadPicture(imageView, item.getBookPhoto());
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
