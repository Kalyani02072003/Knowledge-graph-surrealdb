package com.demo.surreal_db.config;

import com.surrealdb.Surreal;
import com.surrealdb.signin.RootCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SurrealDbConfig {

    @Bean
    public Surreal surrealDb(
            @Value("${surrealdb.url}") String url,
            @Value("${surrealdb.namespace}") String namespace,
            @Value("${surrealdb.database}") String database,
            @Value("${surrealdb.username}") String username,
            @Value("${surrealdb.password}") String password
    ) {
        Surreal db = new Surreal();

        db.connect(url);

        db.useNs(namespace);
        db.useDb(database);

        db.signin(
                new RootCredential(username, password)
        );

        return db;
    }
}
