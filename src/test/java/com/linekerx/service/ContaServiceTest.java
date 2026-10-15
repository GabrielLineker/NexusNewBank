package com.linekerx.service;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.*;
import com.linekerx.repository.IContaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock private IContaRepository contaRepository;

    @InjectMocks private ContaService contaService;

    @Test
    void deveAbrirContaComSucesso() {
        contaService.abrirConta("João", "12345678901", new BigDecimal("100.00"));
        Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(Mockito.any(Conta.class));
    }

    @Test
    void deveLancarCpfInvalidoQuandoCpfForInvalido() {
        assertThrows(CpfInvalido.class, () -> {
            contaService.abrirConta("João", "1234567890", new BigDecimal("100.00"));
        });
    }

    @Test
    void deveLancarContaJaExisteExceptionQuandoContaJaExiste() {
        Mockito.when(contaRepository.existeContaParaCpf("12345678901")).thenReturn(true);

        assertThrows(ContaJaExisteException.class, () -> {
            contaService.abrirConta("João", "12345678901", new BigDecimal("100.00"));
        });
    }

    @Test
    void deveLancarSaldoInicialNegativoExceptionQuandoSaldoInicialForNegativo() {
        assertThrows(SaldoInicialNegativoException.class, () -> {
            contaService.abrirConta("João", "12345678901", new BigDecimal("-100.00"));
        });
    }

    @Test
    void deveDeletarContaComSucesso() {
        contaService.deletarConta("12345678901");
        Mockito.verify(contaRepository, Mockito.times(1)).removerConta("12345678901");
    }

    @Test
    void deveLancarContaInexistenteExceptionQuandoRemoverContaInexistente() {
        Mockito.doThrow(new ContaInexistenteException("A conta 12345678900 não está cadastrada no sistema."))
                .when(contaRepository).removerConta("12345678900");

        assertThrows(ContaInexistenteException.class, () -> {
            contaService.deletarConta("12345678900");
        });
    }

    @Test
    void deveRealizarDepositoComSucesso() {
        Conta conta = new Conta(new Usuario("João", "12345678901"), new BigDecimal("100.00"));
        Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(conta);

        contaService.realizarDeposito("12345678901", new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), conta.getSaldo());

        Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(conta);
    }

    @Test
    void deveLancarValorNegativoExceptionQuandoDepositoForNegativo(){
        assertThrows(ValorNegativoException.class, () -> {
            contaService.realizarDeposito("12345678901", new BigDecimal("-50.00"));
        });
    }

    @Test
    void deveLancarValorNegativoExceptionQuandoSaqueForNegativo(){
        assertThrows(ValorNegativoException.class, () -> {
            contaService.realizarSaque("12345678901", new BigDecimal("-50.00"));
        });
    }

    @Test
    void deveLancarSaldoInsuficienteExceptionQuandoSaqueForMaiorQueSaldo(){
        Conta conta = new Conta(new Usuario("João", "12345678901"), new BigDecimal("50.00"));
        Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(conta);

        assertThrows(SaldoInsuficienteException.class, () -> {
            contaService.realizarSaque("12345678901", new BigDecimal("100.00"));
        });

        Mockito.verify(contaRepository, Mockito.never()).salvarConta(conta);
    }

    @Test
    void deveRealizarSaqueComSucesso(){
        Conta conta = new Conta(new Usuario("João", "12345678901"), new BigDecimal("100.00"));
        Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(conta);

        contaService.realizarSaque("12345678901", new BigDecimal("50.00"));
        assertEquals(new BigDecimal("50.00"), conta.getSaldo());

        Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(conta);
    }

    @Test
    void deveRealizarTransferenciaComSucesso() {
        Conta contaOrigem = new Conta(new Usuario("João", "12345678901"), new BigDecimal("100.00"));
        Conta contaDestino = new Conta(new Usuario("Maria", "10987654321"), new BigDecimal("50.00"));

        Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(contaOrigem);
        Mockito.when(contaRepository.buscarPorCpf("10987654321")).thenReturn(contaDestino);

        contaService.transferir("12345678901", "10987654321", new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("80.00"), contaDestino.getSaldo());

        Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(contaOrigem);
        Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(contaDestino);
    }

    @Test
    void deveLancarSaldoInsuficienteExceptionQuandoTransferenciaForMaiorQueSaldo() {
        Conta contaOrigem = new Conta(new Usuario("João", "12345678901"), new BigDecimal("50.00"));
        Conta contaDestino = new Conta(new Usuario("Maria", "10987654321"), new BigDecimal("50.00"));

        Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(contaOrigem);
        Mockito.when(contaRepository.buscarPorCpf("10987654321")).thenReturn(contaDestino);

        assertThrows(SaldoInsuficienteException.class, () -> {
            contaService.transferir("12345678901", "10987654321", new BigDecimal("100.00"));
        });

        Mockito.verify(contaRepository, Mockito.never()).salvarConta(contaOrigem);
        Mockito.verify(contaRepository, Mockito.never()).salvarConta(contaDestino);
    }

    @Test
    void deveLancarValorNegativoExceptionQuandoTransferenciaForNegativa() {
        assertThrows(ValorNegativoException.class, () -> {
            contaService.transferir("12345678901", "10987654321", new BigDecimal("-50.00"));
        });
    }
}
