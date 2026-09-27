## GraphQL API

The application exposes a GraphQL API through Spring GraphQL.

GraphQL endpoint:

```text
http://localhost:8080/graphql
```

GraphiQL interface:

```text
http://localhost:8080/graphiql
```

### Queries

#### 1. Health

Returns the connected SurrealDB server version.

```graphql
query {
  health
}
```

#### 2. Alice's books

Returns books liked by Alice.

```graphql
query {
  aliceBooks {
    title
    author
    description
  }
}
```

You can request only the fields you need:

```graphql
query {
  aliceBooks {
    title
    author
  }
}
```

#### 3. Alice's topics

Traverses Alice's `likes` relationship to books and then the books' `about` relationship to topics.

```graphql
query {
  aliceTopics {
    name
  }
}
```

#### 4. Semantic search

Generates an embedding for the query using Ollama and searches the books using vector similarity in SurrealDB.

```graphql
query {
  semanticSearch(q: "something to read about fantasy adventure") {
    title
    author
    description
  }
}
```

#### 5. Recommendations

Performs semantic similarity search and returns the most similar books along with their topics.

```graphql
query {
  recommend(q: "fantasy adventure") {
    book
    author
    similarity
    topics
  }
}
```

### Mutations

#### 6. Create embeddings

Generates embeddings for the demo books and stores them in SurrealDB.

```graphql
mutation {
  createEmbeddings
}
```

#### 7. Cleanup

Removes the existing Alice → Dune `likes` relationship and recreates it with rating and reason metadata.

```graphql
mutation {
  cleanup
}
```

### API overview

| Operation          | Type     | Purpose                                           |
| ------------------ | -------- | ------------------------------------------------- |
| `health`           | Query    | Check the connected SurrealDB version             |
| `aliceBooks`       | Query    | Traverse Alice → likes → books                    |
| `aliceTopics`      | Query    | Traverse Alice → likes → books → about → topics   |
| `semanticSearch`   | Query    | Semantic/vector search over books                 |
| `recommend`        | Query    | Semantic recommendations with topic relationships |
| `createEmbeddings` | Mutation | Generate and store book embeddings                |
| `cleanup`          | Mutation | Update the Alice → Dune graph relationship        |
