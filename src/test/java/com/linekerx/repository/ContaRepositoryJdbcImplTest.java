package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.ContaInexistenteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ContaRepositoryJdbcImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("minibanco_test")
            .withUsername("test")
            .withPassword("test");

    private ContaRepositoryJdbcImpl repository;

    @BeforeEach
    void setUp() {
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS contas (" +
                    "cpf VARCHAR(11) PRIMARY KEY," +
                    "nome VARCHAR(100) NOT NULL," +
                    "saldo DECIMAL(15, 2) NOT NULL" +
                    ")");
            st.execute("TRUNCATE TABLE contas");
            repository = new ContaRepositoryJdbcImpl(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao preparar a tabela de contas", e);
        }
    }

    @Nested
    @DisplayName("Salvar Conta")
    class SalvarConta {

        @Test
        void deveSalvarContaComSucesso() {
            Conta novaConta = new Conta(new Usuario("Carlos", "98765432100"), new BigDecimal("250.00"));
            repository.salvarConta(novaConta);
            Conta contaSalva = repository.buscarPorCpf("98765432100");

            assertNotNull(contaSalva);
            assertEquals("Carlos", contaSalva.getUsuario().nome());
            assertEquals("98765432100", contaSalva.getUsuario().cpf());
            assertEquals(new BigDecimal("250.00"), contaSalva.getSaldo());
        }

        @Test
        void deveAtualizarSaldoDeContaExistente() {
            Conta novaConta = new Conta(new Usuario("Carlos", "98765432100"), new BigDecimal("250.00"));
            repository.salvarConta(novaConta);

            novaConta.depositar(new BigDecimal("100.00"));
            repository.salvarConta(novaConta);

            Conta contaAtualizada = repository.buscarPorCpf("98765432100");

            assertNotNull(contaAtualizada);
            assertEquals(new BigDecimal("350.00"), contaAtualizada.getSaldo());
        }

    }

    @Nested
    @DisplayName("Remover Conta")
    class RemoverConta {

        @Test
        void deveRemoverContaComSucesso() {
            Conta novaConta = new Conta(new Usuario("Carlos", "98765432100"), new BigDecimal("250.00"));
            repository.salvarConta(novaConta);

            repository.removerConta("98765432100");

            assertFalse(repository.existeContaParaCpf("98765432100"));
        }

        @Test
        void deveLancarExcecaoAoRemoverContaInexistente() {
            ContaInexistenteException e = assertThrows(ContaInexistenteException.class, () -> {
                repository.removerConta("00000000000");
            });
            assertEquals("A conta 00000000000 não está cadastrada no sistema.", e.getMessage());
        }

    }

    @Nested
    @DisplayName("Buscar Conta")
    class BuscarConta {

        @Test
        void deveBuscarContaComSucesso() {
            Conta novaConta = new Conta(new Usuario("Carlos", "98765432100"), new BigDecimal("250.00"));
            repository.salvarConta(novaConta);

            Conta contaBuscada = repository.buscarPorCpf("98765432100");

            assertNotNull(contaBuscada);
            assertEquals("Carlos", contaBuscada.getUsuario().nome());
            assertEquals("98765432100", contaBuscada.getUsuario().cpf());
            assertEquals(new BigDecimal("250.00"), contaBuscada.getSaldo());
        }

        @Test
        void deveLancarExcecaoAoBuscarContaInexistente() {
            ContaInexistenteException e = assertThrows(ContaInexistenteException.class, () -> {
                repository.buscarPorCpf("00000000000");
            });
            assertEquals("A conta 00000000000 não está cadastrada no sistema.", e.getMessage());
        }
    }

    @Nested
    @DisplayName("Verificar Existência de Conta")
    class VerificarExistenciaConta {

        @Test
        void deveRetornarTrueSeContaExiste() {
            Conta novaConta = new Conta(new Usuario("Carlos", "98765432100"), new BigDecimal("250.00"));
            repository.salvarConta(novaConta);
            assertTrue(repository.existeContaParaCpf("98765432100"));
        }

        @Test
        void deveRetornarFalseSeContaNaoExiste() {
            assertFalse(repository.existeContaParaCpf("00000000000"));
        }
    }
}