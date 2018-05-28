package com.example.ruslan.curs2project.ui.book.book_item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ruslan.curs2project.R;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.ui.widget.ExpandableTextView;
import com.example.ruslan.curs2project.utils.FormatterUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CommentItemHolder extends RecyclerView.ViewHolder {

    private static final int MAX_LENGTH = 80;
    private static final String MORE_TEXT = "...";

    private final ImageView avatarImageView;
    private final ExpandableTextView commentTextView;
    private final TextView dateTextView;
    private final ImageView replyView;

    private OnCommentClickListener commentClickListener;

    @NonNull
    public static CommentItemHolder create(@NonNull Context context, OnCommentClickListener commentClickListener) {
        View view = View.inflate(context, R.layout.comment_list_item, null);
        CommentItemHolder holder = new CommentItemHolder(view);
        holder.commentClickListener = commentClickListener;
        return holder;
    }

    public CommentItemHolder(View itemView) {
        super(itemView);

        avatarImageView = (ImageView) itemView.findViewById(R.id.avatarImageView);
        commentTextView = (ExpandableTextView) itemView.findViewById(R.id.commentText);
        dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
        replyView = itemView.findViewById(R.id.iv_reply);
    }

    public void bind(@NonNull Comment comment) {
        final String authorId = comment.getAuthorId();
        commentTextView.setText(comment.getText());

        CharSequence date = FormatterUtil.getRelativeTimeSpanString(avatarImageView.getContext(), comment.getCreatedDate());
        dateTextView.setText(date);

        replyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onReplyClick(getAdapterPosition());
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onAuthorClick(authorId);
            }
        });

        fillComment(comment, commentTextView);

    }

    private void fillComment(Comment comment, ExpandableTextView commentTextView) {
        Spannable contentString = new SpannableStringBuilder(comment.getAuthorName()
                + "   " + comment.getText());
        contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(commentTextView.getContext(), R.color.highlight_text)),
                0, comment.getAuthorName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        commentTextView.setText(contentString);

        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(comment.getAuthorPhotoUrl());

        Glide.with(commentTextView.getContext())
                .load(imageReference)
                .into(avatarImageView);
    }

    private String cutLongDescription(String description) {
        if (description.length() < MAX_LENGTH) {
            return description;
        } else {
            return description.substring(0, MAX_LENGTH - MORE_TEXT.length()) + MORE_TEXT;
        }
    }

    private void fillComment(String userName, String comment, ExpandableTextView commentTextView) {
        Spannable contentString = new SpannableStringBuilder(userName + "   " + comment);
        contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(commentTextView.getContext(), R.color.highlight_text)),
                0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        commentTextView.setText(contentString);
    }
}
