package com.demo.surreal_db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.surrealdb.RecordId;

public class Topic {

    @JsonIgnore
    public RecordId id;

    public String name;

    public Topic() {
    }
}