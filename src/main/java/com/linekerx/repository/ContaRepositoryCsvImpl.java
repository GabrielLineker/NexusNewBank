package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.ErroAoCriarArq;
import com.linekerx.exception.ErroLeituraArq;
import com.linekerx.exception.ErroSalvarDados;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import java.math.BigDecimal;

public class ContaRepositoryCsvImpl extends ContaRepositoryArqBase {

    public ContaRepositoryCsvImpl() {
        configRepository("data/csvs/contas.csv");
    }

    @Override
    public Map<String, Conta> carregarContas() {
        try {
            Stream<String> linhas = Files.lines(caminhoArquivo);
            Map<String, Conta> contasRef = new HashMap<>();
            linhas.filter(linha -> !linha.isBlank()).skip(1).forEach(linha -> {
                String[] partes = linha.split("[,;]");
                if (partes.length < 3) {
                    return;
                }
                String cpf = partes[0].trim();
                String nome = partes[1].trim();
                String saldoStr = partes[2].trim();
                BigDecimal saldo = new BigDecimal(saldoStr);
                Conta conta = new Conta(new Usuario(nome, cpf), saldo);
                contasRef.put(cpf, conta);
            });
            linhas.close();
            return contasRef;

        } catch (IOException e) {
            throw new ErroLeituraArq(caminhoArquivo, e);
        }
    }

    @Override
    public void criarDirArq() {
        try {
            if (!Files.exists(caminhoArquivo.getParent())) {
                Files.createDirectories(caminhoArquivo.getParent());
            }
            Files.createFile(caminhoArquivo);
        } catch (IOException e) {
            throw new ErroAoCriarArq(caminhoArquivo, e);
        }
    }

    @Override
    public void salvarDados() {
        try {
            StringBuilder conteudo = new StringBuilder();
            conteudo.append("CPF;Nome;Saldo")
                    .append(System.lineSeparator());
            for (Conta conta : contas.values()) {
                conteudo.append(conta.getUsuario().cpf())
                        .append(";")
                        .append(conta.getUsuario().nome())
                        .append(";")
                        .append(conta.getSaldo())
                        .append(System.lineSeparator());
            }
            Files.writeString(caminhoArquivo, conteudo.toString());
        } catch (IOException e) {
            throw new ErroSalvarDados(caminhoArquivo, e);
        }
    }
}