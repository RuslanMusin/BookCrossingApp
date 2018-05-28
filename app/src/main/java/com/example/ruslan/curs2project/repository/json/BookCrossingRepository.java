package com.example.ruslan.curs2project.repository.json;

import android.util.Log;

import com.example.ruslan.curs2project.model.Book;
import com.example.ruslan.curs2project.model.BookCrossing;
import com.example.ruslan.curs2project.model.Comment;
import com.example.ruslan.curs2project.model.Point;
import com.example.ruslan.curs2project.model.db_dop_models.CrossingName;
import com.example.ruslan.curs2project.model.db_dop_models.ElementId;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

import static com.example.ruslan.curs2project.utils.Const.QUERY_END;
import static com.example.ruslan.curs2project.utils.Const.SEP;
import static com.example.ruslan.curs2project.utils.Const.TAG_LOG;

public class BookCrossingRepository {

    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "crossings";
    private final String USER_CROSSINGS = "user_crossings";
    private final String BOOK_CROSSINGS = "book_crossings";
    private final String CROSSINGS_QUERY = "crossings_query";
    private final String CROSSING_MEMBERS = "crossing_members";


    private final String FIELD_ID = "id";
    private final String FIELD_TITLE = "name";
    private final String FIELD_DESC = "description";
    private final String FIELD_KEY = "keyPhrase";
    private final String FIELD_DATE = "date";
    private final String FIELD_BOOK_ID = "bookId";
    private final String FIELD_BOOK_NAME = "bookName";
    private final String FIELD_BOOK_PHOTO = "bookPhoto";
    private final String FIELD_BOOK_AUTHOR = "bookAuthor";
    private final String FIELD_LAST_POINT = "lastPointId";

    PointRepository pointRepository;

    public BookCrossingRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public Map<String, Object> toMap(BookCrossing crossing) {
        HashMap<String, Object> result = new HashMap<>();

        result.put(FIELD_ID,crossing.getId());
        result.put(FIELD_DESC, crossing.getDescription());
        result.put(FIELD_TITLE, crossing.getName());
        result.put(FIELD_BOOK_ID, crossing.getBookId());
        result.put(FIELD_BOOK_NAME, crossing.getBookName());
        result.put(FIELD_BOOK_PHOTO, crossing.getBookPhoto());
        result.put(FIELD_BOOK_AUTHOR, crossing.getBookAuthor());
        result.put(FIELD_KEY, crossing.getKeyPhrase());
        result.put(FIELD_DATE, crossing.getDate());
        result.put(FIELD_LAST_POINT,crossing.getLastPointId());

        return result;
    }


