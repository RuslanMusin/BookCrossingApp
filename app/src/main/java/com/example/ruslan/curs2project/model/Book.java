package com.example.ruslan.curs2project.model;


import android.os.Parcel;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Book {

    private String id;

    private String name;

    private List<String> authors;

    private String photoUrl;

    private Integer mark = 0;

    private String desc;

    @Exclude
    transient private List<CommentTwo> comments;

    @Exclude
    transient private List<BookCrossing> crossings;

    public Book() {
    }

    public Book(String name, List<String> authors, String photoUrl, Integer mark, List<CommentTwo> comments, List<BookCrossing> crossings) {
        this.name = name;
        this.authors = authors;
        this.photoUrl = photoUrl;
        this.mark = mark;
        this.comments = comments;
        this.crossings = crossings;
    }

    protected Book(Parcel in) {
        id = in.readString();
        name = in.readString();
        authors = in.createStringArrayList();
        photoUrl = in.readString();
        if (in.readByte() == 0) {
            mark = null;
        } else {
            mark = in.readInt();
        }
        desc = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<CommentTwo> getComments() {
        return comments;
    }

    public void setComments(List<CommentTwo> comments) {
        this.comments = comments;
    }

    public List<BookCrossing> getCrossings() {
        return crossings;
    }

    public void setCrossings(List<BookCrossing> crossings) {
        this.crossings = crossings;
    }
}
