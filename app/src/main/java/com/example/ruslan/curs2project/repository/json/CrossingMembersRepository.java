package com.example.ruslan.curs2project.repository.json;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CrossingMembersRepository {

    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "crossing_members";
    private final String USER_CROSSINGS = "user_crossings";

    public CrossingMembersRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public void createMember(String userId, String crossingId) {
        databaseReference.child(crossingId + "/" + userId).setValue(userId);
        databaseReference.getRoot().child(USER_CROSSINGS).child(userId + "/" + crossingId).setValue(crossingId);
    }
}
