package com.example.ruslan.curs2project.model.db_dop_models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserRelation extends ElementId {

    private String relation;

    private static final String FIELD_RELATION = "relation";

    public UserRelation() {
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public static Map<String, Object> toMap(String id, String relation) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_ID, id);
        result.put(FIELD_RELATION, relation);
        return result;
    }
}
