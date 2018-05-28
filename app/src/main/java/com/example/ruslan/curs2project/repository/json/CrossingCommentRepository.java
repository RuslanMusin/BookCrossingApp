package com.example.ruslan.curs2project.repository.json;

import android.util.Log;

import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.repository.RepositoryProvider;
import com.example.ruslan.curs2project.ui.book.book_item.OnCommentClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class CrossingCommentRepository {

    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "crossing_comments";

    public CrossingCommentRepository(String bookId) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME).child(bookId);
    }

    public DatabaseReference readComment(String pointId) {
        return databaseReference.child(pointId);
    }

    public void deleteComment(String pointId){
        databaseReference.child(pointId).removeValue();
    }

    public void updateComment(Comment comment){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(comment.getId()).updateChildren(updatedValues);
    }

    public DatabaseReference getComments() {
        return databaseReference;
    }

    public void getComments(OnCommentClickListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    comments.add(postSnapshot.getValue(Comment.class));
                }
                fillUserData(listener,comments);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LOG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void fillUserData(OnCommentClickListener listener, List<Comment> comments) {
        List<Query> queries = RepositoryProvider.getUserRepository().loadByComments(comments);

        List<User> users = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User crossing = dataSnapshot.getValue(User.class);
                users.add(crossing);
                Comment comment = comments.get(users.size() - 1);
                comment.setAuthorName(crossing.getUsername());
                comment.setAuthorPhotoUrl(crossing.getPhotoUrl());

                if (users.size() == queries.size()) {
                    listener.setComments(comments);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LOG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        for (Query query : queries) {
            query.addListenerForSingleValueEvent(valueEventListener);
        }

    }

    public void createComment(Comment comment, OnCommentClickListener listener) {
        String key = databaseReference.push().getKey();
        comment.setId(key);
        databaseReference.child(key).setValue(comment);
        listener.addComment(comment);
    }
}
