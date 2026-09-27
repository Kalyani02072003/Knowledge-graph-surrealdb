## Knowledge Graphs vs Relational Databases

A common question is:

> **Can't we represent the same relationships using a relational database with primary and foreign keys?**

**Yes, we can.**

A relational database can absolutely represent a knowledge graph.

For example, consider:

```text
Alice → reads → The Hobbit
The Hobbit → written_by → J.R.R. Tolkien
```

In a relational database, this could be represented using tables such as:

```text
users
+----+-------+
| id | name  |
+----+-------+
| 1  | Alice |
+----+-------+

books
+----+------------+
| id | title      |
+----+------------+
| 10 | The Hobbit |
+----+------------+

authors
+----+------------------+
| id | name             |
+----+------------------+
| 50 | J.R.R. Tolkien    |
+----+------------------+
```

And relationship tables:

```text
user_books
+---------+---------+
| user_id | book_id |
+---------+---------+
| 1       | 10      |
+---------+---------+

book_authors
+---------+----------+
| book_id | author_id|
+---------+----------+
| 10      | 50       |
+---------+----------+
```

The resulting relationship is:

```text
Alice
  ↓
user_books
  ↓
The Hobbit
  ↓
book_authors
  ↓
J.R.R. Tolkien
```

So the difference is **not that relational databases cannot represent relationships**.

### The difference is how relationships are modeled and traversed

In a relational database, relationships are generally represented through foreign keys and join tables.

For example, finding information several relationships away may require multiple joins:

```sql
SELECT ...
FROM users u
JOIN friendships f ON ...
JOIN users u2 ON ...
JOIN employment e ON ...
JOIN companies c ON ...
JOIN books b ON ...
JOIN book_topics bt ON ...
JOIN topics t ON ...
WHERE u.name = 'Alice';
```

Relational databases can even perform recursive graph-like traversal using recursive CTEs:

```sql
WITH RECURSIVE network AS (
    SELECT friend_id, 1 AS depth
    FROM friendships
    WHERE user_id = 1

    UNION ALL

    SELECT f.friend_id, n.depth + 1
    FROM friendships f
    JOIN network n
      ON f.user_id = n.friend_id
    WHERE n.depth < 5
)
SELECT DISTINCT friend_id
FROM network;
```

This works, but the graph traversal is being expressed through relational operations.

---

## Graph-oriented databases

A graph database makes the relationship itself a first-class part of the data model.

Instead of thinking primarily in terms of tables and joins:

```text
Table → Foreign Key → Table → Foreign Key → Table
```

we can model the data directly as:

```text
Alice
  │
  │ reads
  ▼
The Hobbit
  │
  │ written_by
  ▼
J.R.R. Tolkien
```

The basic structure becomes:

```text
Node → Relationship → Node
```

Relationships can also contain their own properties:

```text
Alice
  │
  │ KNOWS
  │ since: 2018
  │ confidence: 0.92
  ▼
Bob
```

This relationship-centric model is particularly useful for highly connected domains such as:

* Social networks
* Recommendation systems
* Fraud detection
* Knowledge graphs
* Dependency graphs
* Network topology
* Entity relationships

---

## Where SurrealDB fits

SurrealDB supports graph relationships directly.

For example:

```sql
CREATE user:alice SET name = "Alice";
CREATE book:hobbit SET title = "The Hobbit";

RELATE user:alice -> reads -> book:hobbit;
```

This creates:

```text
Alice
  │
  │ reads
  ▼
The Hobbit
```

We can then add another relationship:

```sql
CREATE author:tolkien SET name = "J.R.R. Tolkien";

RELATE book:hobbit -> written_by -> author:tolkien;
```

Now the graph becomes:

```text
Alice
  │
  │ reads
  ▼
The Hobbit
  │
  │ written_by
  ▼
J.R.R. Tolkien
```

This can be extended further:

```text
Alice
 │
 ├── reads ───────→ Book
 │                    │
 │                    └── written_by → Author
 │
 └── interested_in → Topic
                       ▲
                       │
                     about
                       │
                      Book
```

This is the basis of a **knowledge graph**.

---

## Knowledge Graph + AI

The real advantage becomes more interesting when a knowledge graph is combined with vector embeddings.

A vector database or vector search system can answer:

> "Which books are semantically related to distributed systems?"

while the knowledge graph can answer:

> "Which books has Alice read?"

Combining both allows queries such as:

> **"Which books has Alice read that are semantically related to distributed systems?"**

Conceptually:

```text
                    Semantic Search
                         │
                         ▼
                Relevant Books
                         │
                         ▼
Alice ────── reads ────→ Book
```

Here:

* **Vector search** provides semantic similarity.
* **Graph traversal** provides explicit relationships.
* **Knowledge graphs** provide structured context between entities.

This combination is particularly useful in AI applications such as **GraphRAG**, where graph relationships can be incorporated into the retrieval process before information is passed to an LLM.

### Key takeaway

[Session PPT](https://docs.google.com/presentation/d/1W5zR_Nzd-hM6NBrAgVwM7Q-fkMNlpcTG/edit?usp=sharing&ouid=117470378100335116173&rtpof=true&sd=true)

> **A knowledge graph does not require a graph database. A relational database can represent a knowledge graph using tables, foreign keys, and join tables. Graph databases such as SurrealDB make relationships and graph traversal a first-class part of the data model, which can make highly connected, relationship-centric workloads more natural to model and query.**
