package com.linekerx.exception;

import java.io.IOException;
import java.nio.file.Path;

public class ErroAoCriarArq extends RuntimeException {
    public ErroAoCriarArq(Path caminhoArquivo, IOException causa) {
        super("Erro ao criar o arquivo: " + caminhoArquivo.toString(), causa);
    }
}
