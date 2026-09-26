package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.exception.ContaInexistenteException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class ContaRepositoryArqBase implements IContaRepository {
    protected Map<String, Conta> contas;

    protected void configRepository(Path caminhoArquivo) {
        if(!Files.exists(caminhoArquivo)) {
            criarDirArq();
            this.contas = new HashMap<>();
        } else {
            this.contas = carregarContas();
        }
    }

    @Override
    public void salvarConta(Conta conta) {
        contas.put(conta.getUsuario().cpf(), conta);
        salvarDados();
    }

    @Override
    public void removerConta(String cpf) {
        Conta contaParaRemover = contas.remove(cpf);
        if (contaParaRemover == null) {
            throw new ContaInexistenteException(cpf);
        }
        salvarDados();
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

    protected abstract void criarDirArq();
    protected abstract Map<String, Conta> carregarContas();
    protected abstract void salvarDados();

}
