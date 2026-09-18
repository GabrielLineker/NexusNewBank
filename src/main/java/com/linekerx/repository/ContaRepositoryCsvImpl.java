package com.linekerx.repository;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.ContaInexistenteException;
import com.linekerx.exception.ErroAoCriarArq;
import com.linekerx.exception.ErroLeituraArq;
import com.linekerx.exception.ErroSalvarArq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import java.math.BigDecimal;

public class ContaRepositoryCsvImpl implements IContaRepositoryData {

    public static final Path CAMINHO_ARQUIVO = Path.of("data/csvs/contas.csv");
    private final Map<String, Conta> contas = lerContasDoArquivo();


    private Map<String, Conta> lerContasDoArquivo() {
        try {
            if(!Files.exists(CAMINHO_ARQUIVO)) {
                return new HashMap<>();
            }
            Stream<String> linhas = Files.lines(CAMINHO_ARQUIVO);
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
            throw new ErroLeituraArq(CAMINHO_ARQUIVO, e); // Eu sei q o throw aqui n serve de nada
        }
    }

    @Override
    public void criarArquivoSeNaoExistir() {
        try{
            if(!Files.exists(CAMINHO_ARQUIVO)) {
                if(!Files.exists(CAMINHO_ARQUIVO.getParent())) {
                    Files.createDirectories(CAMINHO_ARQUIVO.getParent());
                }
                Files.createFile(CAMINHO_ARQUIVO);
            }
        } catch (IOException e) {
            throw new ErroAoCriarArq(CAMINHO_ARQUIVO, e);
        }
    }

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

    @Override
    public void salvarDadosNoArquivo() {
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
            Files.writeString(CAMINHO_ARQUIVO, conteudo.toString());
        } catch (IOException e) {
            throw new ErroSalvarArq(CAMINHO_ARQUIVO, e);
        }
    }
}
