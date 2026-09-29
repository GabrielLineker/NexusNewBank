package com.linekerx.repository;

public class ContaRepositoryJdbcImpl implements IContaRepository {
    @Override
    public void salvarConta(com.linekerx.domain.Conta conta) {
        // Implementação para salvar a conta no banco de dados usando JDBC
    }

    @Override
    public void removerConta(String cpf) {
        // Implementação para remover a conta do banco de dados usando JDBC
    }

    @Override
    public com.linekerx.domain.Conta buscarPorCpf(String cpf) {
        // Implementação para buscar a conta no banco de dados usando JDBC
        return null;
    }

    @Override
    public boolean existeContaParaCpf(String cpf) {
        // Implementação para verificar se a conta existe no banco de dados usando JDBC
        return false;
    }
}
