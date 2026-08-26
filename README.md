# 🏦 NexusNewBank

> Aplicação bancária de console em Java puro, criada com foco no estudo de **arquitetura de projeto**, **boas práticas** e **evolução incremental** do código.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
  - [Estrutura de Pacotes](#estrutura-de-pacotes)
  - [Diagrama de Dependências](#diagrama-de-dependências)
  - [Camadas da Aplicação](#camadas-da-aplicação)
- [Boas Práticas Aplicadas](#-boas-práticas-aplicadas)
- [Decisões Técnicas](#-decisões-técnicas)
- [Tecnologias](#-tecnologias)
- [Como Executar](#-como-executar)
- [Roadmap — Evolução do Projeto](#-roadmap--evolução-do-projeto)
- [Changelog](#-changelog)

---

## 💡 Sobre o Projeto

O **miniBanco** é um sistema bancário simplificado que roda no terminal. O objetivo principal **não** é ser um produto, mas sim servir como **laboratório de estudo** para:

- Praticar arquitetura em camadas (Layered Architecture)
- Aplicar princípios SOLID e boas práticas de OOP
- Retomar fluência em Java após um período sem uso da linguagem
- Documentar a evolução arquitetural do projeto ao longo do tempo

---

## ✨ Funcionalidades

| Operação         | Descrição                                       |
|------------------|--------------------------------------------------|
| Visualizar Conta | Consulta dados de uma conta pelo CPF             |
| Abrir Conta      | Cria uma nova conta com nome, CPF e saldo inicial |
| Deletar Conta    | Remove uma conta existente pelo CPF              |
| Depósito         | Adiciona saldo a uma conta                       |
| Saque            | Retira saldo de uma conta (com validação)        |
| Transferência    | Transfere valor entre duas contas                |

---

## 🏗 Arquitetura

### Estrutura de Pacotes

```
src/main/java/com/linekerx/
├── Main.java                          # Ponto de entrada — UI de console
├── domain/                            # Entidades de domínio (modelo de negócio)
│   ├── Conta.java                     # Entidade Conta com operações de saldo
│   └── Usuario.java                   # Record imutável representando o titular
├── exception/                         # Exceções de negócio customizadas
│   ├── ContaInexistenteException.java
│   ├── ContaJaExisteException.java
│   ├── CpfInvalido.java
│   ├── SaldoInicialNegativoException.java
│   ├── SaldoInsuficienteException.java
│   └── ValorNegativoException.java
├── repository/                        # Camada de persistência (in-memory)
│   └── ContaRepository.java
├── service/                           # Regras de negócio e orquestração
│   └── ContaService.java
└── utils/                             # Utilitários de formatação
    ├── FormatCpf.java
    ├── FormatInput.java
    └── FormatSaldo.java
```

### Diagrama de Dependências

```
┌─────────────┐
│   Main.java │  ← Camada de Apresentação (Console UI)
└──────┬──────┘
       │ usa
       ▼
┌──────────────┐
│ ContaService │  ← Camada de Serviço (Regras de Negócio)
└──────┬───────┘
       │ usa
       ▼
┌────────────────┐
│ ContaRepository│  ← Camada de Persistência (Dados em Memória)
└──────┬─────────┘
       │ manipula
       ▼
┌──────────────┐
│  Conta       │  ← Camada de Domínio (Entidades)
│  Usuario     │
└──────────────┘

  ╔═══════════════╗        ╔═══════════╗
  ║  exceptions/  ║        ║  utils/   ║
  ║ (transversal) ║        ║(transversal)║
  ╚═══════════════╝        ╚═══════════╝
```

### Camadas da Aplicação

| Camada          | Pacote        | Responsabilidade                                                                 |
|-----------------|---------------|----------------------------------------------------------------------------------|
| **Apresentação**| `Main`        | Interação com o usuário via console. Captura de entrada e exibição de resultados. |
| **Serviço**     | `service`     | Validações de negócio, orquestração de operações e delegação ao repositório.      |
| **Repositório** | `repository`  | Armazenamento e recuperação de dados (atualmente em `HashMap` in-memory).         |
| **Domínio**     | `domain`      | Entidades puras com comportamento próprio (ex: `Conta.depositar()`, `Conta.sacar()`). |
| **Exceções**    | `exception`   | Exceções semânticas de negócio — cada erro tem sua própria classe.                |
| **Utilitários** | `utils`       | Funções auxiliares de formatação (CPF, saldo, entrada do console).                |

---

## ✅ Boas Práticas Aplicadas

### 1. Separação de Responsabilidades (SRP)
Cada classe tem **uma única razão para mudar**:
- `Conta` → gerencia saldo
- `ContaService` → valida regras de negócio
- `ContaRepository` → armazena/recupera dados
- `Main` → apenas interface com o usuário

### 2. Exceções Semânticas e Específicas
Em vez de lançar `RuntimeException` genérica ou usar códigos de erro, o projeto define **exceções de negócio nomeadas**:
- `ContaInexistenteException` — conta não encontrada
- `ContaJaExisteException` — CPF já cadastrado
- `CpfInvalido` — CPF fora do formato esperado
- `SaldoInsuficienteException` — saque superior ao saldo
- `SaldoInicialNegativoException` — abertura de conta com saldo negativo
- `ValorNegativoException` — depósito/saque/transferência com valor ≤ 0

Isso torna o código **auto-documentado** e facilita o tratamento granular de erros.

### 3. Imutabilidade com Records
`Usuario` é declarado como `record`, tornando-o **imutável** por padrão — ideal para value objects.

```java
public record Usuario(String nome, String cpf) { }
```

### 4. Uso de `BigDecimal` para Valores Monetários
O projeto **evita `double`/`float`** para representar dinheiro, usando `BigDecimal` para garantir precisão em operações financeiras.

### 5. Encapsulamento de Comportamento no Domínio (Rich Domain)
As operações de `depositar()` e `sacar()` vivem **dentro** da classe `Conta`, não no serviço. A entidade possui comportamento, não é apenas um "saco de dados" (Anemic Domain antipattern evitado).

### 6. Injeção de Dependência via Construtor
`ContaService` recebe `ContaRepository` pelo construtor, facilitando:
- Substituição por mocks em testes
- Desacoplamento entre camadas
- Inversão de dependência (DIP)

```java
public ContaService(ContaRepository contaRepository) {
    this.contaRepository = contaRepository;
}
```

### 7. Fluxo de Exceções com try-catch-finally
A `Main` trata exceções de forma centralizada por operação, com blocos `finally` para manter a UX consistente (pausa antes de voltar ao menu).

### 8. Validação Defensiva
Todas as operações validam entradas **antes** de executar:
- CPF com 11 dígitos
- Saldo inicial ≥ 0
- Valores de operação > 0
- Existência da conta antes de operar

---

## 🔧 Decisões Técnicas

| Decisão                        | Justificativa                                                                 |
|--------------------------------|-------------------------------------------------------------------------------|
| Java 25                        | Uso da versão mais recente para aproveitar recursos modernos da linguagem     |
| Maven                          | Gerenciamento de build padrão do ecossistema Java                             |
| `HashMap` como storage         | Simplicidade para fase inicial — sem dependência de banco de dados             |
| `RuntimeException` como base   | Exceções não-checadas para evitar poluição de assinaturas de métodos          |
| Console como interface         | Foco na lógica de negócio, sem overhead de frameworks web                     |
| Sem frameworks (Spring, etc.)  | Estudo de fundamentos sem "magia" — inversão de controle manual               |

---

## 🛠 Tecnologias

- **Java 25**
- **Maven** (gerenciamento de build e dependências)
- **Nenhum framework** — Java puro

---

## 🚀 Como Executar

### Pré-requisitos
- [JDK 25+](https://jdk.java.net/25/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)

### Compilar e Executar

```bash
# Compilar o projeto
mvn compile

# Executar a aplicação
mvn exec:java -Dexec.mainClass="com.linekerx.Main"

# Ou via javac/java diretamente
javac -d target/classes src/main/java/com/linekerx/**/*.java src/main/java/com/linekerx/Main.java
java -cp target/classes com.linekerx.Main
```

---

## 🗺 Roadmap — Evolução do Projeto

O projeto será evoluído de forma incremental. Cada etapa representa um aprendizado arquitetural:

- [x] **v1.0** — Estrutura base com camadas (domain, service, repository, exception, utils)
- [ ] **v1.1** — Testes unitários com JUnit 5 + Mockito
- [ ] **v1.2** — Interfaces para `Repository` (programar para abstrações, não implementações)
- [ ] **v1.3** — Persistência em arquivo (JSON ou CSV) — substituindo o `HashMap`
- [ ] **v2.0** — Migração para API REST com Spring Boot
- [ ] **v2.1** — Persistência com banco de dados (H2 / PostgreSQL + JPA)
- [ ] **v2.2** — DTOs, validação com Bean Validation, e tratamento global de erros
- [ ] **v3.0** — Autenticação e autorização (Spring Security)
- [ ] **v3.1** — Docker + Docker Compose
- [ ] **v3.2** — CI/CD com GitHub Actions

> 💡 Cada etapa será documentada aqui e registrada em commits/tags no repositório.

---

## 📓 Changelog

### v1.0 — Fundação (Agosto 2026)
- ✅ Estrutura de pacotes em camadas
- ✅ Entidades de domínio (`Conta`, `Usuario` como record)
- ✅ Exceções de negócio customizadas (6 tipos)
- ✅ Repository in-memory com `HashMap`
- ✅ Service com regras de negócio e validações
- ✅ Interface de console com menu interativo
- ✅ Utilitários de formatação (CPF, saldo, input)
- ✅ `BigDecimal` para valores monetários
- ✅ Injeção de dependência manual via construtor

---

## 📄 Licença

Este projeto é de uso pessoal e educacional.

---

<p align="center">
  Feito por <strong>Gabriel Lineker</strong>
</p>
