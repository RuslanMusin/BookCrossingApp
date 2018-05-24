package com.example.ruslan.curs2project.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CommentTwo {

    private String id;

    private String author;

    private String content;

    private String photoUrl;

    public CommentTwo() {
    }

    public CommentTwo(String author, String content, String photoUrl) {
        this.author = author;
        this.content = content;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
