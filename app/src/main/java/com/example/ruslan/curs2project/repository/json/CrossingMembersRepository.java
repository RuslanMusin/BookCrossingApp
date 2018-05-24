package com.example.ruslan.curs2project.repository.json;

import com.example.ruslan.curs2project.model.CommentTwo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CrossingMembersRepository {

    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "crossing_members";
    private final String USER_CROSSINGS = "user_crossings";


    public CrossingMembersRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public void createMember(String userId, String crossingId) {
        /*String key = databaseReference.push().getKey();
        bookCrossing.setId(key);*/
        databaseReference.child(crossingId + "/" + userId).setValue(userId);
        databaseReference.getRoot().child(USER_CROSSINGS).child(userId + "/" + crossingId).setValue(crossingId);
    }

    public DatabaseReference readComment(String bookCrossingId) {
        return databaseReference.child(bookCrossingId);
    }

    public void deleteComment(String bookCrossingId){
        databaseReference.child(bookCrossingId).removeValue();
    }

    public void updateComment(CommentTwo comment){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(comment.getId()).updateChildren(updatedValues);
    }

    public DatabaseReference getComments() {
        return databaseReference.getRoot();
    }
}
