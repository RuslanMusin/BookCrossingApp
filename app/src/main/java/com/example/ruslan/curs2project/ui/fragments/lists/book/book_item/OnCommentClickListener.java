package com.example.ruslan.curs2project.ui.fragments.lists.book.book_item;

import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.ui.base.BaseAdapter;

import java.util.List;

public interface OnCommentClickListener extends BaseAdapter.OnItemClickListener<Comment> {

    void onReplyClick(int position);

    void onAuthorClick(String position);

    void setComments(List<Comment> comments);

    void addComment(Comment comment);
}
