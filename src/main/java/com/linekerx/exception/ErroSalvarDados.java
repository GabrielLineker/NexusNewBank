package com.linekerx.exception;

import java.nio.file.Path;

public class ErroSalvarDados extends RuntimeException {
    public ErroSalvarDados(Path caminhoArquivo, Throwable causa) {
        super("Erro ao salvar dados: " + caminhoArquivo.toString(), causa);
    }
}
