package com.linekerx;

import com.linekerx.domain.Conta;
import com.linekerx.exception.*;
import com.linekerx.repository.ContaRepositoryCsvImpl;
import com.linekerx.repository.ContaRepositoryJsonImpl;
import com.linekerx.repository.IContaRepository;
import com.linekerx.service.ContaService;
import com.linekerx.utils.FormatCpf;
import com.linekerx.utils.FormatInput;
import com.linekerx.utils.FormatSaldo;

import java.math.BigDecimal;


public class Main {

    private static IContaRepository contaRepository;
    private static ContaService contaService;

    public static void main(String[] args) {
        inicializar();

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

    private static void inicializar() {
        try {
            contaRepository = new ContaRepositoryJsonImpl();
            contaService = new ContaService(contaRepository);

        } catch (ErroAoCriarArq | ErroLeituraArq e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void visualizarConta() {
        try {
            String cpf = FormatInput.formatarScanner("Digite o CPF da conta: ");
            Conta conta = contaService.buscaCpf(cpf);
            System.out.println("Nome: " + conta.getUsuario().nome());
            System.out.println("CPF: " + FormatCpf.formatarCpf(conta.getUsuario().cpf()));
            System.out.println("Saldo: " + FormatSaldo.formatarSaldo(conta.getSaldo()));

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

        } catch (ContaJaExisteException | CpfInvalido | SaldoInicialNegativoException | NumberFormatException | ErroSalvarDados e) {
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

        } catch (ContaInexistenteException | ErroSalvarDados e) {
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

        } catch (ContaInexistenteException | ValorNegativoException | NumberFormatException | ErroSalvarDados e) {
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

        } catch (ContaInexistenteException | ValorNegativoException | SaldoInsuficienteException | NumberFormatException | ErroSalvarDados e) {
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

        } catch (ContaInexistenteException | ValorNegativoException | SaldoInsuficienteException | NumberFormatException | ErroSalvarDados e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println("Pressione Enter para continuar...");
            FormatInput.formatarScanner("");
        }
    }

    private static void exit() {
        System.out.println("Saindo do sistema...");
        System.exit(0);
    }

}
