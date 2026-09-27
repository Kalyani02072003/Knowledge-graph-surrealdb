package com.demo.surreal_db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.surrealdb.RecordId;

public class Book {

    @JsonIgnore
    public RecordId id;

    public String title;
    public String author;
    public String description;

    public Book() {
    }
}