package com.linekerx;

import com.linekerx.domain.Conta;
import com.linekerx.exception.*;
import com.linekerx.repository.ContaRepositoryCsvImpl;
import com.linekerx.repository.ContaRepositoryMemoriaImpl;
import com.linekerx.repository.IContaRepository;
import com.linekerx.repository.IContaRepositoryData;
import com.linekerx.service.ContaService;
import com.linekerx.utils.FormatCpf;
import com.linekerx.utils.FormatInput;
import com.linekerx.utils.FormatSaldo;

import java.math.BigDecimal;


public class Main {

    private static final IContaRepositoryData contaRepository = new ContaRepositoryCsvImpl();
    private static final ContaService contaService = new ContaService(contaRepository);

    static void main(String[] args) {

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Visualizar Conta");
            System.out.println("2. Abrir conta");
            System.out.println("3. Deletar conta");
            System.out.println("4. Realizar depósito");
            System.out.println("5. Realizar saque");
            System.out.println("6. Transferência");
            System.out.println("7. Sair");

            String opcao = FormatInput.formatarScanner("Opção: ");

            switch (opcao) {
                case "1" -> visualizarConta();
                case "2" -> criarConta();
                case "3" -> excluirConta();
                case "4" -> deposito();
                case "5" -> saque();
                case "6" -> transferencia();
                case "7" -> exit();
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void visualizarConta() {
        try {
            String cpf = FormatInput.formatarScanner("Digite o CPF da conta: ");
            Conta conta = contaService.buscaCpf(cpf);
            System.out.println("Nome: " + conta.getUsuario().nome());
            System.out.println("CPF: " + FormatCpf.formatarCpf(conta.getUsuario().cpf()));
            System.out.println("Saldo: R$ " + FormatSaldo.formatarSaldo(conta.getSaldo()));

        } catch (ContaInexistenteException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void criarConta() {
        try{
            String nome = FormatInput.formatarScanner("Digite o nome do titular: ");
            String cpf = FormatInput.formatarScanner("Digite o CPF do titular (apenas números): ");
            BigDecimal saldoInicial = new BigDecimal(FormatInput.formatarScanner("Digite o saldo inicial: "));

            contaService.abrirConta(nome, cpf, saldoInicial);
            System.out.println("Conta criada com sucesso!");

        } catch (ContaJaExisteException | CpfInvalido | SaldoInicialNegativoException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void excluirConta() {
        try {
            String cpf = FormatInput.formatarScanner("Digite o CPF da conta a ser excluída: ");
            contaService.deletarConta(cpf);
            System.out.println("Conta excluída com sucesso!");

        } catch (ContaInexistenteException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void deposito() {
        try {
            String cpf = FormatInput.formatarScanner("Digite o CPF da conta: ");
            BigDecimal valor = new BigDecimal(FormatInput.formatarScanner("Digite o valor do depósito: "));
            contaService.realizarDeposito(cpf, valor);
            System.out.println("Depósito realizado com sucesso!");

        } catch (ContaInexistenteException | ValorNegativoException | NumberFormatException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void saque() {
        try {
            String cpf = FormatInput.formatarScanner("Digite o CPF da conta: ");
            BigDecimal valor = new BigDecimal(FormatInput.formatarScanner("Digite o valor do saque: "));
            contaService.realizarSaque(cpf, valor);
            System.out.println("Saque realizado com sucesso!");

        } catch (ContaInexistenteException | ValorNegativoException | SaldoInsuficienteException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void transferencia() {
        try {
            String cpfOrigem = FormatInput.formatarScanner("Digite o CPF da conta de origem: ");
            String cpfDestino = FormatInput.formatarScanner("Digite o CPF da conta de destino: ");
            BigDecimal valor = new BigDecimal(FormatInput.formatarScanner("Digite o valor da transferência: "));
            contaService.transferir(cpfOrigem, cpfDestino, valor);
            System.out.println("Transferência realizada com sucesso!");

        } catch (ContaInexistenteException | ValorNegativoException | SaldoInsuficienteException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void exit() {
        try{
            contaRepository.salvarDadosNoArquivo();
            System.out.println("Dados salvos com sucesso. Saindo do programa...");
            System.exit(0);

        } catch (ErroSalvarArq e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

}
