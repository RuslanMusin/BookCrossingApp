package com.example.ruslan.curs2project.repository.json;

import android.util.Log;

import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.User;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.example.ruslan.curs2project.model.db_dop_models.Identified;
import com.example.ruslan.curs2project.model.db_dop_models.UserRelation;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;

import io.reactivex.Single;

import static com.example.ruslan.curs2project.utils.Const.ADD_FRIEND;
import static com.example.ruslan.curs2project.utils.Const.QUERY_END;
import static com.example.ruslan.curs2project.utils.Const.REMOVE_FRIEND;
import static com.example.ruslan.curs2project.utils.Const.REMOVE_REQUEST;
import static com.example.ruslan.curs2project.utils.Const.SEP;

public class UserRepository {

    private static final String TAG = "UserRepository" ;
    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "users";
    private final String CROSSING_MEMBERS = "crossing_members";
    private final String USER_FRIENDS = "user_friends";
    private final String USER_REQUESTS = "user_requests";


    private final String FIELD_ID = "id";
    private final String FIELD_NAME = "username";
    private final String FIELD_RELATION = "relation";

    public UserRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public void createUser(User user) {
        databaseReference.child(user.getId()).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null) {
                    Log.d(TAG, "database error = " + databaseError.getMessage());
                }
                Log.d(TAG, "completed ");
            }


        });
    }

    public DatabaseReference readUser(String userId) {
        return databaseReference.child(userId);
    }

    public void deleteUser(User user){
        databaseReference.child(user.getId()).removeValue();
    }

    public void updateUser(User user){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(user.getId()).updateChildren(updatedValues);
    }

    public static String getCurrentId(){
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public Single<Query> loadDefaultUsers() {
        return Single.just(databaseReference.limitToFirst(100));
    }

    public Single<Query> loadByCrossing(String crossingId){
        DatabaseReference reference = databaseReference.getRoot().child(CROSSING_MEMBERS).child(crossingId);
        return Single.just(reference);
    }

    public Single<Query> loadReadersByQuery(String query) {
        String queryPart = query.trim();
        Query queryName = databaseReference.orderByChild(FIELD_NAME).startAt(queryPart).endAt(queryPart + QUERY_END).limitToFirst(100);
        return Single.just(queryName);
    }

    public Single<List<Query>> loadFriendsByQuery(String query, String userId) {
        String queryPart = query.trim();
        DatabaseReference reference = databaseReference.getRoot().child(USER_FRIENDS).child(userId);
        return findByReference(reference,queryPart);
    }

    public Single<List<Query>> loadRequestByQuery(String query, String userId) {
        String queryPart = query.trim();
        DatabaseReference reference = databaseReference.getRoot().child(USER_FRIENDS).child(userId);
        return findByReference(reference,queryPart);
    }

    private Single<List<Query>> findByReference(DatabaseReference reference,String queryPart) {
        List<Query> queries = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Identified user = snapshot.getValue(ElementId.class);
                    Query queryDb = databaseReference.orderByChild(FIELD_ID).equalTo(user.getId());
                    queryDb = queryDb
                            .orderByChild(FIELD_NAME)
                            .startAt(queryPart)
                            .endAt(queryPart + QUERY_END);
                    if(queryDb != null){
                        queries.add(queryDb);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Single.just(queries);
    }

    public Single<List<Query>> loadByIds(List<String> crossingsIds) {
        List<Query> queries = new ArrayList<>();
        for(String id : crossingsIds) {
            queries.add(databaseReference.child(id));
        }
        return Single.just(queries);
    }

    public List<Query> loadByComments(List<Comment> comments){
        List<Query> queries = new ArrayList<>();
        for(Comment comment : comments) {
            queries.add(databaseReference.child(comment.getAuthorId()));
        }
        return queries;
    }

    public Single<Query> findFriends(String userId){
        Query reference = databaseReference.getRoot().child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION).equalTo(REMOVE_FRIEND);
        return Single.just(reference);
    }

    public Single<Query> findRequests(String userId){
        Query reference = databaseReference.getRoot().child(USER_FRIENDS).child(userId).orderByChild(FIELD_RELATION).equalTo(REMOVE_REQUEST);
        return Single.just(reference);
    }

    public void addFriend(String userId, String friendId) {
        Map<String, Object> userValues = UserRelation.toMap(userId,REMOVE_FRIEND);
        Map<String, Object> friendValues = UserRelation.toMap(friendId,REMOVE_FRIEND);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(USER_FRIENDS + SEP + userId + SEP + friendId, friendValues);
        childUpdates.put(USER_FRIENDS + SEP + friendId + SEP + userId, userValues);

        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public void removeFriend(String userId, String friendId) {
        Map<String, Object> userValues = UserRelation.toMap(userId,REMOVE_REQUEST);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(USER_FRIENDS + SEP + userId + SEP + friendId, null);
        childUpdates.put(USER_FRIENDS + SEP + friendId + SEP + userId, userValues);

        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public void addFriendRequest(String userId, String friendId) {
        Map<String, Object> friendValues = UserRelation.toMap(friendId,REMOVE_REQUEST);
        Map<String, Object> userValues = UserRelation.toMap(userId,ADD_FRIEND);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(USER_FRIENDS + SEP + userId + SEP + friendId, friendValues);
        childUpdates.put(USER_FRIENDS + SEP + friendId + SEP + userId, userValues);

        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public void removeFriendRequest(String userId, String friendId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(USER_FRIENDS + SEP + userId + SEP + friendId, null);

        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public Query checkType(String userId, String friendId) {
        return databaseReference.getRoot().child(USER_FRIENDS).child(userId).child(friendId);
    }
}
