package com.example.ruslan.curs2project.model.db_dop_models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ElementId implements Identified {

    private String id;

    protected static final String FIELD_ID = "id";

    public ElementId() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Map<String, Object> toMap(String id) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_ID, id);
        return result;
    }
}
