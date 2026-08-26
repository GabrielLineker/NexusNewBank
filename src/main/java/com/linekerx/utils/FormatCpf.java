package com.linekerx.utils;

public class FormatCpf {

    public static String formatarCpf(String cpf) {
        return cpf.replaceAll("[^\\d]", "")
                .replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
