package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.ContaInexistenteException;
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
        String sql = "INSERT INTO contas (cpf, nome, saldo) VALUES (?, ?, ?) " +
                "ON CONFLICT (cpf) DO UPDATE SET saldo = EXCLUDED.saldo";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conta.getUsuario().cpf());
            stmt.setString(2, conta.getUsuario().nome());
            stmt.setBigDecimal(3, conta.getSaldo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao acessar o banco de dados", e);
        }
    }

    @Override
    public void removerConta(String cpf) {
        String sql = "DELETE FROM contas WHERE cpf = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new ContaInexistenteException(cpf);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao acessar o banco de dados", e);
        }
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
                    throw new ContaInexistenteException(cpf);
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
