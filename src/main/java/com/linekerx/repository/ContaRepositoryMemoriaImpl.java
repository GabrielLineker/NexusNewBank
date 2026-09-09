package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.exception.ContaInexistenteException;
import java.util.HashMap;
import java.util.Map;

public class ContaRepositoryMemoriaImpl implements IContaRepository {

    private final Map<String, Conta> contas = new HashMap<>();

    @Override
    public void salvarConta(Conta conta) {
        contas.put(conta.getUsuario().cpf(), conta);
    }

    @Override
    public void removerConta(String cpf) {
        Conta contaParaRemover = contas.remove(cpf);
        if (contaParaRemover == null) {
            throw new ContaInexistenteException(cpf);
        }
    }

    @Override
    public Conta buscarPorCpf(String cpf) {
        Conta contaBuscada = contas.get(cpf);
        if (contaBuscada == null) {
            throw new ContaInexistenteException(cpf);
        }
        return contaBuscada;
    }

    @Override
    public boolean existeContaParaCpf(String cpf) {
        return contas.containsKey(cpf);
    }
}
