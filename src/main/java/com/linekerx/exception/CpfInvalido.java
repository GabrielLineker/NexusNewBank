package com.linekerx.exception;

public class CpfInvalido extends RuntimeException {
    public CpfInvalido(String cpf) {
        super("CPF: " + cpf + " é inválido, precisa conter 11 dígitos");
    }
}
