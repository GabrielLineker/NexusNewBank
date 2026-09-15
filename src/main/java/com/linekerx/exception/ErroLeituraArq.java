package com.linekerx.exception;

import java.nio.file.Path;

public class ErroLeituraArq extends RuntimeException {
    public ErroLeituraArq(Path caminhoArquivo, Throwable causa) {
        super("Erro ao ler o arquivo: " + caminhoArquivo.toAbsolutePath(), causa);
    }
}
