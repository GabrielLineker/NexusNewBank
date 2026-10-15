package com.linekerx.service;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.*;
import com.linekerx.repository.IContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock private IContaRepository contaRepository;
    @InjectMocks private ContaService contaService;

    @Nested
    @DisplayName("Abrir Conta")
    class AbrirConta {

        @Test
        void deveAbrirContaComSucesso() {
            contaService.abrirConta("João", "12345678901", new BigDecimal("100.00"));

            ArgumentCaptor<Conta> captor = ArgumentCaptor.forClass(Conta.class);
            Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(captor.capture());

            Conta contaSalva = captor.getValue();
            assertEquals("João", contaSalva.getUsuario().nome());
            assertEquals("12345678901", contaSalva.getUsuario().cpf());
            assertEquals(new BigDecimal("100.00"), contaSalva.getSaldo());
        }

        @Test
        void deveLancarCpfInvalidoQuandoCpfForInvalido() {
            assertThrows(CpfInvalido.class, () -> {
                contaService.abrirConta("João", "1234567890", new BigDecimal("100.00"));
            });

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarContaJaExisteExceptionQuandoContaJaExiste() {
            Mockito.when(contaRepository.existeContaParaCpf("12345678901")).thenReturn(true);

            assertThrows(ContaJaExisteException.class, () -> {
                contaService.abrirConta("João", "12345678901", new BigDecimal("100.00"));
            });

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }

        @Test
        void deveLancarSaldoInicialNegativoExceptionQuandoSaldoInicialForNegativo() {
            assertThrows(SaldoInicialNegativoException.class, () -> {
                contaService.abrirConta("João", "12345678901", new BigDecimal("-100.00"));
            });

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Deletar Conta")
    class DeletarConta {

        @Test
        void deveDeletarContaComSucesso() {
            contaService.deletarConta("12345678901");
            Mockito.verify(contaRepository, Mockito.times(1)).removerConta("12345678901");
        }

        @Test
        void deveLancarContaInexistenteExceptionQuandoRemoverContaInexistente() {
            Mockito.doThrow(new ContaInexistenteException("12345678900"))
                    .when(contaRepository).removerConta("12345678900");

            assertThrows(ContaInexistenteException.class, () -> {
                contaService.deletarConta("12345678900");
            });
        }
    }

    @Nested
    @DisplayName("Depósito")
    class Deposito {

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

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarValorNegativoExceptionQuandoDepositoForZero(){
            assertThrows(ValorNegativoException.class, () -> {
                contaService.realizarDeposito("12345678901", BigDecimal.ZERO);
            });

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarContaInexistenteExceptionQuandoDepositarEmContaInexistente(){
            Mockito.when(contaRepository.buscarPorCpf("12345678900"))
                    .thenThrow(new ContaInexistenteException("12345678900"));

            assertThrows(ContaInexistenteException.class, () -> {
                contaService.realizarDeposito("12345678900", new BigDecimal("50.00"));
            });

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Saque")
    class Saque {

        @Test
        void deveRealizarSaqueComSucesso(){
            Conta conta = new Conta(new Usuario("João", "12345678901"), new BigDecimal("100.00"));
            Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(conta);

            contaService.realizarSaque("12345678901", new BigDecimal("50.00"));
            assertEquals(new BigDecimal("50.00"), conta.getSaldo());

            Mockito.verify(contaRepository, Mockito.times(1)).salvarConta(conta);
        }

        @Test
        void deveLancarValorNegativoExceptionQuandoSaqueForNegativo(){
            assertThrows(ValorNegativoException.class, () -> {
                contaService.realizarSaque("12345678901", new BigDecimal("-50.00"));
            });

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarValorNegativoExceptionQuandoSaqueForZero(){
            assertThrows(ValorNegativoException.class, () -> {
                contaService.realizarSaque("12345678901", BigDecimal.ZERO);
            });

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarContaInexistenteExceptionQuandoSacarDeContaInexistente(){
            Mockito.when(contaRepository.buscarPorCpf("12345678900"))
                    .thenThrow(new ContaInexistenteException("12345678900"));

            assertThrows(ContaInexistenteException.class, () -> {
                contaService.realizarSaque("12345678900", new BigDecimal("50.00"));
            });

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }

        @Test
        void deveLancarSaldoInsuficienteExceptionQuandoSaqueForMaiorQueSaldo(){
            Conta conta = new Conta(new Usuario("João", "12345678901"), new BigDecimal("50.00"));
            Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(conta);

            assertThrows(SaldoInsuficienteException.class, () -> {
                contaService.realizarSaque("12345678901", new BigDecimal("100.00"));
            });

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Transferência")
    class Transferencia {

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

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }

        @Test
        void deveLancarValorNegativoExceptionQuandoTransferenciaForNegativa() {
            assertThrows(ValorNegativoException.class, () -> {
                contaService.transferir("12345678901", "10987654321", new BigDecimal("-50.00"));
            });

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarValorNegativoExceptionQuandoTransferenciaForZero() {
            assertThrows(ValorNegativoException.class, () -> {
                contaService.transferir("12345678901", "10987654321", BigDecimal.ZERO);
            });

            Mockito.verifyNoInteractions(contaRepository);
        }

        @Test
        void deveLancarContaInexistenteExceptionQuandoContaOrigemNaoExistir() {
            Mockito.when(contaRepository.buscarPorCpf("12345678900"))
                    .thenThrow(new ContaInexistenteException("12345678900"));

            assertThrows(ContaInexistenteException.class, () -> {
                contaService.transferir("12345678900", "10987654321", new BigDecimal("30.00"));
            });

            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }

        @Test
        void deveLancarContaInexistenteExceptionQuandoContaDestinoNaoExistir() {
            Conta contaOrigem = new Conta(new Usuario("João", "12345678901"), new BigDecimal("100.00"));

            Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(contaOrigem);
            Mockito.when(contaRepository.buscarPorCpf("10987654300"))
                    .thenThrow(new ContaInexistenteException("10987654300"));

            assertThrows(ContaInexistenteException.class, () -> {
                contaService.transferir("12345678901", "10987654300", new BigDecimal("30.00"));
            });

            assertEquals(new BigDecimal("100.00"), contaOrigem.getSaldo());
            Mockito.verify(contaRepository, Mockito.never()).salvarConta(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Busca por CPF")
    class BuscaPorCpf {

        @Test
        void deveRetornarContaAoBuscarPorCpf() {
            Conta conta = new Conta(new Usuario("João", "12345678901"), new BigDecimal("100.00"));
            Mockito.when(contaRepository.buscarPorCpf("12345678901")).thenReturn(conta);

            Conta contaBuscada = contaService.buscaCpf("12345678901");

            assertSame(conta, contaBuscada);
        }

        @Test
        void deveLancarContaInexistenteExceptionAoBuscarCpfInexistente() {
            Mockito.when(contaRepository.buscarPorCpf("12345678900"))
                    .thenThrow(new ContaInexistenteException("12345678900"));

            assertThrows(ContaInexistenteException.class, () -> {
                contaService.buscaCpf("12345678900");
            });
        }
    }
}
