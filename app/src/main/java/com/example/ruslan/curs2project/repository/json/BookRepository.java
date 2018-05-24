package com.example.ruslan.curs2project.repository.json;

import com.example.ruslan.curs2project.model.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BookRepository {

    private DatabaseReference databaseReference;

    private final String TABLE_NAME = "books";

    private final String FIELD_AUTHOR = "author";
    private final String FIELD_NAME = "name";
    private final String FIELD_MARK = "mark";
    private final String FIELD_PHOTO_URL = "photoUrl";
    private final String FIELD_DESC = "desc";
    private final String FIELD_COMMENTS = "comments";
    private final String FIELD_CROSSINGS = "crossings";


    public BookRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public Map<String, Object> toMap(Book book) {
        HashMap<String, Object> result = new HashMap<>();

        result.put(FIELD_AUTHOR, book.getAuthors());
        result.put(FIELD_NAME, book.getName());
        result.put(FIELD_MARK, book.getMark());
        result.put(FIELD_PHOTO_URL, book.getPhotoUrl());
        result.put(FIELD_DESC, book.getDesc());

        return result;
    }

    public void createBook(Book book) {
        String key = databaseReference.push().getKey();
        book.setId(key);
        databaseReference.setValue(book);
    }

    public DatabaseReference readBook(String pointId) {
        return databaseReference.child(pointId);
    }

    public void deleteBook(String pointId){
        databaseReference.child(pointId).removeValue();
    }

    public void updateBook(Book book){
        Map<String, Object> updatedValues = toMap(book);
        databaseReference.child(book.getId()).updateChildren(updatedValues);
    }

    public DatabaseReference getBook() {
        return databaseReference;
    }
}
