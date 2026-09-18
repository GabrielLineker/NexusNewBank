package com.linekerx.service;

import com.linekerx.domain.Conta;
import com.linekerx.domain.Usuario;
import com.linekerx.exception.ContaJaExisteException;
import com.linekerx.exception.CpfInvalido;
import com.linekerx.exception.SaldoInicialNegativoException;
import com.linekerx.exception.ValorNegativoException;
import com.linekerx.repository.IContaRepository;
import com.linekerx.repository.IContaRepositoryData;

import java.math.BigDecimal;

public class ContaService {
    private final IContaRepositoryData contaRepository;

    public ContaService(IContaRepositoryData contaRepository) {
        this.contaRepository = contaRepository;
    }

    public void criarArquivo() {
        contaRepository.criarArquivoSeNaoExistir();
    }

    public void abrirConta(String nome, String cpf, BigDecimal saldoInicial) {
        if(cpf.length() != 11) {
            throw new CpfInvalido(cpf);
        }
        if(contaRepository.existeContaParaCpf(cpf)) {
            throw new ContaJaExisteException(cpf);
        }
        if(saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInicialNegativoException(saldoInicial);
        }
        Usuario usuario = new Usuario(nome, cpf);
        Conta conta = new Conta(usuario, saldoInicial);
        contaRepository.salvarConta(conta);
    }

    public void deletarConta(String cpf) {
        contaRepository.removerConta(cpf);
    }

    public void realizarDeposito(String cpf, BigDecimal valor) {
        if(valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorNegativoException(valor);
        }
        Conta conta = contaRepository.buscarPorCpf(cpf);
        conta.depositar(valor);
    }

    public void realizarSaque(String cpf, BigDecimal valor) {
        if(valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorNegativoException(valor);
        }
        Conta conta = contaRepository.buscarPorCpf(cpf);
        conta.sacar(valor);
    }

    public void transferir(String cpfOrigem, String cpfDestino, BigDecimal valor) {
        if(valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorNegativoException(valor);
        }
        Conta contaOrigem = contaRepository.buscarPorCpf(cpfOrigem);
        Conta contaDestino = contaRepository.buscarPorCpf(cpfDestino);
        contaOrigem.sacar(valor);
        contaDestino.depositar(valor);
    }

    public Conta buscaCpf(String cpf) {
        return contaRepository.buscarPorCpf(cpf);
    }

    public void salvarDados(){
        contaRepository.salvarDadosNoArquivo();
    }
}
