package com.example.ruslan.curs2project.repository.json;

import com.example.ruslan.curs2project.model.CommentTwo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;

public class BookCommentRepository {

    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "book_comments";

    public BookCommentRepository(String bookId) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME).child(bookId);
    }

    public void createComment(CommentTwo comment) {
        String key = databaseReference.push().getKey();
        comment.setId(key);
        databaseReference.setValue(comment);
    }

    public DatabaseReference readComment(String pointId) {
        return databaseReference.child(pointId);
    }

    public void deleteComment(String pointId){
        databaseReference.child(pointId).removeValue();
    }

    public void updateComment(CommentTwo comment){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(comment.getId()).updateChildren(updatedValues);
    }

    public Single<Query> getComments(Integer page, Integer size, String sort) {
        return Single.just(databaseReference.getRoot().startAt(page).limitToFirst(size).orderByChild(sort));
    }
}
