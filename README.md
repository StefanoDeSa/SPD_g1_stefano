# Leilão Simples SPD

Projeto em **Java** desenvolvido para a disciplina de **Sistemas Paralelos e Distribuídos**.

## Objetivo
Implementar um sistema de leilão em rede com:
- um servidor central;
- múltiplos clientes conectados ao mesmo tempo;
- comunicação via **TCP** e **UDP**;
- concorrência com **threads**;
- autenticação de usuários;
- persistência do histórico em **JSON**.

## Funcionalidades
### Servidor
- cadastra item do leilão;
- recebe e valida lances;
- identifica o autor do lance;
- informa novos lances em tempo real;
- encerra o leilão;
- informa vencedor e valor final;
- salva o histórico em arquivo JSON.

### Clientes
- conectam ao servidor pela rede;
- fazem login com usuário e senha;
- consultam o estado do leilão;
- compradores enviam lances;
- recebem atualizações em tempo real via UDP.

## Requisitos Atendidos
### Obrigatórios
- sockets **TCP/UDP**;
- conexões concorrentes com **threads**;
- gerenciamento do estado do leilão;
- validação de lances;
- notificação em tempo real;
- persistência em arquivo.

### Bônus
- autenticação de participantes: **implementado**;
- criptografia das comunicações: **não implementado**.

## Estrutura do Projeto
```text
src/main/java/br/com/spd/leilao/
├── cliente/
│   ├── ClienteLeilao.java
│   └── OuvinteUdp.java
├── modelo/
│   ├── EstadoLeilao.java
│   ├── ItemLeilao.java
│   ├── Lance.java
│   └── Usuario.java
├── servidor/
│   ├── ServidorLeilao.java
│   └── TratadorCliente.java
└── util/
    └── JsonUtil.java
```

## Tecnologias
- Java 17
- TCP
- UDP
- Threads
- JSON com Jackson

## Execução
### Servidor
Executar:
`br.com.spd.leilao.servidor.ServidorLeilao`

### Cliente
Executar uma ou mais instâncias de:
`br.com.spd.leilao.cliente.ClienteLeilao`

## Fluxo de Uso
1. iniciar o servidor;
2. conectar os clientes;
3. realizar login;
4. administrador cadastrar o item;
5. compradores enviarem lances;
6. servidor notificar os participantes;
7. administrador encerrar o leilão;
8. histórico final ser salvo em JSON.

## Arquivos de Dados
- `dados/usuarios.json`: usuários permitidos no sistema;
- `dados/historico-leilao.json`: histórico final do leilão.

## Observação Final
O projeto foi desenvolvido com foco em simplicidade, organização e facilidade de apresentação, atendendo todos os requisitos obrigatórios da atividade e parte do bônus.
