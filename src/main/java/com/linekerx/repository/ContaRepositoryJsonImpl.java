package com.linekerx.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.linekerx.domain.Conta;
import com.linekerx.exception.ContaInexistenteException;
import com.linekerx.exception.ErroAoCriarArq;
import com.linekerx.exception.ErroLeituraArq;
import com.linekerx.exception.ErroSalvarDados;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class ContaRepositoryJsonImpl implements IContaRepository {

    public static final Path CAMINHO_ARQUIVO = Path.of("data/jsons/contas.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, Conta> contas;

    public ContaRepositoryJsonImpl() {
        if(!Files.exists(CAMINHO_ARQUIVO)) {
            criarArquivoSeNaoExistir();
            this.contas = new HashMap<>();
        } else {
            this.contas = lerContasDoArquivo();
        }
    }

    private Map<String, Conta> lerContasDoArquivo() {
        try {
            String conteudo = Files.readString(CAMINHO_ARQUIVO);
            Type tipoMapa = new TypeToken<Map<String, Conta>>() {}.getType();
            Map<String, Conta> contasRef = gson.fromJson(conteudo, tipoMapa);
            return contasRef != null ? contasRef : new HashMap<>();

        } catch (IOException e) {
            throw new ErroLeituraArq(CAMINHO_ARQUIVO, e);
        }
    }

    private void criarArquivoSeNaoExistir() {
        try{
            if(!Files.exists(CAMINHO_ARQUIVO.getParent())) {
                Files.createDirectories(CAMINHO_ARQUIVO.getParent());
            }
            Files.writeString(CAMINHO_ARQUIVO, "{}");

        } catch (IOException e) {
            throw new ErroAoCriarArq(CAMINHO_ARQUIVO, e);
        }
    }

    @Override
    public void salvarConta(Conta conta) {
        contas.put(conta.getUsuario().cpf(), conta);
        salvarDadosNoArquivo();
    }

    @Override
    public void removerConta(String cpf) {
        Conta contaParaRemover = contas.remove(cpf);
        if (contaParaRemover == null) {
            throw new ContaInexistenteException(cpf);
        }
        salvarDadosNoArquivo();
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

    private void salvarDadosNoArquivo() {
        try {
            String json = gson.toJson(contas);
            Files.writeString(CAMINHO_ARQUIVO, json);

        } catch (IOException e) {
            throw new ErroSalvarDados(CAMINHO_ARQUIVO, e);
        }
    }
}