    public Map<String, Object> toMap(String id) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_ID, id);
        return result;
    }

    public Map<String, Object> toMap(CrossingName crossingName) {
        HashMap<String, Object> result = new HashMap<>();

        result.put(FIELD_ID, crossingName.getId());
        result.put(FIELD_BOOK_NAME, crossingName.getBookName());
        result.put(FIELD_BOOK_AUTHOR, crossingName.getBookAuthor());

        return result;
    }

    public void createCrossing(BookCrossing bookCrossing, String userId) {
        String crossingKey = databaseReference.push().getKey();
        bookCrossing.setId(crossingKey);

        Point point = bookCrossing.getPoints().get(0);
        pointRepository = new PointRepository();
        String pointKey = pointRepository.getKey(crossingKey);
        point.setId(pointKey);

        bookCrossing.setLastPointId(point.getId());

        ElementId elementId = new ElementId();
        elementId.setId(bookCrossing.getId());

        CrossingName crossingName = new CrossingName();
        crossingName.setId(crossingKey);
        crossingName.setBookName(bookCrossing.getBookName());
        crossingName.setBookAuthor(bookCrossing.getBookAuthor());

        Map<String, Object> pointValues = pointRepository.toMap(point);
        Map<String, Object> crossingValues = toMap(bookCrossing);
        Map<String, Object> crossingIdValues = toMap(crossingKey);
        Map<String, Object> crossingNameValues = toMap(crossingName);
        Map<String, Object> followerValues = toMap(userId);

        Log.d(TAG_LOG,"id = " + bookCrossing.getId());
        Log.d(TAG_LOG,"name = " + crossingName.getBookName());
        Log.d(TAG_LOG,"authorName = " + crossingName.getBookAuthor());
        Log.d(TAG_LOG,"userId = " + userId);


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(TABLE_NAME + "/" + crossingKey, crossingValues);
        childUpdates.put(pointRepository.TABLE_NAME + "/" + crossingKey + "/" + pointKey, pointValues);
        childUpdates.put(BOOK_CROSSINGS + SEP + bookCrossing.getBookId() + SEP + crossingKey,crossingIdValues);
        childUpdates.put(CROSSINGS_QUERY + SEP + bookCrossing.getId(),crossingNameValues);
        childUpdates.put(USER_CROSSINGS + SEP + userId + SEP + crossingKey,crossingIdValues);
        childUpdates.put(CROSSING_MEMBERS + SEP + bookCrossing.getId() + SEP + userId,followerValues);

        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public DatabaseReference readCrossing(String pointId) {
        return databaseReference.child(pointId);
    }

    public void deleteCrossing(String pointId){
        databaseReference.child(pointId).removeValue();
    }

    public void updateCrossing(Comment comment){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(comment.getId()).updateChildren(updatedValues);
    }

    public Single<Query> loadDefaultCrossings() {
        return Single.just(databaseReference.limitToFirst(100));
    }

    public Single<Query> loadByUser(String userId) {
        return Single.just(databaseReference.getRoot().child(USER_CROSSINGS).child(userId));
    }

    public Single<Map<String,Query>> loadByBook(Book book) {
        Map<String, Query> queryMap = new HashMap<>();
        DatabaseReference reference = databaseReference.getRoot().child(BOOK_CROSSINGS);
        Query queryId =  reference.child(book.getId());

        reference = databaseReference.getRoot().child(CROSSINGS_QUERY);
        Query queryName;
        List<String> authors = book.getAuthors();
        String bookName = book.getName().trim();
        if(authors.size() > 1) {
            queryName =  reference.orderByChild(FIELD_BOOK_NAME).startAt(bookName).endAt(bookName + QUERY_END);
        } else {
            String[] parts = authors.get(0).trim().split("\\s+");
            String lastPart = parts[parts.length-1];
            queryName = reference.orderByChild(FIELD_BOOK_NAME).startAt(bookName).endAt(bookName + QUERY_END);
//            queryName = queryName.orderByChild(FIELD_BOOK_AUTHOR).endAt(lastPart);
        }
        Log.d(TAG_LOG, "query is null ? " + (queryName == null));
        Log.d(TAG_LOG, "queryId is null ? " + (queryId == null));

        queryMap.put("queryId",queryId);
        queryMap.put("queryName",queryName);
        return Single.just(queryMap);
    }

    public Single<Query> loadByQuery(String query) {
        Log.d(TAG_LOG, "load cross by query = " + query);
        String queryPart = query.trim();
        DatabaseReference reference = databaseReference.getRoot().child(CROSSINGS_QUERY);
        Query queryName = reference.orderByChild(FIELD_BOOK_NAME).startAt(queryPart).endAt(queryPart + QUERY_END);
        Log.d(TAG_LOG, "query is null ? " + (queryName == null));
        return Single.just(queryName);
    }

    public Single<List<Query>> loadByIds(List<String> crossingsIds) {
        List<Query> queries = new ArrayList<>();
        for(String id : crossingsIds) {
            queries.add(databaseReference.child(id));
        }
        return Single.just(queries);

    }

    public Single<Query> findFollowers(String crossingId) {
        DatabaseReference reference = databaseReference.getRoot().child(CROSSING_MEMBERS).child(crossingId);
        return Single.just(reference);
    }

    public void addFollower(BookCrossing crossing, String userId) {
        Map<String, Object> followerValues = toMap(userId);
        Map<String,Object> crossingValues = toMap(crossing.getId());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(CROSSING_MEMBERS + SEP + crossing.getId() + SEP + userId,followerValues);
        childUpdates.put(USER_CROSSINGS + SEP + userId + SEP + crossing.getId(),crossingValues);

        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public void removeFollower(BookCrossing crossing, String userId) {
        DatabaseReference reference = databaseReference.getRoot().child(CROSSING_MEMBERS).child(crossing.getId()).child(userId);
        reference.removeValue();
    }
}
