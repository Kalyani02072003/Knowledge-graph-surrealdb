package com.demo.surreal_db.service;

import com.demo.surreal_db.model.Book;
import com.demo.surreal_db.model.Recommendation;
import com.demo.surreal_db.model.Topic;
import com.surrealdb.Array;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;

@Service
public class SurrealDbService {

    private final Surreal db;

    private final OllamaService ollamaService;

    public SurrealDbService(
            Surreal db,
            OllamaService ollamaService
    ) {
        this.db = db;
        this.ollamaService = ollamaService;
    }

    public String version() {
        return db.version().toString();
    }

    public List<Book> getAliceBooks() {

        Response response = db.query(
                "SELECT VALUE ->likes->book.* FROM user:alice;"
        );

        Array outerArray = response.take(0).getArray();
        Array booksArray = outerArray.get(0).getArray();

        List<Book> books = new ArrayList<>();

        var iterator = booksArray.iterator(Book.class);

        while (iterator.hasNext()) {
            books.add(iterator.next());
        }

        return books;
    }

    public List<Topic> getAliceTopics() {

        Response response = db.query(
                "SELECT VALUE ->likes->book->about->topic.* FROM user:alice;"
        );

        Array outerArray = response.take(0).getArray();

        Array topicsArray = outerArray.get(0).getArray();

        List<Topic> topics = new ArrayList<>();

        var iterator = topicsArray.iterator(Topic.class);

        while (iterator.hasNext()) {
            topics.add(iterator.next());
        }

        return topics;
    }

    public String createEmbeddings() {

        List<Double> hobbitEmbedding = ollamaService.embed(
                "A fantasy adventure about Bilbo Baggins, " +
                        "a hobbit who leaves his quiet home and joins " +
                        "a dangerous journey filled with exploration, " +
                        "friendship, treasure, and mythical creatures."
        );

        List<Double> duneEmbedding = ollamaService.embed(
                "A science fiction epic about Paul Atreides, " +
                        "politics, desert worlds, power, survival, " +
                        "prophecy, and an ancient struggle between " +
                        "powerful families."
        );

        String hobbitVector = hobbitEmbedding.toString();
        String duneVector = duneEmbedding.toString();

        db.query(
                "UPDATE book:the_hobbit SET " +
                        "description = 'A fantasy adventure about Bilbo Baggins, " +
                        "a hobbit who leaves his quiet home and joins a dangerous " +
                        "journey filled with exploration, friendship, treasure, " +
                        "and mythical creatures.', " +
                        "embedding = " + hobbitVector + ";"
        );

        db.query(
                "UPDATE book:dune SET " +
                        "description = 'A science fiction epic about Paul Atreides, " +
                        "politics, desert worlds, power, survival, prophecy, " +
                        "and an ancient struggle between powerful families.', " +
                        "embedding = " + duneVector + ";"
        );

        return "Embeddings created. Dimension = "
                + hobbitEmbedding.size();
    }

    public List<Book> semanticSearch(String query) {

        List<Double> queryEmbedding =
                ollamaService.embed(query);

        String vector = queryEmbedding.toString();

        Response response = db.query(
                "SELECT " +
                        "id, " +
                        "title, " +
                        "author, " +
                        "description, " +
                        "vector::similarity::cosine(embedding, " +
                        vector +
                        ") AS similarity, " +
                        "->about->topic.name AS topics " +
                        "FROM book " +
                        "ORDER BY similarity DESC;"
        );

        Array resultsArray = response.take(0).getArray();

        List<Book> books = new ArrayList<>();

        var iterator = resultsArray.iterator(Book.class);

        while (iterator.hasNext()) {
            books.add(iterator.next());
        }

        return books;
    }

    public String cleanDuplicateLikes() {

        db.query(
                "DELETE likes WHERE in = user:alice AND out = book:dune;"
        );

        db.query(
                "RELATE user:alice->likes->book:dune " +
                        "SET rating = 5, reason = 'Loved the world building';"
        );

        return "Dune relationship cleaned";
    }

    public List<Recommendation> recommend(String query) {

        List<Double> queryEmbedding =
                ollamaService.embed(query);

        String vector = queryEmbedding.toString();

        Response response = db.query(
                "SELECT " +
                        "title AS book, " +
                        "author, " +
                        "vector::similarity::cosine(embedding, " +
                        vector +
                        ") AS similarity, " +
                        "->about->topic.name AS topics " +
                        "FROM book " +
                        "WHERE embedding != NONE " +
                        "ORDER BY similarity DESC " +
                        "LIMIT 3;"
        );

        Array resultsArray = response.take(0).getArray();

        List<Recommendation> results = new ArrayList<>();

        var iterator = resultsArray.iterator(Recommendation.class);

        while (iterator.hasNext()) {
            results.add(iterator.next());
        }

        return results;
    }


}