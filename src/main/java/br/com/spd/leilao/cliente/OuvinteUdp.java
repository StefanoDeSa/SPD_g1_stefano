package br.com.spd.leilao.cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class OuvinteUdp implements Runnable {
    private final int portaUdp;
    private volatile boolean ativo;
    private DatagramSocket socketUdp;

    public OuvinteUdp(int portaUdp) {
        this.portaUdp = portaUdp;
        this.ativo = true;
    }

    @Override
    public void run() {
        try {
            socketUdp = new DatagramSocket(portaUdp);
            System.out.println("Ouvinte UDP iniciado na porta " + portaUdp);

            while (ativo) {
                byte[] buffer = new byte[1024];
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                socketUdp.receive(pacote);

                String mensagem = new String(
                        pacote.getData(),
                        0,
                        pacote.getLength(),
                        StandardCharsets.UTF_8
                );

                exibirMensagemFormatada(mensagem);
            }
        } catch (Exception e) {
            if (ativo) {
                System.out.println("Erro no ouvinte UDP: " + e.getMessage());
            }
        } finally {
            encerrar();
        }
    }

    private void exibirMensagemFormatada(String mensagem) {
        String[] partes = mensagem.split("\\|");

        if (partes.length == 0) {
            System.out.println("\n[UDP] Mensagem recebida: " + mensagem);
            return;
        }

        String tipo = partes[0].trim().toUpperCase();

        switch (tipo) {
            case "ITEM_CADASTRADO":
                if (partes.length >= 3) {
                    System.out.println("\n[ATUALIZAÇÃO] Item cadastrado: " + partes[1] + " | Valor inicial: " + partes[2]);
                } else {
                    System.out.println("\n[ATUALIZAÇÃO] " + mensagem);
                }
                break;

            case "NOVO_LANCE":
                if (partes.length >= 3) {
                    System.out.println("\n[ATUALIZAÇÃO] Novo lance de " + partes[1] + ": R$ " + partes[2]);
                } else {
                    System.out.println("\n[ATUALIZAÇÃO] " + mensagem);
                }
                break;

            case "LEILAO_ENCERRADO":
                if (partes.length >= 3) {
                    if ("SEM_VENCEDOR".equalsIgnoreCase(partes[1])) {
                        System.out.println("\n[ATUALIZAÇÃO] Leilão encerrado sem vencedor.");
                    } else {
                        System.out.println("\n[ATUALIZAÇÃO] Leilão encerrado. Vencedor: " + partes[1] + " | Valor final: R$ " + partes[2]);
                    }
                } else {
                    System.out.println("\n[ATUALIZAÇÃO] " + mensagem);
                }
                break;

            default:
                System.out.println("\n[ATUALIZAÇÃO] " + mensagem);
                break;
        }

        System.out.print("> ");
    }

    public void encerrar() {
        ativo = false;

        if (socketUdp != null && !socketUdp.isClosed()) {
            socketUdp.close();
        }
    }
}