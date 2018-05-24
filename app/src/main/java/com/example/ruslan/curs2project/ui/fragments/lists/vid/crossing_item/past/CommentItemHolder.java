package com.example.ruslan.curs2project.ui.fragments.lists.vid.crossing_item.past;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.CommentTwo;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;

public class CommentItemHolder extends RecyclerView.ViewHolder {

    private static final int MAX_LENGTH = 80;
    private static final String MORE_TEXT = "...";

    private TextView author;
    private TextView content;
    private ImageView imageView;

    @NonNull
    public static CommentItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_comment, null);
        CommentItemHolder holder = new CommentItemHolder(view);
        return holder;
    }

    public CommentItemHolder(View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.tv_author);
        content = itemView.findViewById(R.id.tv_content);
        imageView = itemView.findViewById(R.id.iv_author);
    }

    public void bind(@NonNull CommentTwo item) {
        author.setText(item.getAuthor());
        if (item.getContent() != null) {
            content.setText(cutLongDescription(item.getContent()));
        }
        if (item.getPhotoUrl() != null) {
            /*ImageLoadHelper.loadPicture(imageView, String.format("%s.%s", item.getPhotoUrl(),
                    item.getPhotoUrl().getExtension()));*/
            ImageLoadHelper.loadPicture(imageView,item.getPhotoUrl());
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
