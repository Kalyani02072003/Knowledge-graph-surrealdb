package com.demo.surreal_db.model;

import java.util.List;

public class Recommendation {

    public String book;
    public String author;
    public Double similarity;
    public List<String> topics;

    public Recommendation() {
    }

    public Recommendation(
            String book,
            String author,
            Double similarity,
            List<String> topics
    ) {
        this.book = book;
        this.author = author;
        this.similarity = similarity;
        this.topics = topics;
    }
}