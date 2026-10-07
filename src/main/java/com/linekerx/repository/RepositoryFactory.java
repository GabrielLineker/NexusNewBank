package com.linekerx.repository;

import io.github.cdimascio.dotenv.Dotenv;

public class RepositoryFactory {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String TIPO = dotenv.get("TIPO_PERSISTENCIA").toLowerCase();

    public static IContaRepository criarContaRepository() {
        return switch (TIPO) {
            case "json" -> new ContaRepositoryJsonImpl();
            case "jdbc" -> new ContaRepositoryJdbcImpl();
            case "csv" -> new ContaRepositoryCsvImpl();
            case "memoria" -> new ContaRepositoryMemoriaImpl();
            default -> throw new IllegalArgumentException("Tipo de persistência não suportado: " + TIPO);
        };
    }
}