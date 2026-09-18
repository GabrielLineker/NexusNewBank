package com.linekerx.repository;

public interface IContaRepositoryData extends IContaRepository {
    void criarArquivoSeNaoExistir();
    void salvarDadosNoArquivo();
}
