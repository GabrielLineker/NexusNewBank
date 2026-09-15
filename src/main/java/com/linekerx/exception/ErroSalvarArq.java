package com.linekerx.exception;

import java.nio.file.Path;

public class ErroSalvarArq extends RuntimeException {
    public ErroSalvarArq(Path caminhoArquivo, Throwable causa) {
        super("Erro ao salvar o arquivo: " + caminhoArquivo.toString(), causa);
    }
}
