package com.example.ruslan.curs2project.model.db_dop_models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CrossingName implements Identified{

    private String id;

    private String bookName;

    private String bookAuthor;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
}
