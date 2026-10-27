package com.linekerx.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class ContaRepositoryJdbcImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("minibanco_test")
            .withUsername("test")
            .withPassword("test");

    @Test
    @DisplayName("Deve iniciar o banco de dados no Docker com sucesso")
    void deveIniciarOBancoComSucesso() {
        assertTrue(postgres.isRunning());
        System.out.println("URL JDBC do Banco Temporário: " + postgres.getJdbcUrl());
    }
}
