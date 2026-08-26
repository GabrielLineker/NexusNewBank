package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.exception.ContaInexistenteException;
import java.util.HashMap;
import java.util.Map;

public class ContaRepository {

    private final Map<String, Conta> contas = new HashMap<>();

    public void salvarConta(Conta conta) {
        contas.put(conta.getUsuario().cpf(), conta);
    }

    public void removerConta(String cpf) {
        Conta contaParaRemover = contas.remove(cpf);
        if (contaParaRemover == null) {
            throw new ContaInexistenteException(cpf);
        }
    }

    public Conta buscarPorCpf(String cpf) {
        Conta contaBuscada = contas.get(cpf);
        if (contaBuscada == null) {
            throw new ContaInexistenteException(cpf);
        }
        return contaBuscada;
    }

    public boolean existeContaParaCpf(String cpf) {
        return contas.containsKey(cpf);
    }
}
