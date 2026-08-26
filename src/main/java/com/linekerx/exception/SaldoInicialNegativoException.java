package com.linekerx.exception;

import java.math.BigDecimal;

public class SaldoInicialNegativoException extends RuntimeException {
    public SaldoInicialNegativoException(BigDecimal saldo) {
        super("Saldo inicial não pode ser negativo. Saldo informado: " + saldo);
    }
}
