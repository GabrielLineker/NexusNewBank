package com.linekerx.repository;

import com.linekerx.domain.Conta;

public interface IContaRepository {
    void salvarConta(Conta conta);
    void removerConta(String cpf);
    Conta buscarPorCpf(String cpf);
    boolean existeContaParaCpf(String cpf);
}
