package com.example.ruslan.curs2project.ui.fragments.lists.member.member_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.utils.ImageLoadHelper;


/**
 * Created by Nail Shaykhraziev on 26.02.2018.
 */

public class MemberItemHolder extends RecyclerView.ViewHolder {

    private static final int MAX_LENGTH = 80;
    private static final String MORE_TEXT = "...";

    private TextView name;
    private TextView description;
    private ImageView imageView;

    @NonNull
    public static MemberItemHolder create(@NonNull Context context) {
        View view = View.inflate(context, R.layout.item_books, null);
        MemberItemHolder holder = new MemberItemHolder(view);
        return holder;
    }

    public MemberItemHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tv_name);
        description = itemView.findViewById(R.id.tv_description);
        imageView = itemView.findViewById(R.id.iv_cover);
    }

    public void bind(@NonNull User item) {
        name.setText(item.getUsername());
        if (item.getDesc() != null) {
            description.setText(cutLongDescription(item.getCity()));
        } else {
            description.setText(imageView.getContext().getText(R.string.description_default));
        }
        if (item.getPhotoUrl() != null) {
            if(item.getPhotoUrl().equals(String.valueOf(R.drawable.ic_person_black_24dp))) {
                ImageLoadHelper.loadPictureByDrawableDefault(imageView,R.drawable.ic_person_black_24dp);
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
