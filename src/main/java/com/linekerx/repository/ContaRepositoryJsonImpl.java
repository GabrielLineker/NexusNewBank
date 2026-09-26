package com.linekerx.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.linekerx.domain.Conta;
import com.linekerx.exception.ErroAoCriarArq;
import com.linekerx.exception.ErroLeituraArq;
import com.linekerx.exception.ErroSalvarDados;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


public class ContaRepositoryJsonImpl extends ContaRepositoryArqBase {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ContaRepositoryJsonImpl() {
        configRepository("data/jsons/contas.json");
    }

    @Override
    public Map<String, Conta> carregarContas() {
        try {
            String conteudo = Files.readString(caminhoArquivo);
            Type tipoMapa = new TypeToken<Map<String, Conta>>() {}.getType();
            Map<String, Conta> contasRef = gson.fromJson(conteudo, tipoMapa);
            return contasRef != null ? contasRef : new HashMap<>();

        } catch (IOException e) {
            throw new ErroLeituraArq(caminhoArquivo, e);
        }
    }

    @Override
    public void criarDirArq() {
        try{
            if(!Files.exists(caminhoArquivo.getParent())) {
                Files.createDirectories(caminhoArquivo.getParent());
            }
            Files.writeString(caminhoArquivo, "{}");

        } catch (IOException e) {
            throw new ErroAoCriarArq(caminhoArquivo, e);
        }
    }

    @Override
    public void salvarDados() {
        try {
            String json = gson.toJson(contas);
            Files.writeString(caminhoArquivo, json);

        } catch (IOException e) {
            throw new ErroSalvarDados(caminhoArquivo, e);
        }
    }
}