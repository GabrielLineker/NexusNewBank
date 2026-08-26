package com.linekerx.domain;

import com.linekerx.exception.SaldoInsuficienteException;
import java.math.BigDecimal;

public class Conta {
    private final Usuario usuario;
    private BigDecimal saldo;

    public Conta(Usuario usuario, BigDecimal saldo) {
        this.usuario = usuario;
        this.saldo = saldo;
    }

    public void depositar(BigDecimal valor) {
        saldo = saldo.add(valor);
    }

    public void sacar(BigDecimal valor) {
        if (valor.compareTo(saldo) <= 0) {
            saldo = saldo.subtract(valor);
        } else {
            throw new SaldoInsuficienteException(saldo);
        }
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

}
