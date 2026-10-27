package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
public class ContaRepositoryJdbcImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("minibanco_test")
            .withUsername("test")
            .withPassword("test");

    private ContaRepositoryJdbcImpl repository;

    @BeforeEach
    void setUp() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS contas (" +
                    "cpf VARCHAR(11) PRIMARY KEY," +
                    "nome VARCHAR(100) NOT NULL," +
                    "saldo DECIMAL(15, 2) NOT NULL" +
                    ")");
            st.execute("TRUNCATE TABLE contas");
            repository = new ContaRepositoryJdbcImpl(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar a tabela de contas", e);
        }
    }

    @Test
    @DisplayName("Deve salvar uma conta na base de dados real com sucesso")
    void deveSalvarContaComSucesso() {
        Conta novaConta = new Conta(new Usuario("Carlos", "98765432100"), new BigDecimal("250.00"));

        repository.salvarConta(novaConta);

        Conta contaSalva = repository.buscarPorCpf("98765432100");

        assertNotNull(contaSalva);
        assertEquals("Carlos", contaSalva.getUsuario().nome());
        assertEquals("98765432100", contaSalva.getUsuario().cpf());
        assertEquals(new BigDecimal("250.00"), contaSalva.getSaldo());
    }
}