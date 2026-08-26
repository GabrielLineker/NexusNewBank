package com.linekerx.exception;

public class ContaJaExisteException extends RuntimeException {
    public ContaJaExisteException(String cpf) {
        super("Conta já existe para o CPF: " + cpf);
    }
}
