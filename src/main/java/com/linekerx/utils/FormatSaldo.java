package com.linekerx.utils;

import java.math.BigDecimal;
import java.util.Locale;

public class FormatSaldo {

    public static String formatarSaldo(BigDecimal saldo) {
        return String.format(Locale.of("pt", "BR"), "R$ %.2f", saldo);
    }

}

