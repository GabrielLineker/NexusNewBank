package com.linekerx.exception;

public class ContaInexistenteException extends RuntimeException {
    public ContaInexistenteException(String cpf) {
        super("A conta " + cpf + " não está cadastrada no sistema.");
    }
}
