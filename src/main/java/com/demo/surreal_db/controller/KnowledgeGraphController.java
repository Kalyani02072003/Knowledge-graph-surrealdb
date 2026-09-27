package com.demo.surreal_db.controller;

import com.demo.surreal_db.model.Book;
import com.demo.surreal_db.model.Recommendation;
import com.demo.surreal_db.model.Topic;
import com.demo.surreal_db.service.SurrealDbService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class KnowledgeGraphController {

    private final SurrealDbService surrealDbService;

    public KnowledgeGraphController(
            SurrealDbService surrealDbService
    ) {
        this.surrealDbService = surrealDbService;
    }

    @QueryMapping
    public String health() {
        return surrealDbService.version();
    }

    @QueryMapping
    public List<Book> aliceBooks() {
        return surrealDbService.getAliceBooks();
    }

    @QueryMapping
    public List<Topic> aliceTopics() {
        return surrealDbService.getAliceTopics();
    }

    @QueryMapping
    public List<Book> semanticSearch(
            @Argument String q
    ) {
        return surrealDbService.semanticSearch(q);
    }

    @QueryMapping
    public List<Recommendation> recommend(
            @Argument String q
    ) {
        return surrealDbService.recommend(q);
    }

    @MutationMapping
    public String createEmbeddings() {
        return surrealDbService.createEmbeddings();
    }

    @MutationMapping
    public String cleanup() {
        return surrealDbService.cleanDuplicateLikes();
    }
}