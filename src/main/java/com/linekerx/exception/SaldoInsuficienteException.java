package com.linekerx.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(BigDecimal saldo) {
        super("Saldo insuficiente para realizar o saque. Saldo atual: " + saldo);
    }
}
