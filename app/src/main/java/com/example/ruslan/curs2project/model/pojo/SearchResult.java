package com.example.ruslan.curs2project.model.pojo;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class SearchResult {

    private static final String TAG = "TAG" ;

    @SerializedName("items")
    private List<GBook> listGBooks;

    private String className;

    public List<GBook> getListGBooks() {
        Log.d(TAG,"books = " + listGBooks.size());
        return listGBooks;
    }

    public void setListGBooks(List<GBook> listGBooks) {
        this.listGBooks = listGBooks;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
