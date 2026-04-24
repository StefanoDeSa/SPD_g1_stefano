# Leilão Simples SPD

Projeto em Java para a disciplina de Sistemas Paralelos e Distribuídos.

## Objetivo
Implementar um sistema simples de leilão com:
- servidor único;
- múltiplos clientes concorrentes;
- comunicação TCP;
- notificações UDP;
- persistência em JSON;
- autenticação;
- criptografia TLS/SSL.

## Estrutura
- `ServidorLeilao.java`: servidor principal
- `TratadorCliente.java`: uma thread por cliente
- `ClienteLeilao.java`: cliente principal
- `OuvinteUdp.java`: escuta notificações UDP
- `EstadoLeilao.java`: regra de negócio
- `JsonUtil.java`: leitura e escrita de JSON

## Dados
- `dados/usuarios.json`
- `dados/historico-leilao.json`