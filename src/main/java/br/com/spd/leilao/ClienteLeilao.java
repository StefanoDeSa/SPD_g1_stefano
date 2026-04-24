package br.com.spd.leilao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteLeilao {
    private static final int PORTA_TCP = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OuvinteUdp ouvinteUdp = null;
        Thread threadUdp = null;

        try {
            System.out.println("=== CLIENTE DO LEILÃO ===");
            System.out.print("Informe o IP do servidor: ");
            String hostServidor = scanner.nextLine().trim();

            System.out.print("Informe a porta UDP deste cliente: ");
            int portaUdp = Integer.parseInt(scanner.nextLine().trim());

            ouvinteUdp = new OuvinteUdp(portaUdp);
            threadUdp = new Thread(ouvinteUdp);
            threadUdp.setDaemon(true);
            threadUdp.start();

            try (
                    Socket socket = new Socket(hostServidor, PORTA_TCP);
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter saida = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String mensagemInicial = entrada.readLine();
                if (mensagemInicial != null) {
                    System.out.println("Servidor: " + mensagemInicial);
                }

                boolean logado = false;
                String papelUsuario = "";

                while (!logado) {
                    System.out.println("\n=== LOGIN ===");
                    System.out.print("Usuário: ");
                    String login = scanner.nextLine().trim();

                    System.out.print("Senha: ");
                    String senha = scanner.nextLine().trim();

                    String comandoLogin = "LOGIN|" + login + "|" + senha + "|" + portaUdp;
                    saida.println(comandoLogin);

                    String resposta = entrada.readLine();
                    if (resposta == null) {
                        System.out.println("Conexão encerrada pelo servidor.");
                        return;
                    }

                    System.out.println("Servidor: " + resposta);

                    if (resposta.startsWith("OK|")) {
                        logado = true;

                        String[] partes = resposta.split("\\|");
                        if (partes.length >= 4) {
                            papelUsuario = partes[3];
                        }

                        System.out.println("Login realizado. Papel: " + papelUsuario);
                    }
                }

                boolean executando = true;

                while (executando) {
                    exibirMenu(papelUsuario);
                    System.out.print("> ");
                    String opcao = scanner.nextLine().trim();

                    switch (opcao) {
                        case "1":
                            if ("ADMIN".equalsIgnoreCase(papelUsuario)) {
                                cadastrarItem(scanner, saida, entrada);
                            } else {
                                enviarLance(scanner, saida, entrada);
                            }
                            break;

                        case "2":
                            consultarEstado(saida, entrada);
                            break;

                        case "3":
                            if ("ADMIN".equalsIgnoreCase(papelUsuario)) {
                                encerrarLeilao(saida, entrada);
                            } else {
                                sair(saida, entrada);
                                executando = false;
                            }
                            break;

                        case "4":
                            if ("ADMIN".equalsIgnoreCase(papelUsuario)) {
                                sair(saida, entrada);
                                executando = false;
                            } else {
                                System.out.println("Opção inválida.");
                            }
                            break;

                        default:
                            System.out.println("Opção inválida.");
                            break;
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Porta UDP inválida.");
        } catch (IOException e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        } finally {
            if (ouvinteUdp != null) {
                ouvinteUdp.encerrar();
            }
            scanner.close();
        }
    }

    private static void exibirMenu(String papelUsuario) {
        System.out.println();

        if ("ADMIN".equalsIgnoreCase(papelUsuario)) {
            System.out.println("=== MENU ADMIN ===");
            System.out.println("1 - Cadastrar item");
            System.out.println("2 - Consultar estado do leilão");
            System.out.println("3 - Encerrar leilão");
            System.out.println("4 - Sair");
        } else {
            System.out.println("=== MENU COMPRADOR ===");
            System.out.println("1 - Enviar lance");
            System.out.println("2 - Consultar estado do leilão");
            System.out.println("3 - Sair");
        }
    }

    private static void cadastrarItem(Scanner scanner, PrintWriter saida, BufferedReader entrada) throws IOException {
        System.out.print("Nome do item: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Descrição do item: ");
        String descricao = scanner.nextLine().trim();

        System.out.print("Valor inicial: ");
        String valorInicial = scanner.nextLine().trim();

        String comando = "CADASTRAR_ITEM|" + nome + "|" + descricao + "|" + valorInicial;
        saida.println(comando);

        String resposta = entrada.readLine();
        if (resposta != null) {
            System.out.println("Servidor: " + resposta);
        }
    }

    private static void enviarLance(Scanner scanner, PrintWriter saida, BufferedReader entrada) throws IOException {
        System.out.print("Valor do lance: ");
        String valor = scanner.nextLine().trim();

        String comando = "LANCE|" + valor;
        saida.println(comando);

        String resposta = entrada.readLine();
        if (resposta != null) {
            System.out.println("Servidor: " + resposta);
        }
    }

    private static void consultarEstado(PrintWriter saida, BufferedReader entrada) throws IOException {
        saida.println("ESTADO");

        String resposta = entrada.readLine();
        if (resposta != null) {
            System.out.println("Servidor: " + formatarEstado(resposta));
        }
    }

    private static void encerrarLeilao(PrintWriter saida, BufferedReader entrada) throws IOException {
        saida.println("ENCERRAR");

        String resposta = entrada.readLine();
        if (resposta != null) {
            System.out.println("Servidor: " + resposta);
        }
    }

    private static void sair(PrintWriter saida, BufferedReader entrada) throws IOException {
        saida.println("SAIR");

        String resposta = entrada.readLine();
        if (resposta != null) {
            System.out.println("Servidor: " + resposta);
        }
    }

    private static String formatarEstado(String resposta) {
        if (resposta == null || !resposta.startsWith("ESTADO|")) {
            return resposta;
        }

        String[] partes = resposta.split("\\|");

        if (partes.length < 2) {
            return resposta;
        }

        if ("SEM_ITEM".equalsIgnoreCase(partes[1])) {
            return "Nenhum item cadastrado no momento.";
        }

        if (partes.length >= 5) {
            return "Status: " + partes[1]
                    + " | Item: " + partes[2]
                    + " | Valor atual: R$ " + partes[3]
                    + " | Maior lance por: " + partes[4];
        }

        return resposta;
    }
}