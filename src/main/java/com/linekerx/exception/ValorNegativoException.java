package com.linekerx.exception;

import java.math.BigDecimal;

public class ValorNegativoException extends RuntimeException {
    public ValorNegativoException(BigDecimal valor) {
        super("O valor não pode ser negativo. Valor informado: " + valor);
    }
}
