package com.demo.surreal_db.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Double> embed(String text) {

        try {

            String requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", "nomic-embed-text",
                            "input", text
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/embed"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama returned HTTP " + response.statusCode()
                                + ": " + response.body()
                );
            }

            JsonNode json = objectMapper.readTree(response.body());

            JsonNode embedding = json
                    .get("embeddings")
                    .get(0);

            return objectMapper.convertValue(
                    embedding,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, Double.class)
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
}