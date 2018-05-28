package com.example.ruslan.curs2project.repository.json;

import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Point;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;

import static com.example.ruslan.curs2project.utils.Const.SEP;

public class PointRepository {

    private DatabaseReference databaseReference;

    public final String TABLE_NAME = "points";

    private final String FIELD_ID = "id";
    private final String FIELD_DESC = "desc";
    private final String FIELD_LONG = "longitude";
    private final String FIELD_LAT = "latitude";
    private final String FIELD_PHOTO_URL = "photoUrl";
    private final String FIELD_TIME = "date";
    private final String FIELD_EDITOR = "editorId";

    public PointRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public Map<String, Object> toMap(Point point) {
        HashMap<String, Object> result = new HashMap<>();

        result.put(FIELD_ID,point.getId());
        result.put(FIELD_DESC, point.getDesc());
        result.put(FIELD_LAT, point.getLatitude());
        result.put(FIELD_LONG, point.getLongitude());
        result.put(FIELD_PHOTO_URL, point.getPhotoUrl());
        result.put(FIELD_TIME, point.getDate());
        result.put(FIELD_EDITOR, point.getEditorId());


        return result;
    }

    public String getKey(String crossingId){
        return databaseReference.child(crossingId).push().getKey();
    }

    public void createPoint(BookCrossing crossing, Point point) {
        String pointKey = getKey(crossing.getId());
        Map<String, Object> pointValues = toMap(point);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(TABLE_NAME + SEP + crossing.getId() + SEP + pointKey, pointValues);
        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public DatabaseReference readPoint(String pointId) {
        return databaseReference.child(pointId);
    }

    public void deletePoint(String pointId){
        databaseReference.child(pointId).removeValue();
    }

    public void updateUser(Point point){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(point.getId()).updateChildren(updatedValues);
    }

    public DatabaseReference getPoints() {
        return databaseReference.getRoot();
    }

    public Single<Query> loadPoints(String crossingId){
        return Single.just(databaseReference.child(crossingId));

    }

    public Single<Query> findPoint(String crossingId,String userId){
        DatabaseReference reference = databaseReference.child(crossingId);
        Query query = reference.orderByChild(FIELD_EDITOR).equalTo(userId);
        return Single.just(query);

    }
}
