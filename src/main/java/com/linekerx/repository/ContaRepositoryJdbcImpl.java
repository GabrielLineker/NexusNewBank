package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigDecimal;
import java.sql.*;

public class ContaRepositoryJdbcImpl implements IContaRepository {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USERNAME");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void salvarConta(Conta conta) {
        // Implementação para salvar a conta no banco de dados usando JDBC
    }

    @Override
    public void removerConta(String cpf) {
        // Implementação para remover a conta do banco de dados usando JDBC
    }

    @Override
    public Conta buscarPorCpf(String cpf) {
        String sql = "SELECT nome, saldo FROM contas WHERE cpf = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    BigDecimal saldo = rs.getBigDecimal("saldo");
                    return new Conta(new Usuario(cpf, nome), saldo);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao acessar o banco de dados", e);
        }
    }

    @Override
    public boolean existeContaParaCpf(String cpf) {
        String sql = "SELECT 1 FROM contas WHERE cpf = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao acessar o banco de dados", e);
        }
    }
}
